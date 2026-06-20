package fabiofdez.knots_and_rings.platform.neoforge;

//? neoforge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(KnotsAndRings.MOD_ID)
public class NeoforgeEntrypoint {

	public NeoforgeEntrypoint(IEventBus modEventBus, ModContainer ignored) {
		KnotsAndRings.onInitialize();

    NeoForge.EVENT_BUS.register(this);
    ModSounds.register(modEventBus);

    modEventBus.addListener(NeoforgeEntrypoint::addFeaturePacks);
	}

	@SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
  }

  public static void addFeaturePacks(final AddPackFindersEvent event) {
    event.addPackFinders(
        KnotsAndRings.id("resourcepacks/knots_and_rings_resources"),
        PackType.CLIENT_RESOURCES,
        Component.literal("Wood Connected Textures"),
        PackSource.BUILT_IN,
        false,
        Pack.Position.TOP
    );
  }
}
*///?}
