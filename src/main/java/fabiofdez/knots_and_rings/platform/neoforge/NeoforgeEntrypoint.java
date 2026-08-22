package fabiofdez.knots_and_rings.platform.neoforge;

//? neoforge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.ModBlocks;
import fabiofdez.knots_and_rings.ModItems;
import fabiofdez.knots_and_rings.ModSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(KnotsAndRings.MOD_ID)
public class NeoforgeEntrypoint {

	public NeoforgeEntrypoint(IEventBus modEventBus, ModContainer ignored) {
		KnotsAndRings.onInitialize();

    ModBlocks.register(modEventBus);
    ModItems.register(modEventBus);
    ModSounds.register(modEventBus);
    NeoForge.EVENT_BUS.register(this);

    modEventBus.addListener(this::commonSetup);
	}

  private void commonSetup(final FMLCommonSetupEvent event) {
    ModBlocks.initialize();
    ModItems.initialize();
    ModSounds.initialize();
  }

	@SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
  }
}
*///?}
