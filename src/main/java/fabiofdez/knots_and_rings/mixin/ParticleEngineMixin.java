package fabiofdez.knots_and_rings.mixin;

import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;

//? < 1.21.11 {
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fabiofdez.knots_and_rings.compat.Particles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? }

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

  //? < 1.21.11 {
  @Shadow
  protected ClientLevel level;

  @Inject(method = "destroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;", shift = At.Shift.AFTER))
  protected void knots_and_rings$changeDestroyParticles(CallbackInfo ci, @Local(argsOnly = true) LocalRef<BlockState> stateRef, @Local(argsOnly = true) BlockPos pos) {
    stateRef.set(Particles.getForSaplingParticle(stateRef.get(), level, pos));
  }
  //? }
}
