package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fabiofdez.knots_and_rings.block.LogBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if < 1.21 {
/*import net.minecraft.world.level.block.RotatedPillarBlock;
import org.spongepowered.asm.mixin.injection.Redirect;
*///? } else {
import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.KnotsAndRings;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;
//?}

@Mixin(Blocks.class)
public class BlocksMixin {

  //? if < 1.21 {
  /*@Redirect(method = "log(Lnet/minecraft/world/level/material/MapColor;Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/RotatedPillarBlock;", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/RotatedPillarBlock;"))
  private static RotatedPillarBlock knots_and_rings$customLog(Properties properties) {
    return new LogBlock(properties);
  }

  @Redirect(method = "log(Lnet/minecraft/world/level/material/MapColor;Lnet/minecraft/world/level/material/MapColor;Lnet/minecraft/world/level/block/SoundType;)Lnet/minecraft/world/level/block/RotatedPillarBlock;", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/RotatedPillarBlock;"))
  private static RotatedPillarBlock knots_and_rings$customLog2(Properties properties) {
    return new LogBlock(properties);
  }
  *///?} else {
  @Inject(method = "register(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", at = @At("HEAD"))
  private static void knots_and_rings$customLog(CallbackInfoReturnable<Block> cir, @Local(argsOnly = true) LocalRef<Function<Properties, Block>> functionRef, @Local(argsOnly = true) ResourceKey<Block> blockKey) {
    //? < 1.21.11
    ResourceLocation blockId = blockKey.location();
    //? >= 1.21.11
    //ResourceLocation blockId = blockKey.identifier();

    if (!blockId.getPath().endsWith("_log")) return;

    KnotsAndRings.LOGGER.info("registering {} as {}", blockId, LogBlock.class.getSimpleName());
    functionRef.set(LogBlock::new);
  }
  //?}
}
