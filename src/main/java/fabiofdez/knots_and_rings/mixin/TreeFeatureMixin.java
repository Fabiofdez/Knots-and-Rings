package fabiofdez.knots_and_rings.mixin;

import fabiofdez.knots_and_rings.ModGameRules;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Properties;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Stage;
import fabiofdez.knots_and_rings.feature.LivingWoodBlock;
import fabiofdez.knots_and_rings.feature.SaplingShape;
import fabiofdez.knots_and_rings.feature.SaplingShape.Layout;
import fabiofdez.knots_and_rings.feature.SaplingType;
import fabiofdez.knots_and_rings.feature.WorldGenContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Brightness;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

@Mixin(TreeFeature.class)
public class TreeFeatureMixin {

  @Inject(method = "place", at = @At("RETURN"))
  private void knots_and_rings$placeSaplingNearTree(FeaturePlaceContext<TreeConfiguration> ctx, CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValue()) return;

    RandomSource random = ctx.random();
    if (random.nextInt(8) != 0) return;

    WorldGenLevel level = ctx.level();
    boolean generateSaplings = level
        .getLevel()
        .getGameRules()/*? if < 1.21.11 { *//*.getRule*//*? } else { */.get/*? } */(ModGameRules.GENERATE_SAPLINGS/*? if neoforge && >= 1.21.11 { *//*.get()*//*? } */)
        /*? if < 1.21.11 { *//*.get()*//*? } */;
    if (!generateSaplings) return;

    BlockPos treeOrigin = ctx.origin();
    BlockState originState = level.getBlockState(treeOrigin);
    if (!isVertical(originState) || !WorldGenContext.isNewTree(level.getLevel(), treeOrigin)) return;

    Set<BlockPos> treeBase = resolveTreeBase(level, treeOrigin);
    Optional<BoundingBox> baseBounds = BoundingBox.encapsulatingPositions(treeBase);
    int treeRadius = baseBounds.map((box) -> (box.getXSpan() + box.getZSpan()) / 2).orElse(1);
    treeOrigin = baseBounds.map(BoundingBox::getCenter).orElse(treeOrigin);

    SaplingType type = SaplingType.ofWood(getWoodType(level, treeOrigin, treeBase));
    if (type == SaplingType.NONE) return;

    Range treeDist = Range.btwn(treeRadius + 2, treeRadius + 5);
    VegetationBlockAccessor saplingAccessor = (VegetationBlockAccessor) type.sapling();
    Predicate<BlockPos> validSaplingSoil = (pos) -> {
      BlockState saplingSoil = level.getBlockState(pos);
      return saplingAccessor.knots_and_rings$mayPlaceOn(saplingSoil, level, pos);
    };
    if (!validSaplingSoil.test(treeOrigin.below())) return;

    Predicate<BlockPos> mayPlaceSapling = (pos) -> validSaplingSoil.test(pos.below());
    Function<BlockPos, Integer> skyLight = (pos) -> level.getBrightness(LightLayer.SKY, pos);
    Function<BlockPos, Integer> lightDiff = (pos) -> Brightness.FULL_BRIGHT.sky() - skyLight.apply(pos);

    spotsForSaplings(level, random, treeOrigin, treeDist, Range.btwn(1, 3)).forEach((origin) -> {
      Layout maxLayout = random.nextBoolean() ? Layout.SQUARE_SM : Layout.SINGLETON;
      SaplingShape shape = findSpaceForSapling(type, level, origin, maxLayout);
      if (shape == SaplingShape.NO_SHAPE) return;

      Set<BlockPos> saplingSpots = shape.allPositions();
      if (!saplingSpots.stream().allMatch(mayPlaceSapling)) return;

      BlockPos minLightSpot = saplingSpots.stream().min(Comparator.comparingInt(skyLight::apply)).orElse(origin);

      int growthChance = (2 * lightDiff.apply(minLightSpot)) + 1;
      if (random.nextInt(growthChance) != 0) return;

      growSapling(type, shape, level, random);
    });
  }

  @Unique
  private static Set<BlockPos> resolveTreeBase(WorldGenLevel level, BlockPos origin) {
    Set<BlockPos> treeBase = new HashSet<>();

    Queue<BlockPos> toVisit = new ArrayDeque<>();
    toVisit.add(origin);

    while (!toVisit.isEmpty()) {
      BlockPos cur = toVisit.remove();
      WorldGenContext.markVisited(level.getLevel(), cur);

      Direction.Plane.HORIZONTAL.forEach((dir) -> {
        BlockPos neighbor = cur.relative(dir);
        if (treeBase.contains(neighbor)) return;

        BlockState neighborState = level.getBlockState(neighbor);
        if (!isVertical(neighborState)) return;

        toVisit.add(neighbor);
        treeBase.add(cur);
      });
    }

    return treeBase;
  }

  @Unique
  private static Block getWoodType(WorldGenLevel level, BlockPos origin, Set<BlockPos> treeBase) {
    Set<Block> woodBlockCache = new HashSet<>();
    UnaryOperator<Block> combineWoodBlocks = (block) -> {
      if (woodBlockCache.contains(block)) return block;

      return LivingWoodBlock.getAsLog(block).map((log) -> {
        woodBlockCache.add(log);
        return log;
      }).orElse(block);
    };

    return treeBase
        .stream()
        .map((pos) -> level.getBlockState(pos).getBlock())
        .collect(groupingBy(combineWoodBlocks, summingInt((block) -> 1)))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(level.getBlockState(origin).getBlock());
  }

  @Unique
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private static boolean isVertical(BlockState state) {
    if (!state.hasProperty(BlockStateProperties.AXIS)) return false;
    return state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;
  }

  @Unique
  private static Set<BlockPos> spotsForSaplings(WorldGenLevel level, RandomSource random, BlockPos treeOrigin, Range treeDistance, Range numSaplings) {
    return Collections
        .nCopies(numSaplings.next(random), treeOrigin)
        .stream()
        .map(treeDistance.nearby(random))
        .filter(level::ensureCanWrite)
        .map((pos) -> level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos))
        .collect(Collectors.toSet());
  }

  @Unique
  private static SaplingShape findSpaceForSapling(SaplingType type, BlockGetter level, BlockPos root, Layout maxLayout) {
    Predicate<BlockPos> isReplaceable = (pos) -> {
      BlockState state = level.getBlockState(pos);
      if (state.is(Blocks.AIR)) return true;
      if (state.is(BlockTags.LEAVES)) return false;

      return state.is(BlockTags.REPLACEABLE_BY_TREES);
    };

    Predicate<BlockPos> spaceAbove = (pos) -> {
      BlockPos topHalf = pos.above();
      return isReplaceable.test(topHalf) && isReplaceable.test(topHalf.above());
    };

    for (Layout layout : type.getPossibleShapes()) {
      if (layout.compareTo(maxLayout) < 0) continue;

      SaplingShape inPlace = layout.spaceAvailableAt(root, isReplaceable);
      if (inPlace.isEmpty()) continue;
      if (!inPlace.allPositions().stream().allMatch(spaceAbove)) continue;
      if (!inPlace.perimeter().stream().allMatch(isReplaceable)) continue;

      return inPlace;
    }

    return SaplingShape.NO_SHAPE;
  }

  @Unique
  private static void growSapling(SaplingType type, SaplingShape shape, WorldGenLevel level, RandomSource random) {
    boolean isStunted = random.nextBoolean();
    Stage stage = (shape.layout() == Layout.SQUARE_SM && !isStunted) ? Stage.GIANT : Stage.TALL_SAPLING;

    BlockPos root = shape.root();
    shape.forEachOffset((offset, pos) -> {
      BlockState stem = type
          .placedStem(level, root)
          .setValue(Properties.GROWTH_STAGE, stage)
          .setValue(Properties.PRUNED, true);

      boolean isRoot = pos == root;
      stem = isRoot
          ? GrowingSapling.makeSaplingRoot(stem, shape.layout(), false)
          : GrowingSapling.absorbSapling(stem, shape.layout(), offset.reverse(), random);

      if (!isRoot) stem = stem.setValue(Properties.GROWTH_STAGE, Stage.HIDDEN);
      BlockState top = GrowingSapling
          .convertToSapling(stem, level, pos.above())
          .setValue(Properties.HALF, DoubleBlockHalf.UPPER);

      BlockState soil = level.getBlockState(pos.below());
      if (level.getBlockState(pos).is(BlockTags.SNOW) && soil.hasProperty(BlockStateProperties.SNOWY)) {
        soil = soil.setValue(BlockStateProperties.SNOWY, false);
        level.setBlock(pos.below(), soil, 3);
      }

      level.setBlock(pos, stem, 3);
      level.setBlock(pos.above(), top, 3);
    });
  }

  private record Range(int min, int max) {

    static Range btwn(int min, int max) {
      return new Range(min, max);
    }

    int next(RandomSource random) {
      return random.nextIntBetweenInclusive(min, max);
    }

    UnaryOperator<BlockPos> nearby(RandomSource random) {
      return (origin) -> {
        int xOffset = next(random) * (random.nextBoolean() ? -1 : 1);
        int zOffset = next(random) * (random.nextBoolean() ? -1 : 1);

        return origin.relative(Direction.Axis.X, xOffset).relative(Direction.Axis.Z, zOffset);
      };
    }
  }
}
