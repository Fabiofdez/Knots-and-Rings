package fabiofdez.knots_and_rings.platform.fabric;

//? fabric {

import fabiofdez.knots_and_rings.util.LogConnectivityCache;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

public class FabricEventSubscriber {

  public static void registerEvents() {
    ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> LogConnectivityCache.invalidateInChunk(chunk));
  }
}
//?}
