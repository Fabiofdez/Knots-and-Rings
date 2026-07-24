package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.block.LogBlock;
import fabiofdez.knots_and_rings.util.LivingWoodBlock;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  @ModifyVariable(method = "useOn", at = @At("STORE"), ordinal = 0)
  private Optional<BlockState> knots_and_rings$preserveLogSides(Optional<BlockState> strippedState, @Local(argsOnly = true) UseOnContext ctx) {
    BlockState initialState = ctx.getLevel().getBlockState(ctx.getClickedPos());
    if (!LivingWoodBlock.isNaturalWood(initialState)) return strippedState;

    return strippedState.map((state) -> state.setValue(LogBlock.SIDES, initialState.getValue(LogBlock.SIDES)));
  }
}
