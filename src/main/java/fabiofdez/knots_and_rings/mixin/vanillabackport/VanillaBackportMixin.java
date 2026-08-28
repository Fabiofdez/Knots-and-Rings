//~ uses_tree_grower

package fabiofdez.knots_and_rings.mixin.vanillabackport;

//? <= 1.21.1 {

/*import com.blackgear.vanillabackport.common.registries.ModBlocks;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import fabiofdez.knots_and_rings.ModBlocks.SaplingStems;
import fabiofdez.knots_and_rings.ModBlocks.TreeSeeds;
import fabiofdez.knots_and_rings.block.LogBlock;
import fabiofdez.knots_and_rings.feature.SaplingType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Function;

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

  @Definition(id = "RotatedPillarBlock", type = RotatedPillarBlock.class)
  @Definition(id = "PALE_OAK_SAPLING", field = "Lcom/blackgear/vanillabackport/common/registries/ModBlocks;PALE_OAK_SAPLING:Ljava/util/function/Supplier;")
  @Expression(value = "RotatedPillarBlock::new", id = "log_block")
  @Expression(value = "PALE_OAK_SAPLING = ?", id = "pale_oak_sapling")
  @ModifyArgs(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/blackgear/platform/core/helper/BlockRegistry;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Ljava/util/function/Supplier;"), slice = @Slice(from = @At(value = "MIXINEXTRAS:EXPRESSION", id = "log_block"), to = @At(value = "MIXINEXTRAS:EXPRESSION", id = "pale_oak_sapling")), remap = false)
  private static void knots_and_rings$customPaleOakLogs(Args args) {
    String blockId = args.get(0);

    switch (blockId) {
      case "pale_oak_log", "stripped_pale_oak_log" -> {
        Function<BlockBehaviour.Properties, Block> logBlock = LogBlock::new;
        args.set(1, logBlock);
      }
    }
  }
}
*///? }
