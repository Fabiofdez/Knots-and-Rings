package fabiofdez.knots_and_rings.platform.neoforge;

//? if neoforge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
//? if 1.21.1
//import fabiofdez.knots_and_rings.client.model.TexturedCompositeLoader;
import fabiofdez.knots_and_rings.feature.SaplingTint;
import fabiofdez.knots_and_rings.resource.PackRegistry;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
//? if 1.21.1
//import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = KnotsAndRings.MOD_ID/^? if < 1.21.11 { ^/, bus = EventBusSubscriber.Bus.MOD/^? } ^/, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {

  @SubscribeEvent
  public static void onClientSetup(final FMLClientSetupEvent event) {
    KnotsAndRings.onInitializeClient();
  }

  //? if 1.21.1 {
  /^@SubscribeEvent
  public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
    event.register(TexturedCompositeLoader.ID, TexturedCompositeLoader.INSTANCE);
  }
  ^///? }

  @SubscribeEvent
  public static void registerBlockColorHandlers(RegisterColorHandlersEvent.BlockTintSources event) {
    SaplingTint.registerWith(event::register);
  }

  @SubscribeEvent
  public static void addFeaturePacks(final AddPackFindersEvent event) {
    ResourcePacks.Client.registerPacks(PackRegistry.resolve(event));
  }
}
*///? }
