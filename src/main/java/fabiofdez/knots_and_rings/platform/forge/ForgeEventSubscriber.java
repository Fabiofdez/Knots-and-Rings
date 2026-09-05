package fabiofdez.knots_and_rings.platform.forge;

//? forge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.feature.LogConnectivityCache;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = KnotsAndRings.MOD_ID)
public class ForgeEventSubscriber {

  @SubscribeEvent
  public static void onChunkUnload(ChunkEvent.Unload event) {
    if (!((event.getLevel()) instanceof Level level)) return;
    LogConnectivityCache.invalidateInChunk(level, event.getChunk().getPos());
  }
}
*///?}
