package fabiofdez.knots_and_rings.platform.neoforge;

//? neoforge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.util.LogConnectivityCache;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(modid = KnotsAndRings.MOD_ID)
public class NeoforgeEventSubscriber {

  @SubscribeEvent
  public static void onChunkUnload(ChunkEvent.Unload event) {
    LogConnectivityCache.invalidateInChunk(event.getChunk());
  }
}
*///?}
