package fabiofdez.knots_and_rings.platform.forge;

//? forge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.platform.Platform;
import fabiofdez.knots_and_rings.resource.BuiltInResourcePack;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;

import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = KnotsAndRings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ForgeClientEventSubscriber {

  @SubscribeEvent
  public static void onClientSetup(final FMLClientSetupEvent event) {
    KnotsAndRings.onInitializeClient();
  }

  @SubscribeEvent
  public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
    GrowingSapling.TintHandler.registerTints(event::register);
  }

  @SubscribeEvent
  public static void addFeaturePacks(final AddPackFindersEvent event) {
    if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

    IModFileInfo modFileInfo = ModList.get().getModFileById(KnotsAndRings.MOD_ID);
    if (modFileInfo == null) return;
    IModFile modFile = modFileInfo.getFile();

    boolean hasFusion = KnotsAndRings.xplat().isModLoaded(ResourcePacks.FUSION_MOD_ID);

    if (hasFusion) addPack(modFile, event, ResourcePacks.PACK_FUSION);
    else addPack(modFile, event, ResourcePacks.PACK_CTM);

    addPack(modFile, event, ResourcePacks.PACK_DEFAULT);
  }

  private static void addPack(IModFile modFile, final AddPackFindersEvent event, BuiltInResourcePack pack) {
    Path sourcePath = modFile.findResource("resourcepacks/" + pack.id());

    Pack createdPack = Pack.readMetaAndCreate(
        pack.id(),
        pack.name(),
        false,
        (id) -> new PathPackResources(id, true, sourcePath),
        PackType.CLIENT_RESOURCES,
        Pack.Position.TOP,
        PackSource.BUILT_IN
    );

    event.addRepositorySource((packConsumer) -> packConsumer.accept(createdPack));
  }
}

*///?}
