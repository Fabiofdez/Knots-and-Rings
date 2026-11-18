package fabiofdez.knots_and_rings.platform.fabric;

//? if fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.feature.SaplingTint;
import fabiofdez.knots_and_rings.resource.BuiltInResourcePack;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

//? if <= 1.21.5
import net.minecraft.client.renderer.RenderType;
//? if < 26.1 {
import fabiofdez.knots_and_rings.ModBlocks;
import fabiofdez.knots_and_rings.feature.LivingWoodBlock;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
//? }
//? if >= 1.21.11 < 26.1
//import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    KnotsAndRings.onInitializeClient();

    //? if < 26.1 {
    BuiltInRegistries.BLOCK.entrySet().forEach((entry) -> {
      ResourceLocation blockId = KnotsAndRings.fromKey(entry.getKey());
      if (!LivingWoodBlock.isLogBlock(blockId) && !LivingWoodBlock.isWoodBlock(blockId)) return;

      renderTranslucent(entry.getValue());
    });

    ModBlocks.SaplingStems.forEach(FabricClientEntrypoint::renderCutout);
    ModBlocks.TreeSeeds.forEach(FabricClientEntrypoint::renderCutout);
    //? }

    SaplingTint.registerWith(ColorProviderRegistry.BLOCK::register);

    ModContainer container = FabricLoader.getInstance().getModContainer(KnotsAndRings.MOD_ID).orElse(null);
    if (container == null) return;

    ResourcePacks.registerWith((pack) -> addPack(container, pack));
  }

  //? if < 26.1 {
  private static void renderCutout(Block block) {
    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
  }

  private static void renderTranslucent(Block block) {
    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent());
  }
  //? }

  private static void addPack(ModContainer container, BuiltInResourcePack pack) {
    ResourceManagerHelper.registerBuiltinResourcePack(
        KnotsAndRings.id(pack.id()),
        container,
        pack.name(),
        activationType(pack)
    );
  }

  private static ResourcePackActivationType activationType(BuiltInResourcePack pack) {
    return pack.defaultEnabled() ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL;
  }
}
//? }
