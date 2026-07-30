package fabiofdez.knots_and_rings.compat.regions_unexplored;

//? <= 1.21.1 {

/*import fabiofdez.knots_and_rings.block.LogBlock;
import fabiofdez.knots_and_rings.util.LivingWoodBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.regions_unexplored.registry.RUBlocks;
import net.regions_unexplored.world.level.block.state.properties.RuBlockStateProperties;
import org.jetbrains.annotations.NotNull;

//? >= 1.21.5 {
import net.minecraft.world.level.ScheduledTickAccess;
 //? }

public class PineLogBlock extends LogBlock {

  public static final BooleanProperty TRANSITION_BLOCK;

  public PineLogBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.defaultBlockState().setValue(TRANSITION_BLOCK, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(TRANSITION_BLOCK);
  }

  @NotNull
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    BlockState state = super.getStateForPlacement(ctx);

    Level level = ctx.getLevel();
    if (level.isClientSide()) return state;

    BlockState stateBelow = level.getBlockState(ctx.getClickedPos().below());
    return state.setValue(TRANSITION_BLOCK, getIsTransition(state, stateBelow));
  }

  @NotNull
  @Override
      //? if < 1.21.5 {
  /^protected BlockState updateShape(BlockState state, Direction from, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2) {
    state = super.updateShape(state, from, state2, level, pos, pos2);
    ^///? } else {
  protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ignored, BlockPos pos, Direction from, BlockPos pos2, BlockState state2, RandomSource random) {
    state = super.updateShape(state, level, ignored, pos, from, pos2, state2, random);
    //? }

    BlockState stateBelow = level.getBlockState(pos.below());
    return state.setValue(TRANSITION_BLOCK, getIsTransition(state, stateBelow));
  }

  private static boolean getIsTransition(BlockState state, BlockState stateBelow) {
    if (LivingWoodBlock.getAxis(state) != Direction.Axis.Y) return false;

    Block strippedPine = RUBlocks
        //? < 1.21
        //.STRIPPED_PINE_LOG/^? forge >> ';' ^//^.get()^/;
        //? >= 1.21
        .PINE_WOOD_SET.getStrippedLog();

    return stateBelow.is(strippedPine);
  }

  static {
    TRANSITION_BLOCK = RuBlockStateProperties.TRANSITION_BLOCK;
  }
}
*///? }
