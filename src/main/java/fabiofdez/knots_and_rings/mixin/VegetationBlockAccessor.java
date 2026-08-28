package fabiofdez.knots_and_rings.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VegetationBlock.class)
public interface VegetationBlockAccessor {

  @Invoker("mayPlaceOn")
  boolean knots_and_rings$mayPlaceOn(BlockState state, BlockGetter blockGetter, BlockPos pos);

  @Invoker("canSurvive")
  boolean knots_and_rings$canSurvive(BlockState state, LevelReader level, BlockPos pos);
}
