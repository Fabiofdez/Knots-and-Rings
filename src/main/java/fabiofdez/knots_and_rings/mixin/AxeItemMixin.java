package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.block.LogBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {

  @ModifyReturnValue(method = "getStripped", at = @At("RETURN"))
  private static Optional<BlockState> knots_and_rings$preserveLogSides(Optional<BlockState> strippedState, @Local(argsOnly = true) BlockState initialState) {
    if (!initialState.is(BlockTags.LOGS)) return strippedState;
    return strippedState.map((state) -> state.setValue(LogBlock.SIDES, initialState.getValue(LogBlock.SIDES)));
  }
}
