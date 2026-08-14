//~ blocks_mixin

package fabiofdez.knots_and_rings.mixin;

import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.block.LogBlock;
import fabiofdez.knots_and_rings.feature.LivingWoodBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

//? if <= 1.21.1 {
/*import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.world.level.block.RotatedPillarBlock;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
*///? } else {
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? }

@Mixin(Blocks.class)
public class BlocksMixin {

  //? if <= 1.21.1 {
  /*@Redirect(method = "log(Lnet/minecraft/world/level/material/MapColor;Lnet/minecraft/world/level/material/MapColor;)Lnet/minecraft/world/level/block/Block;", at = @At(value = "NEW", target = LivingWoodBlock.OLD_CONSTRUCTOR))
  private static RotatedPillarBlock knots_and_rings$customLog(BlockBehaviour.Properties properties) {
    return new LogBlock(properties);
  }

  @Redirect(method = "log(Lnet/minecraft/world/level/material/MapColor;Lnet/minecraft/world/level/material/MapColor;Lnet/minecraft/world/level/block/SoundType;)Lnet/minecraft/world/level/block/Block;", at = @At(value = "NEW", target = LivingWoodBlock.OLD_CONSTRUCTOR))
  private static RotatedPillarBlock knots_and_rings$customLog2(BlockBehaviour.Properties properties) {
    return new LogBlock(properties);
  }
  *///?} else {
  @Inject(method = "register(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", at = @At("HEAD"))
  private static void knots_and_rings$customLog(CallbackInfoReturnable<Block> cir, @Local(argsOnly = true) LocalRef<Function<BlockBehaviour.Properties, Block>> functionRef, @Local(argsOnly = true) ResourceKey<Block> blockKey) {
    if (!LivingWoodBlock.isLogBlock(KnotsAndRings.fromKey(blockKey))) return;
    functionRef.set(LogBlock::new);
  }
  //?}
}
