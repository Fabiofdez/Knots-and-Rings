package fabiofdez.knots_and_rings.platform.fabric;

//? fabric {

import fabiofdez.knots_and_rings.feature.LogConnectivityCache;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.level.gameevent.GameEvent;

public class FabricEventSubscriber {

  public static void registerEvents() {
    ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> LogConnectivityCache.invalidateInChunk(chunk));
  }
}
//?}
