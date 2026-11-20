package fabiofdez.knots_and_rings.platform.fabric;

//? if fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.feature.SaplingTint;
import fabiofdez.knots_and_rings.resource.BuiltInResourcePack;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

//? if <= 1.21.5
//import net.minecraft.client.renderer.rendertype.RenderType;
//? if < 26.1 {
/*import fabiofdez.knots_and_rings.ModBlocks;
import fabiofdez.knots_and_rings.feature.LivingWoodBlock;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
*///? }
//? if >= 1.21.11 < 26.1
//import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    KnotsAndRings.onInitializeClient();

    //? if < 26.1 {
    /*BuiltInRegistries.BLOCK.entrySet().forEach((entry) -> {
      Identifier blockId = KnotsAndRings.fromKey(entry.getKey());
      if (!LivingWoodBlock.isLogBlock(blockId) && !LivingWoodBlock.isWoodBlock(blockId)) return;

      renderTranslucent(entry.getValue());
    });

    ModBlocks.SaplingStems.forEach(FabricClientEntrypoint::renderCutout);
    ModBlocks.TreeSeeds.forEach(FabricClientEntrypoint::renderCutout);
    *///? }

    SaplingTint.registerWith(BlockColorRegistry::register);

    ModContainer container = FabricLoader.getInstance().getModContainer(KnotsAndRings.MOD_ID).orElse(null);
    if (container == null) return;

    ResourcePacks.registerWith((pack) -> addPack(container, pack));
  }

  //? if < 26.1 {
  /*private static void renderCutout(Block block) {
    BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT);
  }

  private static void renderTranslucent(Block block) {
    BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.TRANSLUCENT);
  }
  *///? }

  private static void addPack(ModContainer container, BuiltInResourcePack pack) {
    ResourceLoader.registerBuiltinPack(
        KnotsAndRings.id(pack.id()),
        container,
        pack.name(),
        activationType(pack)
    );
  }

  private static PackActivationType activationType(BuiltInResourcePack pack) {
    return pack.defaultEnabled() ? PackActivationType.DEFAULT_ENABLED : PackActivationType.NORMAL;
  }
}
//? }
