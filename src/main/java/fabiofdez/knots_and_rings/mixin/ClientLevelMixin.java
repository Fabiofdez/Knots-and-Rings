package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? >= 1.21.11 {
/*import fabiofdez.knots_and_rings.compat.Particles;
import net.minecraft.core.BlockPos;
*///? }

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

  @Inject(method = "addDestroyBlockEffect", at = @At("HEAD"))
  protected void knots_and_rings$cancelHiddenSaplings(CallbackInfo ci, @Local(argsOnly = true) LocalRef<BlockState> stateRef) {
    BlockState state = stateRef.get();
    if (!GrowingSapling.isGrowingSapling(state)) return;
    if (GrowingSapling.growthStage(state) != GrowingSapling.Stage.HIDDEN) return;

    stateRef.set(Blocks.AIR.defaultBlockState());
  }

  //? >= 1.21.11 {
  /*@Inject(method = "addDestroyBlockEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;", shift = At.Shift.AFTER))
  protected void knots_and_rings$changeDestroyParticles(CallbackInfo ci, @Local(argsOnly = true) LocalRef<BlockState> stateRef, @Local(argsOnly = true) BlockPos pos) {
    stateRef.set(Particles.getForSaplingParticle(stateRef.get(), (ClientLevel) (Object) this, pos));
  }
  *///? }
}
