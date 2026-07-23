package fabiofdez.knots_and_rings.platform.neoforge;

//? neoforge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.resource.BuiltInResourcePack;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = KnotsAndRings.MOD_ID, /^? if < 1.21.11 >> 'value' ^/ bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {

  @SubscribeEvent
  public static void onClientSetup(final FMLClientSetupEvent event) {
    KnotsAndRings.onInitializeClient();
  }

  @SubscribeEvent
  public static void addFeaturePacks(final AddPackFindersEvent event) {
    boolean hasFusion = KnotsAndRings.xplat().isModLoaded(ResourcePacks.FUSION_MOD_ID);

    if (hasFusion) addPack(event, ResourcePacks.PACK_FUSION);
    else addPack(event, ResourcePacks.PACK_CTM);
  }

  private static void addPack(final AddPackFindersEvent event, BuiltInResourcePack pack) {
    event.addPackFinders(
        KnotsAndRings.id("resourcepacks/" + pack.id()),
        PackType.CLIENT_RESOURCES,
        pack.name(),
        PackSource.BUILT_IN,
        false,
        Pack.Position.TOP
    );
  }
}
*///?}
