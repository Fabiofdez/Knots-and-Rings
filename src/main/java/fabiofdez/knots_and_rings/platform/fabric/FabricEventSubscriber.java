package fabiofdez.knots_and_rings.platform.fabric;

//? fabric {

import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.ModBlocks;
import fabiofdez.knots_and_rings.feature.LogConnectivityCache;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

public class FabricEventSubscriber {

  public static void registerEvents() {
    KnotsAndRings.modifyCreativeTabs(ModBlocks::addCreative);

    ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> LogConnectivityCache.invalidateInChunk(level, chunk.getPos()));
  }
}
//?}
