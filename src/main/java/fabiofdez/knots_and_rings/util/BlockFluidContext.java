package fabiofdez.knots_and_rings.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BlockFluidContext {
  BlockState getAt(BlockPos pos);
}
