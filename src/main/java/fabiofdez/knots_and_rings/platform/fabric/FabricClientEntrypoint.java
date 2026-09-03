package fabiofdez.knots_and_rings.platform.fabric;

//? fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.ModBlocks;
import fabiofdez.knots_and_rings.feature.LivingWoodBlock;
import fabiofdez.knots_and_rings.feature.SaplingTint;
import fabiofdez.knots_and_rings.resource.BuiltInResourcePack;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
import net.fabricmc.api.ClientModInitializer;
//? < 26.1
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
//? <= 1.21.5
import net.minecraft.client.renderer.RenderType;
//? >= 1.21.11 && < 26.1
//import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    KnotsAndRings.onInitializeClient();

    BuiltInRegistries.BLOCK.entrySet().forEach((entry) -> {
      ResourceLocation blockId = KnotsAndRings.fromKey(entry.getKey());
      if (!LivingWoodBlock.isLogBlock(blockId) && !LivingWoodBlock.isWoodBlock(blockId)) return;

      renderTranslucent(entry.getValue());
    });

    ModBlocks.SaplingStems.forEach(FabricClientEntrypoint::renderCutout);
    ModBlocks.TreeSeeds.forEach(FabricClientEntrypoint::renderCutout);
    SaplingTint.registerWith(ColorProviderRegistry.BLOCK::register);

    ModContainer container = FabricLoader.getInstance().getModContainer(KnotsAndRings.MOD_ID).orElse(null);
    if (container == null) return;

    ResourcePacks.registerWith((pack) -> addPack(container, pack));
  }

  private static void renderCutout(Block block) {
    //? < 26.1
    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
  }

  private static void renderTranslucent(Block block) {
    //? < 26.1
    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent());
  }

  private static void addPack(ModContainer container, BuiltInResourcePack pack) {
    ResourceManagerHelper.registerBuiltinResourcePack(
        KnotsAndRings.id(pack.id()),
        container,
        pack.name(),
        activationFor(pack)
    );
  }

  private static ResourcePackActivationType activationFor(BuiltInResourcePack pack) {
    return pack.defaultEnabled() ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL;
  }
}
//?}
