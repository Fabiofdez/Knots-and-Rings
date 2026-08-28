package fabiofdez.knots_and_rings.platform.neoforge;

//? neoforge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
//import fabiofdez.knots_and_rings.feature.LivingWoodBlock;
import fabiofdez.knots_and_rings.feature.SaplingTint;
import fabiofdez.knots_and_rings.resource.BuiltInResourcePack;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
//import net.minecraft.client.renderer.ItemBlockRenderTypes;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = KnotsAndRings.MOD_ID, /^? if < 1.21.11 >> 'value' ^/ bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {

  @SubscribeEvent
  public static void onClientSetup(final FMLClientSetupEvent event) {
    KnotsAndRings.onInitializeClient();

//    BuiltInRegistries.BLOCK.entrySet().forEach((entry) -> {
//      ResourceLocation blockId = KnotsAndRings.fromKey(entry.getKey());
//      if (!LivingWoodBlock.isWoodBlock(blockId)) return;
//      ItemBlockRenderTypes.setRenderLayer(entry.getValue(), RenderType.translucent());
//    });
  }

  @SubscribeEvent
  public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
    SaplingTint.registerWith(event::register);
  }

  @SubscribeEvent
  public static void addFeaturePacks(final AddPackFindersEvent event) {
    ResourcePacks.registerWith((pack) -> addPack(event, pack));
  }

  private static void addPack(final AddPackFindersEvent event, BuiltInResourcePack pack) {
    event.addPackFinders(
        KnotsAndRings.id("resourcepacks/" + pack.id()),
        PackType.CLIENT_RESOURCES,
        pack.name(),
        PackSource.BUILT_IN,
        pack.defaultEnabled(),
        Pack.Position.TOP
    );
  }
}
*///?}
