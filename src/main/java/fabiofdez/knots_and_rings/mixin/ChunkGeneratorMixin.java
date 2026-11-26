package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.feature.TreeGenContext;
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
  private void knots_and_rings$startDecoratingTrees(CallbackInfo ci, @Local(argsOnly = true) WorldGenLevel level, @Local(argsOnly = true) ChunkAccess chunk) {
    TreeGenContext.startDecorating(level.getLevel(), chunk.getPos());
  }

  @Definition(id = "level", local = @Local(type = WorldGenLevel.class, argsOnly = true))
  @Definition(id = "setCurrentlyGenerating", method = "Lnet/minecraft/world/level/WorldGenLevel;setCurrentlyGenerating(Ljava/util/function/Supplier;)V")
  @Expression("level.setCurrentlyGenerating(null)")
  @Inject(method = "applyBiomeDecoration", at = @At("MIXINEXTRAS:EXPRESSION"))
  private void knots_and_rings$placeSaplingsNearTrees(CallbackInfo ci, @Local(argsOnly = true) WorldGenLevel level, @Local(argsOnly = true) ChunkAccess chunk) {
    TreeGenContext.placeSaplings(level.getLevel(), chunk.getPos());
  }
}
