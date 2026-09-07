//~ has_interaction_result

package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.ModSounds;
import fabiofdez.knots_and_rings.block.state.LogSide;
import fabiofdez.knots_and_rings.compat.ItemDamage;
import fabiofdez.knots_and_rings.feature.LivingWoodBlock;
import fabiofdez.knots_and_rings.feature.LivingWoodCluster;
import fabiofdez.knots_and_rings.feature.LogConnectivityCache;
import fabiofdez.knots_and_rings.compat.Particles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(RotatedPillarBlock.class)
public abstract class RotatedPillarBlockMixin extends BlockMixin implements BonemealableBlock {

  @Unique
  private static final BooleanProperty ALIVE;
  @Unique
  private static final BooleanProperty IS_TRUNK;
  @Unique
  private static final BooleanProperty SINGLETON;
  @Unique
  private static final EnumProperty<LogSide.Mapping> SIDES;

  @Unique
  private static final Class<?>[] INVALID_SUBCLASSES = new Class[]{ChainBlock.class, HayBlock.class};

  @Inject(method = "<init>", at = @At("TAIL"))
  private void knots_and_rings$logInit(CallbackInfo ci) {
    if (!this.isValidSubclass()) return;

    BlockState state = this.defaultBlockState();
    if (!LivingWoodBlock.isLivingWoodBlock(state)) return;

    this.registerDefaultState(state
        .setValue(ALIVE, false)
        .setValue(IS_TRUNK, false)
        .setValue(SINGLETON, true)
        .setValue(SIDES, LogSide.Mapping.M_0000));
  }

  @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
  private void knots_and_rings$createLogStateDef(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
    if (this.isValidSubclass()) builder.add(ALIVE, SINGLETON, IS_TRUNK, SIDES);
  }

  @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
  private BlockState knots_and_rings$getStateForLogPlacement(BlockState state, @Local(argsOnly = true) BlockPlaceContext ctx) {
    Level level = ctx.getLevel();
    if (level.isClientSide()) return state;

    Block placedBlock = state.getBlock();
    ItemStack itemInHand = ctx.getItemInHand();
    if (!itemInHand.is(placedBlock.asItem())) return state;
    if (!LivingWoodBlock.isNaturalWood(state)) return state;

    BlockPos pos = ctx.getClickedPos();
    state = LivingWoodBlock.checkLogsNearby(state, level, pos).setValue(SIDES, LogSide.Mapping.M_0000);
    if (LivingWoodBlock.isTrunk(state)) state = LivingWoodBlock.getLogShape(state, level, pos);

    return state;
  }

  @Override
  protected BlockState knots_and_rings$updateShape(BlockState state, /*? if < 1.21.5 { *//*LevelAccessor*//*? } else { */LevelReader/*? } */ level, BlockPos pos, Direction from, BlockState state2) {
    if (!LivingWoodBlock.identicalLogs(state, state2)) return state;
    if (from.getAxis() == LivingWoodBlock.getAxis(state)) return state;
    if (!LivingWoodBlock.isTrunk(state)) return state;

    return LivingWoodBlock.getLogShape(state, level, pos);
  }

  @Override
  protected void knots_and_rings$tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
    knots_and_rings$randomTick(state, level, pos, random, ci);
  }

  @Override
  protected boolean knots_and_rings$isRandomlyTicking(boolean original, BlockState state) {
    return LivingWoodBlock.isNaturalWood(state) || original;
  }

  @Override
  protected void knots_and_rings$randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
    if (!LivingWoodBlock.isNaturalWood(state)) return;

    if (LogConnectivityCache.exploring(level, pos)) return;
    Boolean cachedAlive = LogConnectivityCache.checkCached(level, pos);
    if (cachedAlive == null) {
      LivingWoodCluster.attemptRevivePath(level, pos);
    } else {
      LivingWoodBlock.updateLivingState(level, pos, cachedAlive);
    }

    if (LivingWoodBlock.isTrunkNearby(state, level, pos)) {
      LivingWoodBlock.updateIsTrunk(state, level, pos, true);
    }
  }

  @Override
  protected InteractionResult knots_and_rings$useItemOn(InteractionResult result, /*? if >= 1.21 { */ItemStack stack,/*? } */ BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    //? < 1.21
    //ItemStack stack = player.getItemInHand(hand);

    if (stack.is(ItemTags.PICKAXES)) {
      return splitLog(stack, state, level, pos, player, hand, hitResult);
    }

    if (LivingWoodBlock.isNaturalWood(state) && stack.is(Items.BONE_MEAL)) {
      return healLog(stack, state, level, pos, player, hitResult);
    }

    return result;
  }

  @Override
  protected void knots_and_rings$destroy(LevelAccessor levelAccess, BlockPos pos, BlockState state, CallbackInfo ci) {
    if (!LivingWoodBlock.isNaturalWood(state)) return;
    if (!(levelAccess instanceof Level level)) return;

    if (LogConnectivityCache.checkCached(level, pos) != null) {
      LogConnectivityCache.invalidateAttachedTo(level, pos);
    }
  }

  @Override
  public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state/*? if < 1.21 >> ') {'*//*, boolean bl*/) {
    return LivingWoodBlock.isNaturalWood(state) && !LivingWoodBlock.isTrunk(state);
  }

  @Override
  public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
    return true;
  }

  @Override
  public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
  }

  //? > 1.21 {
  @Override
  public @NotNull BlockPos getParticlePos(BlockPos pos) {
    return pos.above();
  }
  //? }

  @Unique
  private static InteractionResult splitLog(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    boolean isNatural = LivingWoodBlock.isNaturalWood(state);
    boolean isTrunk = LivingWoodBlock.isTrunk(state);

    if (isNatural && !isTrunk) return InteractionResult.PASS;
    if (!isNatural && LivingWoodBlock.getSides(state).equals(LogSide.Mapping.M_0000)) return InteractionResult.PASS;

    if (level.isClientSide()) Particles.spawn(level, pos, hitResult, Particles.BLOCK.apply(state));
    else {
      float pitch = 0.8F + level.getRandom().nextFloat() * 0.2F;
      level.playSound(null, pos, ModSounds.SPLIT_WOOD.get(), SoundSource.BLOCKS, 1F, pitch);
      level.playSound(null, pos, ModSounds.CRACK_WOOD.get(), SoundSource.BLOCKS, 1F, pitch);

      if (player != null) ItemDamage.hurtAndBreak(stack, hand, player, 1);
      state = state.setValue(SIDES, LogSide.Mapping.M_0000);
      if (isNatural) {
        LogConnectivityCache.invalidateAttachedTo(level, pos);
        LivingWoodCluster.revivePathOrDecay(level, pos, true);
        LivingWoodBlock.updateIsTrunk(state, level, pos, false);
      } else {
        level.setBlockAndUpdate(pos, state);
      }
    }

    return InteractionResult.SUCCESS;
  }

  @Unique
  private static InteractionResult healLog(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    boolean isTrunk = LivingWoodBlock.isTrunk(state);
    BlockState newState = LivingWoodBlock.getLogShape(state, level, pos);

    if (isTrunk && !LivingWoodBlock.changedShape(state, newState)) return InteractionResult.PASS;

    if (level.isClientSide()) Particles.spawn(level, pos, hitResult, ParticleTypes.HAPPY_VILLAGER);
    else {
      float pitch = 0.8F + level.getRandom().nextFloat() * 0.2F;
      level.playSound(null, pos, ModSounds.HEAL_WOOD.get(), SoundSource.BLOCKS, 1F, pitch);
      level.playSound(null, pos, ModSounds.HEAL_WOOD_ALT.get(), SoundSource.BLOCKS, 1F, 1.2F);

      //? < 1.21
      //if (!player.getAbilities().instabuild) stack.shrink(1);
      //? >= 1.21
      stack.consume(1, player);

      if (isTrunk) {
        level.setBlockAndUpdate(pos, newState);
      } else {
        LogConnectivityCache.invalidateAttachedTo(level, pos);
        LivingWoodBlock.updateIsTrunk(state, level, pos, true);
        healRandomNeighbors(level, pos);
      }
    }

    return InteractionResult.SUCCESS;
  }

  @Unique
  private static void healRandomNeighbors(Level level, BlockPos pos) {
    RandomSource random = level.getRandom();
    LivingWoodBlock.neighborsOf(level, pos).forEach((neighborState, neighborPos) -> {
      if (!LivingWoodBlock.isLivingWoodBlock(neighborState) || LivingWoodBlock.isTrunk(neighborState)) return;

      if (random.nextFloat() < 0.3) {
        int delay = random.nextIntBetweenInclusive(5, 20);
        level.scheduleTick(neighborPos, neighborState.getBlock(), delay);
      }
    });
  }

  @Unique
  private boolean isValidSubclass() {
    return Arrays.stream(INVALID_SUBCLASSES).noneMatch((c) -> c.isInstance(this));
  }

  static {
    ALIVE = LivingWoodBlock.Properties.ALIVE;
    IS_TRUNK = LivingWoodBlock.Properties.IS_TRUNK;
    SINGLETON = LivingWoodBlock.Properties.SINGLETON;
    SIDES = LivingWoodBlock.Properties.SIDES;
  }
}
