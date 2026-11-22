package fabiofdez.knots_and_rings.platform.fabric;

//? if fabric {

import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.ModBlocks;
import fabiofdez.knots_and_rings.feature.LogClusterCache;
import fabiofdez.knots_and_rings.feature.SaplingType;
import fabiofdez.knots_and_rings.resource.PackRegistry;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

//? if < 1.21.11 {
/*import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
*///? }

public class FabricEventSubscriber {

  public static void registerEvents() {
    KnotsAndRings.modifyCreativeTabs(ModBlocks::addCreative);

    ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> LogClusterCache.invalidateInChunk(level, chunk.getPos()));

    registerServerDataPacks();
    registerTagReloadListener();
  }

  private static void registerServerDataPacks() {
    ModContainer container = FabricLoader.getInstance().getModContainer(KnotsAndRings.MOD_ID).orElse(null);
    if (container == null) return;

    ResourcePacks.Server.registerPacks(PackRegistry.resolve(container));
  }

  private static void registerTagReloadListener() {
    //? if < 1.21.11 {
    /*SimpleSynchronousResourceReloadListener onTagReload = new SimpleSynchronousResourceReloadListener() {

      @Override
      public Identifier getFabricId() {
        return ResourcePacks.Server.TAG_RELOAD_ID;
      }

      @Override
      public void onResourceManagerReload(ResourceManager manager) {
        SaplingType.resetPossibleShapes();
      }
    };
    *///? } else {
    ResourceManagerReloadListener onTagReload = (manager) -> SaplingType.resetPossibleShapes();
    //? }

    //~ if >= 1.21.11 'ResourceManagerHelper' -> 'ResourceLoader'
    ResourceLoader.get(PackType.SERVER_DATA)
                  //~ if 1.21.11 'registerReloadListener' -> 'registerReloader'
                  .registerReloadListener(/*? if >= 1.21.11 {*/ResourcePacks.Server.TAG_RELOAD_ID,/*? } */onTagReload);
  }
}
//? }
