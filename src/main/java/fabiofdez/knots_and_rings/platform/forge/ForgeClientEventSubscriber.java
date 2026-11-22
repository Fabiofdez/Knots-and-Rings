package fabiofdez.knots_and_rings.platform.forge;

//? if forge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.client.model.TexturedCompositeLoader;
import fabiofdez.knots_and_rings.feature.SaplingTint;
import fabiofdez.knots_and_rings.resource.PackRegistry;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = KnotsAndRings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeClientEventSubscriber {

  @SubscribeEvent
  public static void onClientSetup(final FMLClientSetupEvent event) {
    KnotsAndRings.onInitializeClient();
  }

  @SubscribeEvent
  public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
    event.register(TexturedCompositeLoader.ID.getPath(), TexturedCompositeLoader.INSTANCE);
  }

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
