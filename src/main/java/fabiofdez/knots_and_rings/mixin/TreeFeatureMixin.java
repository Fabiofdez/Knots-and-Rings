package fabiofdez.knots_and_rings.mixin;

import fabiofdez.knots_and_rings.ModGameRules;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Properties;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Stage;
import fabiofdez.knots_and_rings.feature.LivingWoodBlock;
import fabiofdez.knots_and_rings.feature.SaplingShape;
import fabiofdez.knots_and_rings.feature.SaplingShape.Layout;
import fabiofdez.knots_and_rings.feature.SaplingType;
import fabiofdez.knots_and_rings.feature.TreeGenContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Brightness;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static fabiofdez.knots_and_rings.block.state.BlockPosOffset.MemberSet.CARDINAL;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

@Mixin(TreeFeature.class)
public class TreeFeatureMixin {

  @Unique
  private static final int MAX_SKY_DIMMING = 3;

  // TODO: make configurable?
  @Unique
  private static final Supplier<Boolean> GENERATE_SPROUTS = () -> true;
  @Unique
  private static final Supplier<Integer> SAPLING_GENERATION_CHANCE = () -> 6;
  @Unique
  private static final Supplier<Integer> MAX_NUM_SAPLING_ATTEMPTS = () -> 10;
  @Unique
  private static final Supplier<Integer> MAX_NUM_SAPLINGS = () -> 4;

  @Inject(method = "place", at = @At("RETURN"))
  private void knots_and_rings$placeSaplingsNearTree(FeaturePlaceContext<TreeConfiguration> ctx, CallbackInfoReturnable<Boolean> cir) {
    if (!cir.getReturnValue()) return;

    WorldGenLevel level = ctx.level();
    RandomSource random = ctx.random();
    if (random.nextInt(SAPLING_GENERATION_CHANCE.get()) != 0) return;

    //~ if >= 1.21.11 '.get();' -> ';'
    boolean generateSaplings = level.getLevel().getGameRules().get(ModGameRules.GENERATE_SAPLINGS);
    if (!generateSaplings) return;

    BlockPos treeOrigin = ctx.origin();
    BlockState originState = level.getBlockState(treeOrigin);
    if (!isVertical(originState) || !TreeGenContext.isNewTree(level.getLevel(), treeOrigin)) return;

    Set<BlockPos> treeBase = resolveTreeBase(level, treeOrigin);
    Optional<BoundingBox> baseBounds = BoundingBox.encapsulatingPositions(treeBase);
    treeOrigin = baseBounds.map(BoundingBox::getCenter).orElse(treeOrigin);
    SaplingType type = SaplingType.ofWood(getWoodType(level, treeOrigin, treeBase));
    if (type == SaplingType.NONE) return;

    VegetationBlockAccessor saplingAccessor = (VegetationBlockAccessor) type.sapling();
    Predicate<BlockPos> validSaplingSoil = (pos) -> {
      BlockState saplingSoil = level.getBlockState(pos);
      if (saplingSoil.getBlock() instanceof SaplingBlock) return false;
      return saplingAccessor.knots_and_rings$mayPlaceOn(saplingSoil, level, pos);
    };
    if (!validSaplingSoil.test(treeOrigin.below())) return;

    int treeRadius = Mth.ceil(baseBounds.map((box) -> (box.getXSpan() + box.getZSpan()) / 4F).orElse(0.5F));
    Range saplingsDist = Range.btwn(treeRadius, treeRadius + 8);
    Range numSaplings = Range.btwn(2, MAX_NUM_SAPLING_ATTEMPTS.get());

    Predicate<BlockPos> mayPlaceSapling = (pos) -> validSaplingSoil.test(pos.below());
    Set<BlockPos> saplingSpots = spotsForSaplings(level, random, treeOrigin, saplingsDist, numSaplings);
    Runnable saplingPlaceAction = () -> placeSapling(type, saplingSpots, level, random, mayPlaceSapling);

    TreeGenContext.placeWhenDecorated(level.getLevel(), treeOrigin, saplingPlaceAction);
  }

  @Unique
  private static Set<BlockPos> resolveTreeBase(WorldGenLevel level, BlockPos origin) {
    Set<BlockPos> treeBase = new HashSet<>();

    Queue<BlockPos> toVisit = new ArrayDeque<>();
    toVisit.add(origin);

    while (!toVisit.isEmpty()) {
      BlockPos cur = toVisit.remove();
      TreeGenContext.markVisited(level.getLevel(), cur);

      Plane.HORIZONTAL.forEach((dir) -> {
        BlockPos neighbor = cur.relative(dir);
        if (treeBase.contains(neighbor)) return;

        BlockState neighborState = level.getBlockState(neighbor);
        if (!isVertical(neighborState)) return;

        toVisit.add(neighbor);
      });

      treeBase.add(cur);
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
    return state.getValue(BlockStateProperties.AXIS) == Axis.Y;
  }

  @Unique
  private static Set<BlockPos> spotsForSaplings(WorldGenLevel level, RandomSource random, BlockPos treeOrigin, Range saplingsDistance, Range numSaplings) {
    return Collections
        .nCopies(numSaplings.next(random), treeOrigin)
        .stream()
        .map(saplingsDistance.nearby(random))
        .filter(level::ensureCanWrite)
        .map((pos) -> level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos))
        .collect(Collectors.toSet());
  }

  @Unique
  private static SaplingShape findSpaceForSapling(SaplingType type, BlockGetter level, BlockPos root, Layout largestShape, Stage stage) {
    Predicate<BlockPos> isReplaceable = (pos) -> isReplaceableBySapling(level.getBlockState(pos));
    Predicate<BlockPos> validSpot = (pos) -> {
      if (!isReplaceable.test(pos)) return false;
      if (stage == Stage.SPROUT) return isReplaceable.test(pos.above());

      BlockPos topHalf = pos.above();
      return isReplaceable.test(topHalf) && isReplaceable.test(topHalf.above());
    };

    for (Layout layout : Layout.values()) { // TODO: restrict to type's possible shapes
      if (layout.compareTo(largestShape) < 0) continue;

      SaplingShape inPlace = layout.spaceAvailableAt(root, validSpot);
      if (inPlace.isEmpty()) continue;

      if (stage != Stage.SPROUT) {
        boolean spaceAround = inPlace.perimeter(CARDINAL).stream().allMatch((pos) -> {
          if (!isReplaceable.test(pos)) return false;
          return isReplaceable.test(pos.above());
        });

        if (!spaceAround) continue;
      }

      return inPlace;
    }

    return SaplingShape.NO_SHAPE;
  }

  @Unique
  private static void growSapling(SaplingType type, SaplingShape shape, Stage stage, WorldGenLevel level, RandomSource random) {
    Consumer<BlockPos> checkForSnow = (pos) -> {
      BlockState soil = level.getBlockState(pos.below());
      if (level.getBlockState(pos).is(BlockTags.SNOW) && soil.hasProperty(BlockStateProperties.SNOWY)) {
        soil = soil.setValue(BlockStateProperties.SNOWY, false);
        level.setBlock(pos.below(), soil, 3);
      }
    };

    BlockPos root = shape.root();
    if (stage == Stage.SPROUT) {
      BlockState sapling = type
          .placedSapling(level, root)
          .setValue(Properties.GROWTH_STAGE, stage)
          .setValue(Properties.PRUNED, true);

      checkForSnow.accept(root);
      level.setBlock(root, sapling, 3);
      return;
    }

    shape.forEachOffset((offset, pos) -> {
      BlockState stem = type
          .placedStem(level, pos)
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

      checkForSnow.accept(pos);
      level.setBlock(pos, stem, 3);
      level.setBlock(pos.above(), top, 3);
    });
  }

  @Unique
  private static void placeSapling(SaplingType type, Set<BlockPos> saplingSpots, WorldGenLevel level, RandomSource random, Predicate<BlockPos> mayPlaceSapling) {
    AtomicInteger numPlaced = new AtomicInteger();

    saplingSpots.forEach((origin) -> {
      if (numPlaced.get() >= MAX_NUM_SAPLINGS.get()) return;

      Stage stage = random.nextInt(3) != 0 ? Stage.GIANT : Stage.TALL_SAPLING;
      if (GENERATE_SPROUTS.get() && random.nextBoolean()) stage = Stage.SPROUT;

      Layout largestShape = switch (stage) {
        case SPROUT -> Layout.SINGLETON;
        case GIANT -> Layout.SQUARE_SM;
        default -> random.nextBoolean() ? Layout.SQUARE_SM : Layout.SINGLETON;
      };

      SaplingShape shape = findSpaceForSapling(type, level, origin, largestShape, stage);
      if (shape == SaplingShape.NO_SHAPE) return;

      if (stage == Stage.GIANT && shape.layout() != Layout.SQUARE_SM) {
        stage = Stage.TALL_SAPLING;
      }

      if (!shape.allPositions().stream().allMatch(mayPlaceSapling)) return;

      growSapling(type, shape, stage, level, random);
      numPlaced.getAndIncrement();

      Runnable checkSkyLight = () -> checkSaplingLight(type, shape, level, random);
      TreeGenContext.checkWhenLit(level.getLevel(), shape.root(), checkSkyLight);
    });
  }

  @Unique
  private static void checkSaplingLight(SaplingType type, SaplingShape shape, WorldGenLevel level, RandomSource random) {
    BlockPos saplingRoot = shape.root();
    Set<BlockPos> saplingParts = shape.allPositions();

    Predicate<BlockPos> validSaplingPart = (pos) -> level.getBlockState(pos).getBlock() instanceof SaplingBlock;
    Runnable uprootSapling = () -> shape.forEach((pos) -> {
      level.destroyBlock(pos, false);
      if (validSaplingPart.test(pos.above())) level.destroyBlock(pos.above(), false);
    });

    Function<BlockPos, Integer> skyLight = (pos) -> level.getBrightness(LightLayer.SKY, pos);
    Function<BlockPos, Integer> skyDimming = (pos) -> Brightness.FULL_BRIGHT.sky() - skyLight.apply(pos);
    BlockPos minLightSpot = saplingParts.stream().min(comparingInt(skyLight::apply)).orElse(saplingRoot);

    int extraSpotDimming = skyDimming.apply(minLightSpot) - MAX_SKY_DIMMING;
    boolean willUproot = extraSpotDimming > 0 && random.nextInt(2 * extraSpotDimming) != 0;
    boolean validSapling = saplingParts.stream().allMatch(validSaplingPart);
    if (!willUproot || !validSapling) return;

    uprootSapling.run();
    if (random.nextInt(3) != 0) return;

    BlockState drySapling = type
        .placedSapling(level, saplingRoot)
        .setValue(Properties.GROWTH_STAGE, Stage.DECAYING)
        .setValue(Properties.PRUNED, true);

    level.setBlock(saplingRoot, drySapling, 3);
  }

  @Unique
  private static boolean isReplaceableBySapling(BlockState state) {
    if (state.is(Blocks.AIR)) return true;
    if (state.is(BlockTags.LEAVES)) return false;

    return state.is(BlockTags.REPLACEABLE_BY_TREES) || state.is(BlockTags.FLOWERS);
  }

  private record Range(int min, int max) {

    static Range btwn(int min, int max) {
      return new Range(min, max);
    }

    int next(RandomSource random) {
      return random.nextIntBetweenInclusive(min, max);
    }

    UnaryOperator<BlockPos> nearby(RandomSource random) {
      float dist = min + (max - min) * random.nextFloat();
      float angleRadians = random.nextFloat() * 2 * Mth.PI;
      int xOffset = Math.round(dist * Mth.cos(angleRadians));
      int zOffset = Math.round(dist * Mth.sin(angleRadians));

      return (pos) -> pos.relative(Axis.X, xOffset).relative(Axis.Z, zOffset);
    }
  }
}
