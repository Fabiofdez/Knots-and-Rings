package fabiofdez.knots_and_rings.platform.forge;

//? if forge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.feature.LogClusterCache;
import fabiofdez.knots_and_rings.feature.SaplingType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KnotsAndRings.MOD_ID)
public class ForgeEventSubscriber {

  @SubscribeEvent
  public static void onChunkUnload(ChunkEvent.Unload event) {
    LogClusterCache.invalidateInChunk((Level) event.getLevel(), event.getChunk().getPos());
  }

  @SubscribeEvent
  public static void onResourceReload(AddReloadListenerEvent event) {
    event.addListener((ResourceManagerReloadListener) (manager) -> SaplingType.resetPossibleShapes());
  }
}
*///? }
