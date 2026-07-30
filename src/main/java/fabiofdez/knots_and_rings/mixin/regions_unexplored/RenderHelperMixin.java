package fabiofdez.knots_and_rings.mixin.regions_unexplored;

//? fabric && <= 1.21.1 {

/*import net.minecraft.world.level.block.Block;
import net.regions_unexplored.registry.RUBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

//? if < 1.21 {
/^import net.regions_unexplored.client.render.type.RuBlockCropOuts;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Arrays;
^///? } else {
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.renderer.RenderType;
import net.regions_unexplored.module.platform.RenderHelper;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? }

@SuppressWarnings({"unused", "UnusedMixin"})
//? < 1.21
//@Mixin(RuBlockCropOuts.class)
//? >= 1.21
@Mixin(RenderHelper.class)
public class RenderHelperMixin {

  @Unique
  private static final Block EUCALYPTUS_LOG = RUBlocks
      //? < 1.21
      //.EUCALYPTUS_LOG;
      //? >= 1.21
      .EUCALYPTUS_WOOD_SET.getLog();

  //? if < 1.21 {

  /^@Unique
  private static final String PUT_BLOCKS = "Lnet/fabricmc/fabric/api/blockrenderlayer/v1/BlockRenderLayerMap;putBlocks(Lnet/minecraft/client/renderer/RenderType;[Lnet/minecraft/world/level/block/Block;)V";

  @ModifyArg(method = "register", at = @At(value = "INVOKE", target = PUT_BLOCKS), index = 1)
  private static Block[] knots_and_rings$setEucalyptusTranslucent(Block[] blocks) {
    return (Block[]) Arrays.stream(blocks).filter((block) -> block != EUCALYPTUS_LOG).toArray();
  }
  ^///? } else {
  @Inject(method = "setRenderType", at = @At("HEAD"))
  private static void knots_and_rings$setEucalyptusTranslucent(CallbackInfo ci, @Local(argsOnly = true) Block block, @Local(argsOnly = true) LocalRef<RenderType> renderType) {
    if (block == EUCALYPTUS_LOG) renderType.set(RenderType.translucent());
  }
  //? }
}
*///? }
