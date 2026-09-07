//~ uses_tree_grower

package fabiofdez.knots_and_rings.mixin.vanillabackport;

//? <= 1.21.1 {

/*import com.blackgear.vanillabackport.common.registries.ModBlocks;
import fabiofdez.knots_and_rings.ModBlocks.SaplingStems;
import fabiofdez.knots_and_rings.ModBlocks.TreeSeeds;
import fabiofdez.knots_and_rings.feature.SaplingType;
import net.minecraft.world.level.block.grower.TreeGrower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static fabiofdez.knots_and_rings.feature.SaplingTypeID.*;

@SuppressWarnings("UnusedMixin")
@Mixin(ModBlocks.class)
public class VanillaBackportMixin {

  @Unique
      //? < 1.21
  //private static AbstractTreeGrower paleOakTreeGrower;
      //? >= 1.21
  private static TreeGrower paleOakTreeGrower;

  @ModifyArg(method = "lambda$static$6", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SaplingBlock;<init>(Lnet/minecraft/world/level/block/grower/TreeGrower;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"), remap = false)
      //? < 1.21
  //private static AbstractTreeGrower knots_and_rings$captureTreeGrower(TreeGrower treeGrower) {
      //? >= 1.21
  private static TreeGrower knots_and_rings$captureTreeGrower(TreeGrower treeGrower) {
    paleOakTreeGrower = treeGrower;
    return treeGrower;
  }

  @Inject(method = "<clinit>", at = @At("TAIL"))
  private static void knots_and_rings$initVanillaBackportBlocks(CallbackInfo ci) {
    if (paleOakTreeGrower == null) return;

    SaplingType.add(PALE_OAK, ModBlocks.PALE_OAK_SAPLING, ModBlocks.PALE_OAK_LEAVES);
    SaplingStems.add(PALE_OAK, (props) -> SaplingStems.paleOakSaplingBlock(paleOakTreeGrower, props));
    TreeSeeds.add(PALE_OAK, (props) -> TreeSeeds.seedBlock(ModBlocks.PALE_OAK_SAPLING.get(), props));
  }
}
*///? }
