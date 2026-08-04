package fabiofdez.knots_and_rings.platform.forge;

//? forge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.util.LogConnectivityCache;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KnotsAndRings.MOD_ID)
public class ForgeEventSubscriber {

  @SubscribeEvent
  public static void onChunkUnload(ChunkEvent.Unload event) {
    LogConnectivityCache.invalidateInChunk(event.getChunk());
  }
}
*///?}
