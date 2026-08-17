//~ has_interaction_result
//~ uses_tree_grower

package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fabiofdez.knots_and_rings.feature.GrowingSapling.VisualShape;
import fabiofdez.knots_and_rings.feature.SaplingShape;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Properties;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Stage;
import fabiofdez.knots_and_rings.compat.ItemDamage;
import fabiofdez.knots_and_rings.block.state.SaplingType;
import fabiofdez.knots_and_rings.util.BlockFluidContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(SaplingBlock.class)
public abstract class SaplingMixin extends VegetationBlockMixin {

  @Shadow
  @Final
  public static IntegerProperty STAGE;

  @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/VegetationBlock;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"))
  private static BlockBehaviour.Properties knots_and_rings$initSaplingBlock(BlockBehaviour.Properties properties) {
    return properties.dynamicShape().offsetType(BlockBehaviour.OffsetType.XZ);
  }

  @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SaplingBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
  protected BlockState knots_and_rings$registerSaplingProperties(BlockState state) {
    return GrowingSapling.registerDefaultState(state);
  }

  @Inject(method = "createBlockStateDefinition", at = @At("RETURN"))
  protected void knots_and_rings$addGrowthStageToBlockStateDef(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
    GrowingSapling.initBlockStateDef(builder);
  }

  @Definition(id = "blockState", local = @Local(type = BlockState.class, argsOnly = true))
  @Definition(id = "getValue", method = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;")
  @Definition(id = "STAGE", field = "Lnet/minecraft/world/level/block/SaplingBlock;STAGE:Lnet/minecraft/world/level/block/state/properties/IntegerProperty;")
  @Definition(id = "Integer", type = Integer.class)
  @Expression("(Integer) blockState.getValue(STAGE) == 0")
  @ModifyExpressionValue(method = "advanceTree", at = @At("MIXINEXTRAS:EXPRESSION"))
  protected boolean knots_and_rings$immatureSapling(boolean original, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) LocalRef<BlockState> stateRef, @Local(argsOnly = true) LocalRef<BlockPos> posRef) {
    if (!GrowingSapling.isGrowingSapling(stateRef.get())) return original;

    BlockPos pos = posRef.get();
    BlockState state = stateRef.get();
    if (GrowingSapling.half(state) == DoubleBlockHalf.UPPER) pos = pos.below();

    SaplingShape shape = GrowingSapling.resolveTreeShape(state, pos);
    if (!shape.isEmpty()) pos = shape.root();
    state = level.getBlockState(pos);
    stateRef.set(state);
    posRef.set(pos);

    return GrowingSapling.isImmature(state);
  }

  @Redirect(method = "advanceTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
  protected boolean knots_and_rings$advanceGrowthStage(ServerLevel level, BlockPos pos, BlockState state, int i) {
    if (!GrowingSapling.isGrowingSapling(state)) { // original
      return level.setBlock(pos, state.cycle(STAGE), 260);
    }

    // TODO: resolve shape dynamically/data-driven from sapling type?
    SaplingShape shape = GrowingSapling.resolveTreeShape(state, pos);
    SaplingShape.Layout oldLayout = shape.layout();
    Stage saplingStage = GrowingSapling.growthStage(state);
    BlockPos oldTreeRootPos = shape.root();

    if (oldLayout == SaplingShape.Layout.SINGLETON && saplingStage.LEQ(Stage.SAPLING)) {
      shape = GrowingSapling.findShapeForSapling(state, level, pos);
    }

    if (shape.isEmpty()) return false;

    final BlockPos treeRootPos = shape.root();
    final SaplingShape.Layout layout = shape.layout();
    final boolean isNewShape = layout != oldLayout;
    if (!isNewShape && treeRootPos == oldTreeRootPos) {
      BlockState treeRoot = level.getBlockState(oldTreeRootPos);
      if (!GrowingSapling.isImmature(treeRoot)) return false;
    }

    final Map<BlockPos, BlockState> topsBuilder = new HashMap<>();
    final Map<BlockPos, BlockState> baseBuilder = new HashMap<>();
    final AtomicBoolean isDoubleSapling = new AtomicBoolean();
    final AtomicBoolean spaceAbove = new AtomicBoolean(true);
    final AtomicBoolean success = new AtomicBoolean(true);

    shape.forEachOffset((offset, saplingPos) -> {
      if (!success.get()) return;

      BlockState saplingState = level.getBlockState(saplingPos);
      if (!GrowingSapling.partsOfSameSapling(saplingState, state)) {
        success.set(false);
        return;
      }

      boolean isTreeRoot = saplingPos == treeRootPos;
      BlockState finalState = isTreeRoot
          ? GrowingSapling.makeSaplingRoot(saplingState, layout, !isNewShape)
          : GrowingSapling.absorbSapling(saplingState, layout, offset.reverse(), level.getRandom());

      if (isTreeRoot && GrowingSapling.isDoubleSapling(finalState)) isDoubleSapling.set(true);
      baseBuilder.put(saplingPos, finalState);

      if (!spaceAbove.get()) return;

      BlockState aboveSapling = level.getBlockState(saplingPos.above());
      boolean partOfOwnTreeAbove = GrowingSapling.partsOfSameSapling(finalState, aboveSapling);
      boolean replaceableBlockAbove = aboveSapling.is(BlockTags.REPLACEABLE_BY_TREES);
      if (!aboveSapling.is(Blocks.AIR) && !partOfOwnTreeAbove && !replaceableBlockAbove) {
        spaceAbove.set(false);
        topsBuilder.clear();
        return;
      }

      BlockState newAboveState = finalState.setValue(Properties.HALF, DoubleBlockHalf.UPPER);
      if (!isTreeRoot) {
        newAboveState = newAboveState.setValue(Properties.GROWTH_STAGE, Stage.HIDDEN);
      }

      topsBuilder.put(saplingPos.above(), GrowingSapling.convertToSapling(newAboveState));
    });
    if (!success.get()) {
      if (baseBuilder.size() < shape.allPositions().size()) level.destroyBlock(treeRootPos, true);
      return false;
    }

    if (isDoubleSapling.get()) {
      if (!spaceAbove.get()) {
        level.destroyBlock(treeRootPos, true);
        return false;
      }

      baseBuilder.replaceAll((p, oldState) -> GrowingSapling.convertToStem(oldState));
      topsBuilder.forEach((p, newState) -> setWithFluid(newState, level, p));
    } else {
      topsBuilder.keySet().forEach((p) -> {
        if (!GrowingSapling.isGrowingSapling(level.getBlockState(p))) return;
        level.destroyBlock(p, true);
      });
    }

    baseBuilder.forEach((p, newState) -> setWithFluid(newState, level, p));
    return true;
  }

  @Redirect(method = "advanceTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/grower/TreeGrower;growTree(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Z"))
  protected boolean knots_and_rings$growTree(TreeGrower treeGrower, ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, RandomSource random) {
    if (!GrowingSapling.isGrowingSapling(state)) {
      return treeGrower.growTree(level, generator, pos, state, random);
    }

    SaplingType type = SaplingType.ofStem(state.getBlock());
    if (type == SaplingType.NONE) type = SaplingType.of(state.getBlock());
    if (type == SaplingType.NONE) return false;

    final Block defaultSapling = type.sapling();
    final BlockState defaultSaplingState = defaultSapling.defaultBlockState();
    BlockFluidContext saplingFluidCtx = getBlockWithFluid(defaultSaplingState, level);
    BlockFluidContext emptyFluidCtx = getEmptyWithFluid(level);

    SaplingShape shape = GrowingSapling.resolveTreeShape(state, pos);
    if (shape.isEmpty()) return false;

    pos = shape.root();
    Set<BlockPos> stemSpots = shape.allPositions();
    Set<BlockPos> topSpots = stemSpots.stream().map(BlockPos::above).collect(Collectors.toSet());
    Set<BlockPos> perimeterSpots = shape.perimeter(level);

    Map<BlockPos, BlockState> oldSaplingTops = mapSetToValues(topSpots, level::getBlockState);
    Map<BlockPos, BlockState> oldSaplingStems = mapSetToValues(stemSpots, level::getBlockState);
    Map<BlockPos, BlockState> oldPerimeterBlocks = mapSetToValues(perimeterSpots, level::getBlockState);

    Map<BlockPos, BlockState> emptySaplingTops = mapSetToValues(topSpots, emptyFluidCtx::getAt);
    Map<BlockPos, BlockState> defaultStemSaplings = mapSetToValues(stemSpots, saplingFluidCtx::getAt);

    emptySaplingTops.forEach((p, placeholder) -> level.setBlock(p, placeholder, 16));
    defaultStemSaplings.forEach((p, placeholder) -> level.setBlock(p, placeholder, 16));
    perimeterSpots.forEach((p) -> level.setBlock(p, emptyFluidCtx.getAt(p), 16));

    treeGrower.growTree(level, generator, pos, defaultSaplingState, random);
    oldPerimeterBlocks.forEach((p, oldState) -> {
      if (level.getBlockState(p) != getAsEmptyBlock(oldState)) return;
      level.setBlock(p, oldState, 16);
    });

    Predicate<BlockPos> notDefaultSapling = (p) -> !level.getBlockState(p).is(defaultSapling);
    boolean success = oldSaplingStems.keySet().stream().anyMatch(notDefaultSapling);

    if (success) {
      emptySaplingTops.forEach((p, empty) -> {
        if (level.getBlockState(p) != empty) return;
        level.setBlock(p, saplingFluidCtx.getAt(p), 16);
        level.setBlockAndUpdate(p, empty);
      });
      defaultStemSaplings.forEach((p, sapling) -> {
        if (level.getBlockState(p) != sapling) return;
        level.setBlockAndUpdate(p, emptyFluidCtx.getAt(p));
      });

      return true;
    }

    oldSaplingTops.forEach((p, oldState) -> level.setBlock(p, oldState, 16));
    oldSaplingStems.forEach((p, oldState) -> level.setBlock(p, oldState, 16));
    return false;
  }

  @Definition(id = "randomSource", local = @Local(type = RandomSource.class, argsOnly = true))
  @Definition(id = "nextInt", method = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
  @Expression("randomSource.nextInt(7) == 0")
  @ModifyExpressionValue(method = "randomTick", at = @At("MIXINEXTRAS:EXPRESSION"))
  protected boolean knots_and_rings$conditionalRandomTick(boolean original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) RandomSource random) {
    if (!GrowingSapling.isGrowingSapling(state)) return original;
    return GrowingSapling.canRandomTick(state, random);
  }

  @ModifyReturnValue(method = "isBonemealSuccess", at = @At("RETURN"))
  protected boolean knots_and_rings$isBonemealSuccess(boolean original, @Local(argsOnly = true) BlockState state) {
    if (GrowingSapling.isGrowingSapling(state) && GrowingSapling.isPruned(state)) return true;
    return original;
  }

  @Redirect(method = "performBonemeal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SaplingBlock;advanceTree(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)V"))
  protected void knots_and_rings$growWithBonemeal(SaplingBlock sapling, ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
    if (!GrowingSapling.isGrowingSapling(state) || !GrowingSapling.isPruned(state)) { // original
      sapling.advanceTree(level, pos, state, random);
      return;
    }

    setSaplingPruned(state, level, pos, false);
  }

  @Override
  protected InteractionResult knots_and_rings$useItemOn(InteractionResult result, /*? if >= 1.21 >> 'BlockState' */ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
    if (!GrowingSapling.isGrowingSapling(state)) return result;

    //? < 1.21
    //ItemStack stack = player.getItemInHand(hand);

    if (stack.is(Items.SHEARS)) {
      return pruneSapling(stack, state, level, pos, player, hand);
    }

    return result;
  }

  @Override
  protected boolean knots_and_rings$canSurvive(boolean canSurvive, BlockState state, LevelReader level, BlockPos pos) {
    if (!GrowingSapling.isGrowingSapling(state)) return canSurvive;

    Stage saplingStage = GrowingSapling.growthStage(state);
    boolean isUpper = GrowingSapling.half(state) == DoubleBlockHalf.UPPER;
    if (saplingStage == Stage.GIANT || isUpper) {
      Direction toOtherHalf = isUpper ? Direction.DOWN : Direction.UP;
      BlockState otherHalfState = level.getBlockState(pos.relative(toOtherHalf));
      if (!GrowingSapling.partsOfSameSapling(state, otherHalfState)) return false;

      if (isUpper) return true;
    }

    if (!canSurvive) return false;

    if (!GrowingSapling.markedGiant(state)) {
      if (GrowingSapling.treeShape(state) == SaplingShape.Layout.SINGLETON) {
        return saplingStage != Stage.HIDDEN;
      }

      BlockState treeRoot = level.getBlockState(GrowingSapling.offsetToRoot(state).from(pos));
      return GrowingSapling.partsOfSameSapling(state, treeRoot);
    }

    return GrowingSapling
        .resolveTreeShape(state, pos)
        .neighbors()
        .stream()
        .map(level::getBlockState)
        .allMatch((sapling) -> GrowingSapling.partsOfSameSapling(state, sapling));
  }

  @Override
  protected void knots_and_rings$destroy(LevelAccessor level, BlockPos pos, BlockState state, CallbackInfo ci) {
    if (!GrowingSapling.isGrowingSapling(state)) return;

    if (GrowingSapling.half(state) == DoubleBlockHalf.UPPER) pos = pos.below();
    BlockPos treeRootPos = GrowingSapling.offsetToRoot(state).from(pos);
    SaplingShape shape = GrowingSapling.treeShape(state).inPlace(treeRootPos);
    if (shape.isEmpty()) return;

    shape.forEach((neighborPos) -> {
      BlockState neighborState = level.getBlockState(neighborPos);
      if (!GrowingSapling.isGrowingSapling(neighborState)) return;

      BlockPos neighborTreeRootPos = GrowingSapling.offsetToRoot(neighborState).from(neighborPos);
      if (neighborTreeRootPos != treeRootPos) return;

      level.destroyBlock(neighborPos, true);
      BlockState aboveNeighborState = level.getBlockState(neighborPos.above());
      if (GrowingSapling.isGrowingSapling(aboveNeighborState)) {
        level.destroyBlock(neighborPos.above(), true);
      }
    });
  }

  @ModifyReturnValue(method = "getShape", at = @At("RETURN"))
  protected VoxelShape knots_and_rings$getShape(VoxelShape original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos) {
    if (!GrowingSapling.isGrowingSapling(state)) return original;
    return GrowingSapling.getInteractShape(state, level, pos);
  }

  @Override
  protected VoxelShape knots_and_rings$getInteractionShape(VoxelShape original, BlockState state, BlockGetter level, BlockPos pos) {
    return knots_and_rings$getShape(original, state, level, pos);
  }

  @Override
  protected VoxelShape knots_and_rings$getCollisionShape(VoxelShape original, BlockState state, BlockGetter level, BlockPos pos) {
    if (!GrowingSapling.isGrowingSapling(state)) return original;
    return VisualShape.COLLISION.parse(state, pos);
  }

  @Override
  protected boolean knots_and_rings$isPathfindable(boolean original, BlockState state) {
    if (!GrowingSapling.isGrowingSapling(state)) return original;
    return GrowingSapling.growthStage(state).LT(Stage.TALL_SAPLING);
  }

  @Override
  protected Vec3i knots_and_rings$setPosForSeed(Vec3i pos, BlockState state) {
    if (!GrowingSapling.isGrowingSapling(state)) return pos;
    return GrowingSapling.getPosForRandomOffset(state, pos);
  }

  @Unique
  private static InteractionResult pruneSapling(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
    if (GrowingSapling.isPruned(state)) return InteractionResult.PASS;

    if (!level.isClientSide()) {
      float pitch = 0.8F + level.getRandom().nextFloat() * 0.2F;
      level.playSound(null, pos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1F, pitch);
      level.playSound(null, pos, SoundEvents.GRASS_HIT, SoundSource.BLOCKS, 1.2F, pitch);
      if (player != null) ItemDamage.hurtAndBreak(stack, hand, player, 1);

      setSaplingPruned(state, level, pos, true);
    }

    return InteractionResult.SUCCESS;
  }

  @Unique
  private static void setSaplingPruned(BlockState state, Level level, BlockPos pos, boolean pruned) {
    if (GrowingSapling.half(state) == DoubleBlockHalf.UPPER) pos = pos.below();

    SaplingShape shape = GrowingSapling.resolveTreeShape(state, pos);
    if (shape.isEmpty()) {
      level.setBlockAndUpdate(pos, state.setValue(Properties.PRUNED, pruned));
      return;
    }

    shape.forEach((saplingPos) -> {
      BlockState saplingState = level.getBlockState(saplingPos);
      if (!GrowingSapling.isGrowingSapling(saplingState)) return;

      BlockState aboveSapling = level.getBlockState(saplingPos.above());
      if (GrowingSapling.isGrowingSapling(aboveSapling)) {
        level.setBlockAndUpdate(saplingPos.above(), aboveSapling.setValue(Properties.PRUNED, pruned));
      }

      level.setBlockAndUpdate(saplingPos, saplingState.setValue(Properties.PRUNED, pruned));
    });
  }

  @Unique
  private static <K, V> Map<K, V> mapSetToValues(Set<K> keySet, Function<K, V> valueMapping) {
    return keySet.stream().collect(Collectors.toMap(Function.identity(), valueMapping));
  }

  @Unique
  private static BlockState getAsEmptyBlock(BlockState state) {
    return state.getFluidState().createLegacyBlock();
  }

  @Unique
  private static BlockFluidContext getEmptyWithFluid(Level level) {
    return (pos) -> level.getFluidState(pos).createLegacyBlock();
  }

  @Unique
  private static BlockFluidContext getBlockWithFluid(BlockState state, Level level) {
    boolean waterloggable = state.hasProperty(BlockStateProperties.WATERLOGGED);

    return (pos) -> {
      if (!waterloggable) return state;

      boolean waterlogged = level.getFluidState(pos).is(Fluids.WATER);
      return state.setValue(BlockStateProperties.WATERLOGGED, waterlogged);
    };
  }

  @Unique
  private static void setWithFluid(BlockState state, Level level, BlockPos pos) {
    boolean waterloggable = state.hasProperty(BlockStateProperties.WATERLOGGED);
    if (waterloggable) {
      boolean waterlogged = level.getFluidState(pos).is(Fluids.WATER);
      state = state.setValue(BlockStateProperties.WATERLOGGED, waterlogged);
    }

    level.setBlockAndUpdate(pos, state);
  }
}
