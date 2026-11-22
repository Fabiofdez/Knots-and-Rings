package fabiofdez.knots_and_rings.platform.neoforge;

//? if neoforge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.feature.LogClusterCache;
import fabiofdez.knots_and_rings.feature.SaplingType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

//? if > 1.21.1
import fabiofdez.knots_and_rings.resource.ResourcePacks;

@EventBusSubscriber(modid = KnotsAndRings.MOD_ID)
public class NeoforgeEventSubscriber {

  @SubscribeEvent
  public static void onChunkUnload(ChunkEvent.Unload event) {
    LogClusterCache.invalidateInChunk(event.getChunk().getLevel(), event.getChunk().getPos());
  }

  @SubscribeEvent
  public static void onResourceReload(AddReloadListenerEvent event) {
    ResourceManagerReloadListener onTagReload = (manager) -> SaplingType.resetPossibleShapes();
    event.addListener(/^? if > 1.21.1 { ^//^ResourcePacks.Server.TAG_RELOAD_ID, ^//^? } ^/onTagReload);
  }
}
*///? }
