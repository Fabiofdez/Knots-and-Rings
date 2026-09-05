package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.feature.WorldGenContext;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {

  @Inject(method = "applyBiomeDecoration", at = @At("HEAD"))
  private void knots_and_rings$startGeneratingTrees(CallbackInfo ci, @Local(argsOnly = true) WorldGenLevel level, @Local(argsOnly = true) ChunkAccess chunk) {
    WorldGenContext.startDecorating(level.getLevel(), chunk.getPos());
  }

  @Inject(method = "applyBiomeDecoration", at = @At("RETURN"))
  private void knots_and_rings$endGeneratingTrees(CallbackInfo ci, @Local(argsOnly = true) WorldGenLevel level, @Local(argsOnly = true) ChunkAccess chunk) {
    WorldGenContext.endDecorating(level.getLevel(), chunk.getPos());
  }
}
