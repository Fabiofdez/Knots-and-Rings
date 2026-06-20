package fabiofdez.knots_and_rings.platform.neoforge;

//? neoforge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = KnotsAndRings.MOD_ID, /^? if < 1.21.11 >> 'value' ^/ bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {

	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		KnotsAndRings.onInitializeClient();
	}
}
*///?}
