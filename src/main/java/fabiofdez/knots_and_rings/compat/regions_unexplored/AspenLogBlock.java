package fabiofdez.knots_and_rings.compat.regions_unexplored;

//? <= 1.21.1 {

/*import fabiofdez.knots_and_rings.block.LogBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.regions_unexplored.world.level.block.state.properties.RuBlockStateProperties;
import org.jetbrains.annotations.NotNull;

//? >= 1.21.5 {
import net.minecraft.world.level.ScheduledTickAccess;
 //? }

public class AspenLogBlock extends LogBlock {

  public static final BooleanProperty IS_BASE;

  public AspenLogBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.defaultBlockState().setValue(IS_BASE, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(IS_BASE);
  }

  @NotNull
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    BlockState state = super.getStateForPlacement(ctx);

    Level level = ctx.getLevel();
    if (level.isClientSide()) return state;

    BlockState stateBelow = level.getBlockState(ctx.getClickedPos().below());
    return state.setValue(IS_BASE, stateBelow.is(BlockTags.DIRT));
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
    return state.setValue(IS_BASE, stateBelow.is(BlockTags.DIRT));
  }

  static {
    IS_BASE = RuBlockStateProperties.IS_BASE;
  }
}
*///? }
