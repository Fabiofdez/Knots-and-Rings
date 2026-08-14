package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VegetationBlock.class)
public abstract class VegetationBlockMixin extends BlockMixin {

  @ModifyReturnValue(method = "canSurvive", at = @At("RETURN"))
  protected boolean knots_and_rings$canSurvive(boolean original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) LevelReader level, @Local(argsOnly = true) BlockPos pos) {
    return original;
  }

  @ModifyReturnValue(method = "isPathfindable", at = @At("RETURN"))
  protected boolean knots_and_rings$isPathfindable(boolean original, @Local(argsOnly = true) BlockState state) {
    return original;
  }
}
