package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.feature.TreeGenContext;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//~ if < 1.21 'status.ChunkStatusTasks' -> 'ChunkStatus'
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;

//? if < 1.21 {
/*import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///? } else {
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
//? }

//~ if < 1.21 'ChunkStatusTasks' -> 'ChunkStatus'
@Mixin(ChunkStatusTasks.class)
public class ChunkTaskMixin {

  @Unique
  private static final String SPAWN_MOBS = "Lnet/minecraft/world/level/chunk/ChunkGenerator;spawnOriginalMobs(Lnet/minecraft/server/level/WorldGenRegion;)V";

  //? if < 1.21 {
  /*@Unique
  private static final String GENERATE_SPAWN = /^? if fabric { ^/"method_17033";
  /^? } else { ^/
  /^"lambda$static$16(Lnet/minecraft/world/level/chunk/ChunkStatus;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Ljava/util/List;Lnet/minecraft/world/level/chunk/ChunkAccess;)V";^/
  /^? } ^/

  @Inject(method = GENERATE_SPAWN, at = @At(value = "INVOKE", target = SPAWN_MOBS))
  private static void knots_and_rings$uprootSaplingsInShadow(CallbackInfo ci, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) ChunkAccess chunk) {
    TreeGenContext.endDecorating(level, chunk.getPos());
  }
  *///? } else {
  @Inject(method = "generateSpawn", at = @At(value = "INVOKE", target = SPAWN_MOBS))
  private static void knots_and_rings$uprootSaplingsInShadow(CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir, @Local(argsOnly = true) WorldGenContext ctx, @Local(argsOnly = true) ChunkAccess chunk) {
    TreeGenContext.endDecorating(ctx.level(), chunk.getPos());
  }
  //? }
}
