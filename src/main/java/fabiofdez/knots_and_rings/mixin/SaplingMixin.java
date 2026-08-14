//~ has_interaction_result
//~ uses_tree_grower

package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fabiofdez.knots_and_rings.block.state.BlockPosOffset;
import fabiofdez.knots_and_rings.feature.GrowingSapling.VisualShape;
import fabiofdez.knots_and_rings.feature.SaplingShape;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Properties;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Stage;
import fabiofdez.knots_and_rings.compat.ItemDamage;
import fabiofdez.knots_and_rings.block.state.SaplingType;
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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.chunk.ChunkGenerator;
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
    if (!shape.isEmpty()) pos = shape.src();
    state = level.getBlockState(pos);
    stateRef.set(state);
    posRef.set(pos);

    Stage stage = GrowingSapling.growthStage(state);
    Stage finalStage = GrowingSapling.markedGiant(state) ? Stage.GIANT : Stage.TALL_SAPLING;

    return stage.lessThan(finalStage);
  }

  @Redirect(method = "advanceTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
  protected boolean knots_and_rings$advanceGrowthStage(ServerLevel level, BlockPos pos, BlockState state, int i) {
    if (!GrowingSapling.isGrowingSapling(state)) { // original
      return level.setBlock(pos, state.cycle(STAGE), 260);
    }

    // TODO: resolve shape dynamically/data-driven from sapling type?
    SaplingShape shape = GrowingSapling.resolveTreeShape(state, pos);
    if (shape.layout() == SaplingShape.Layout.SINGLETON) {
      shape = GrowingSapling.findShapeForSapling(state, level, pos);
    }

    if (shape.isEmpty()) return false;

    final BlockPos treeRootPos = shape.src();
    final SaplingShape.Layout layout = shape.layout();
    final boolean isNewShape = layout != GrowingSapling.treeShape(state);

    final Map<BlockPos, BlockState> topsBuilder = new HashMap<>();
    final AtomicBoolean isDoubleSapling = new AtomicBoolean();

    final Map<BlockPos, BlockState> baseBuilder = new HashMap<>();
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
          : GrowingSapling.absorbSapling(saplingState, layout, offset.reverse());

      if (isTreeRoot && GrowingSapling.isDoubleSapling(finalState)) isDoubleSapling.set(true);
      baseBuilder.put(saplingPos, finalState);

      BlockState aboveSapling = level.getBlockState(saplingPos.above());
      if (!aboveSapling.is(Blocks.AIR) && !aboveSapling.is(BlockTags.REPLACEABLE_BY_TREES)) {
        success.set(false);
        return;
      }

      BlockState newAboveState = finalState.setValue(Properties.HALF, DoubleBlockHalf.UPPER);
      topsBuilder.put(saplingPos.above(), GrowingSapling.convertToSapling(newAboveState));
    });
    if (!success.get()) {
      if (baseBuilder.size() < shape.allPositions().size()) {
        destroy(level, treeRootPos, level.getBlockState(treeRootPos));
      }
      return false;
    }

    if (isDoubleSapling.get()) {
      baseBuilder.replaceAll((p, oldState) -> GrowingSapling.convertToStem(oldState));
      topsBuilder.forEach(level::setBlockAndUpdate);
    } else {
      topsBuilder.keySet().forEach((p) -> {
        BlockState currentState = level.getBlockState(p);
        if (!GrowingSapling.isGrowingSapling(currentState)) return;
        currentState.getBlock().destroy(level, p, currentState);
      });
    }

    baseBuilder.forEach(level::setBlockAndUpdate);
    return true;
  }

  @Redirect(method = "advanceTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/grower/TreeGrower;growTree(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Z"))
  protected boolean knots_and_rings$growTree(TreeGrower treeGrower, ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, RandomSource random) {
    if (!GrowingSapling.isGrowingSapling(state)) {
      return treeGrower.growTree(level, generator, pos, state, random);
    }

    Map<BlockPos, BlockState> oldSaplingTops = Map.of();
    Map<BlockPos, BlockState> oldSaplingStems = Map.of();

    SaplingType type = SaplingType.of(state.getBlock());
    if (type == SaplingType.NONE) type = SaplingType.ofStem(state.getBlock());
    if (type == SaplingType.NONE) return false;

    BlockState defaultSapling = type.sapling().defaultBlockState();
    SaplingShape shape = GrowingSapling.resolveTreeShape(state, pos);
    if (!shape.isEmpty()) {
      Set<BlockPos> stemSpots = shape.allPositions();
      Set<BlockPos> topSpots = stemSpots.stream().map(BlockPos::above).collect(Collectors.toSet());
      oldSaplingTops = topSpots.stream().collect(Collectors.toMap(Function.identity(), level::getBlockState));
      oldSaplingStems = stemSpots.stream().collect(Collectors.toMap(Function.identity(), level::getBlockState));
      pos = shape.src();

      BlockState air = Blocks.AIR.defaultBlockState();
      topSpots.forEach((p) -> level.setBlock(p, air, 260));
      stemSpots.forEach((p) -> level.setBlock(p, defaultSapling, 260));
    }

    boolean success = treeGrower.growTree(level, generator, pos, defaultSapling, random);
    if (!success && !oldSaplingTops.isEmpty()) {
      oldSaplingTops.forEach((p, oldState) -> level.setBlock(p, oldState, 260));
      oldSaplingStems.forEach((p, oldState) -> level.setBlock(p, oldState, 260));
    }

    return success;
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

    boolean giantStage = GrowingSapling.growthStage(state) == Stage.GIANT;
    boolean isUpper = GrowingSapling.half(state) == DoubleBlockHalf.UPPER;
    if (giantStage || isUpper) {
      Direction toOtherHalf = isUpper ? Direction.DOWN : Direction.UP;
      BlockState otherHalfState = level.getBlockState(pos.relative(toOtherHalf));
      if (!GrowingSapling.partsOfSameSapling(state, otherHalfState)) return false;

      if (isUpper) return true;
    }

    if (canSurvive && GrowingSapling.markedGiant(state)) {
      return GrowingSapling.resolveTreeShape(state, pos)
          .neighbors()
          .stream()
          .map(level::getBlockState)
          .allMatch((sapling) -> GrowingSapling.partsOfSameSapling(state, sapling));
    }

    if (canSurvive && GrowingSapling.growthStage(state) == Stage.HIDDEN) {
      BlockPosOffset offset = GrowingSapling.offsetToRoot(state);
      if (offset == BlockPosOffset.SELF || offset == BlockPosOffset.NONE) return false;

      BlockState treeRoot = level.getBlockState(offset.from(pos));
      return GrowingSapling.partsOfSameSapling(state, treeRoot);
    }

    return canSurvive;
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
    return GrowingSapling.growthStage(state).lessThan(Stage.TALL_SAPLING);
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
}
