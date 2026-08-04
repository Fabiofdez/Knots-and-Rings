//~ has_interaction_result

package fabiofdez.knots_and_rings.block;

import fabiofdez.knots_and_rings.ModSounds;
import fabiofdez.knots_and_rings.block.state.LogSide;
import fabiofdez.knots_and_rings.util.LivingWoodBlock;
import fabiofdez.knots_and_rings.util.LivingWoodCluster;
import fabiofdez.knots_and_rings.util.LogConnectivityCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
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
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

//? >= 1.21.5 {
import net.minecraft.world.level.ScheduledTickAccess;
 //? }

public class LogBlock extends RotatedPillarBlock implements BonemealableBlock {

  public static final BooleanProperty ALIVE;
  public static final BooleanProperty IS_TRUNK;
  public static final BooleanProperty SINGLETON;
  public static final EnumProperty<LogSide.Mapping> SIDES;

  private static final Function<BlockState, ParticleOptions> BLOCK_PARTICLES;

  public LogBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this
        .defaultBlockState()
        .setValue(ALIVE, false)
        .setValue(IS_TRUNK, false)
        .setValue(SINGLETON, true)
        .setValue(SIDES, LogSide.Mapping.M_0000));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(ALIVE, SINGLETON, IS_TRUNK, SIDES);
  }

  @NotNull
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    BlockState state = super.getStateForPlacement(ctx);

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

  @NotNull
  @Override
      //? < 1.21.5
  //protected BlockState updateShape(BlockState state, Direction from, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2) {
    //? >= 1.21.5
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ignored, BlockPos pos, Direction from, BlockPos pos2, BlockState state2, RandomSource random) {
    if (!LivingWoodBlock.identicalLogs(state, state2)) return state;
    if (from.getAxis() == LivingWoodBlock.getAxis(state)) return state;
    if (!LivingWoodBlock.isTrunk(state)) return state;

    return LivingWoodBlock.getLogShape(state, level, pos);
  }

  @Override
  protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    randomTick(state, level, pos, random);
  }

  @Override
  protected boolean isRandomlyTicking(BlockState state) {
    return LivingWoodBlock.isNaturalWood(state) || super.isRandomlyTicking(state);
  }

  @Override
  protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (!LivingWoodBlock.isNaturalWood(state)) return;

    if (LogConnectivityCache.exploring(pos)) return;
    Boolean cachedAlive = LogConnectivityCache.checkCached(pos);
    if (cachedAlive == null) {
      LivingWoodCluster.attemptRevivePath(level, pos);
    } else {
      LivingWoodBlock.updateLivingState(level, pos, cachedAlive);
    }

    if (LivingWoodBlock.isTrunkNearby(state, level, pos)) {
      LivingWoodBlock.updateIsTrunk(state, level, pos, true);
    }
  }

  @NotNull
  @Override
  protected InteractionResult useItemOn(/*? if >= 1.21 >> 'BlockState' */ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
    //? < 1.21
    //super.use(state, level, pos, player, hand, hitResult);
    //? >= 1.21
    super.useItemOn(stack, state, level, pos, player, hand, hitResult);

    //? < 1.21
    //ItemStack stack = player.getItemInHand(hand);

    if (stack.is(ItemTags.PICKAXES)) {
      return splitLog(stack, state, level, pos, player, hitResult);
    }

    if (!LivingWoodBlock.isNaturalWood(state)) return InteractionResult.PASS;

    if (stack.is(Items.BONE_MEAL)) {
      return healLog(stack, state, level, pos, player, hitResult);
    }

    return InteractionResult.PASS;
  }

  @Override
  public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
    super.destroy(level, pos, state);
    if (!LivingWoodBlock.isNaturalWood(state)) return;

    if (LogConnectivityCache.checkCached(pos) != null) {
      LogConnectivityCache.invalidateAttachedTo(level.getChunk(pos), pos);
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

  private static InteractionResult splitLog(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    boolean isNatural = LivingWoodBlock.isNaturalWood(state);
    boolean isTrunk = LivingWoodBlock.isTrunk(state);

    if (isNatural && !isTrunk) return InteractionResult.PASS;
    if (!isNatural && LivingWoodBlock.getSides(state).equals(LogSide.Mapping.M_0000)) return InteractionResult.PASS;

    if (level.isClientSide()) spawnParticles(level, pos, hitResult, BLOCK_PARTICLES.apply(state));
    else {
      float pitch = 0.8F + level.getRandom().nextFloat() * 0.2F;
      level.playSound(null, pos, ModSounds.SPLIT_WOOD.get(), SoundSource.BLOCKS, 1F, pitch);
      level.playSound(null, pos, ModSounds.CRACK_WOOD.get(), SoundSource.BLOCKS, 1F, pitch);
      if (!player.isCreative() && stack.isDamageableItem()) stack.setDamageValue(stack.getDamageValue() - 1);

      state = state.setValue(SIDES, LogSide.Mapping.M_0000);
      if (isNatural) {
        LogConnectivityCache.invalidateAttachedTo(level.getChunkAt(pos), pos);
        LivingWoodCluster.revivePathOrDecay(level, pos, true);
        LivingWoodBlock.updateIsTrunk(state, level, pos, false);
      } else {
        level.setBlockAndUpdate(pos, state);
      }
    }

    return InteractionResult.SUCCESS;
  }

  private static InteractionResult healLog(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    boolean isTrunk = LivingWoodBlock.isTrunk(state);
    BlockState newState = LivingWoodBlock.getLogShape(state, level, pos);

    if (isTrunk && !LivingWoodBlock.changedShape(state, newState)) return InteractionResult.PASS;

    if (level.isClientSide()) spawnParticles(level, pos, hitResult, ParticleTypes.HAPPY_VILLAGER);
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
        LogConnectivityCache.invalidateAttachedTo(level.getChunkAt(pos), pos);
        LivingWoodBlock.updateIsTrunk(state, level, pos, true);
        healRandomNeighbors(level, pos);
      }
    }

    return InteractionResult.SUCCESS;
  }

  private static void healRandomNeighbors(Level level, BlockPos pos) {
    RandomSource random = level.getRandom();
    LivingWoodBlock.neighborsOf(level, pos).forEach((neighborState, neighborPos) -> {
      if (!(neighborState.getBlock() instanceof LogBlock neighbor) || LivingWoodBlock.isTrunk(neighborState)) return;

      if (random.nextFloat() < 0.3) {
        int delay = random.nextIntBetweenInclusive(5, 20);
        level.scheduleTick(neighborPos, neighbor, delay);
      }
    });
  }

  private static void spawnParticles(Level level, BlockPos pos, BlockHitResult hitResult, ParticleOptions particleOpts) {
    ParticleUtils.spawnParticlesOnBlockFace(
        level,
        pos,
        particleOpts,
        UniformInt.of(10, 15),
        hitResult.getDirection(),
        () -> Vec3.ZERO,
        0.5
    );
  }

  static {
    ALIVE = LivingWoodBlock.Properties.ALIVE;
    IS_TRUNK = LivingWoodBlock.Properties.IS_TRUNK;
    SINGLETON = LivingWoodBlock.Properties.SINGLETON;
    SIDES = LivingWoodBlock.Properties.SIDES;

    BLOCK_PARTICLES = (state) -> new BlockParticleOption(ParticleTypes.BLOCK, state);
  }
}
