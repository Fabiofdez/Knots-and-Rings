package fabiofdez.knots_and_rings.block;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WaterloggedTreeSeedBlock extends TreeSeedBlock implements SimpleWaterloggedBlock {

  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

  public WaterloggedTreeSeedBlock(Block saplingBlock, Properties properties) {
    super(saplingBlock, properties);
    this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(WATERLOGGED);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    BlockState state = super.getStateForPlacement(ctx);
    if (state == null) return defaultBlockState();

    FluidState replacedFluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
    boolean isWaterSource = replacedFluidState.is(Fluids.WATER);
    return state.setValue(WATERLOGGED, isWaterSource);
  }

  @NotNull
  @Override
  protected FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }
}
