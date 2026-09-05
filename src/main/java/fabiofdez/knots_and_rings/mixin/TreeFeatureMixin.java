package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Properties;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Stage;
import fabiofdez.knots_and_rings.feature.SaplingShape;
import fabiofdez.knots_and_rings.feature.SaplingShape.Layout;
import fabiofdez.knots_and_rings.feature.SaplingType;
import fabiofdez.knots_and_rings.feature.WorldGenContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(TreeFeature.class)
public class TreeFeatureMixin {

  @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;encapsulatingPositions(Ljava/lang/Iterable;)Ljava/util/Optional;"))
  private void knots_and_rings$placeSaplingNearTree(FeaturePlaceContext<TreeConfiguration> ctx, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 2) Set<BlockPos> foliage) {
    BlockPos treeOrigin = ctx.origin();
    WorldGenLevel level = ctx.level();
    ChunkAccess chunk = level.getChunk(treeOrigin);
    if (!WorldGenContext.isDecorating(level.getLevel(), chunk.getPos())) return;

    RandomSource random = ctx.random();
    if (random.nextInt(20) != 0) return;

    BlockPos foliagePos = foliage.stream().findFirst().orElse(null);
    if (foliagePos == null) return;

    Block leavesBlock = ctx.config().foliageProvider
        .getState(/*? if >= 26.1 { *//*level, *//*? } */random, foliagePos)
        .getBlock();

    SaplingType type = SaplingType.ofLeaves(leavesBlock);
    if (type == SaplingType.NONE) return;

    VegetationBlockAccessor saplingAccessor = (VegetationBlockAccessor) type.sapling();
    Predicate<BlockPos> mayPlaceSapling = (pos) -> {
      BlockPos saplingSoilPos = pos.below();
      BlockState saplingSoil = level.getBlockState(saplingSoilPos);
      return saplingAccessor.knots_and_rings$mayPlaceOn(saplingSoil, level, saplingSoilPos);
    };

    Range numSaplings = Range.between(1, 3, random);
    Range treeDistance = Range.between(2, 5, random);
    randomSpotsForSaplings(level, treeOrigin, treeDistance, numSaplings).forEach((saplingOrigin) -> {
      Layout maxLayout = random.nextBoolean() ? Layout.SQUARE_SM : Layout.SINGLETON;
      SaplingShape shape = findSpaceForSapling(level, saplingOrigin, maxLayout);
      if (shape == SaplingShape.NO_SHAPE) return;
      if (!shape.allPositions().stream().allMatch(mayPlaceSapling)) return;

      growSapling(type, shape, level, random);
    });
  }

  @Unique
  private static Set<BlockPos> randomSpotsForSaplings(WorldGenLevel level, BlockPos treeOrigin, Range treeDistance, Range numSaplings) {
    return Collections
        .nCopies(numSaplings.generate(), treeOrigin)
        .stream()
        .map(treeDistance::from)
        .filter(level::ensureCanWrite)
        .map((pos) -> level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos))
        .collect(Collectors.toSet());
  }

  @Unique
  private static SaplingShape findSpaceForSapling(BlockGetter level, BlockPos pos, Layout maxLayout) {
    Predicate<BlockPos> isReplaceable = (p) -> level.getBlockState(p).is(BlockTags.REPLACEABLE);
    Predicate<BlockPos> spaceAbove = (p) -> isReplaceable.test(p.above()) && isReplaceable.test(p.above().above());

    for (Layout layout : Layout.values()) {
      if (layout.compareTo(maxLayout) < 0) continue;

      SaplingShape inPlace = layout.spaceAvailableAt(pos, isReplaceable);
      if (inPlace.isEmpty()) continue;
      if (!inPlace.allPositions().stream().allMatch(spaceAbove)) continue;

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

  private record Range(int min, int max, RandomSource random) {

    static Range between(int min, int max, RandomSource random) {
      return new Range(min, max, random);
    }

    int generate() {
      return random.nextIntBetweenInclusive(min, max);
    }

    BlockPos from(BlockPos origin) {
      int xOffset = generate() * (random.nextBoolean() ? -1 : 1);
      int zOffset = generate() * (random.nextBoolean() ? -1 : 1);

      return origin.relative(Direction.Axis.X, xOffset).relative(Direction.Axis.Z, zOffset);
    }
  }
}
