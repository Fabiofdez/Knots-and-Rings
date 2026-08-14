package fabiofdez.knots_and_rings.platform.neoforge;

//? neoforge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.ModBlocks;
import fabiofdez.knots_and_rings.ModSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(KnotsAndRings.MOD_ID)
public class NeoforgeEntrypoint {

	public NeoforgeEntrypoint(IEventBus modEventBus, ModContainer ignored) {
		KnotsAndRings.onInitialize();
    ModBlocks.initialize();
    ModSounds.initialize();

    NeoForge.EVENT_BUS.register(this);
    ModSounds.register(modEventBus);
	}

	@SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
  }
}
*///?}
