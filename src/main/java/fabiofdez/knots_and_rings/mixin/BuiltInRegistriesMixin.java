package fabiofdez.knots_and_rings.mixin;

import fabiofdez.knots_and_rings.block.state.SaplingType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {

  @Inject(method = "bootStrap", at = @At("TAIL"))
  private static void knots_and_rings$bootStrapRegistries(CallbackInfo ci) {
    SaplingType.freezeTypes();
  }
}
