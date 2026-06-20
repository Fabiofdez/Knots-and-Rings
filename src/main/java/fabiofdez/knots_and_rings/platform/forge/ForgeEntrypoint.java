package fabiofdez.knots_and_rings.platform.forge;

//? forge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.resource.PathPackResources;

import java.nio.file.Path;

@Mod(KnotsAndRings.MOD_ID)
public class ForgeEntrypoint {

	public ForgeEntrypoint() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		KnotsAndRings.onInitialize();

    MinecraftForge.EVENT_BUS.register(this);
    ModSounds.register(modEventBus);

    modEventBus.addListener(ForgeEntrypoint::addFeaturePacks);
	}

  public static void addFeaturePacks(final AddPackFindersEvent event) {
    if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

    IModFileInfo modFileInfo = ModList.get().getModFileById(KnotsAndRings.MOD_ID);
    if (modFileInfo == null) return;

    String packId = "knots_and_rings_resources";
    Path sourcePath = modFileInfo.getFile().findResource("resourcepacks/" + packId);

    Pack pack = Pack.readMetaAndCreate(
        packId,
        Component.literal("Wood Connected Textures"),
        true,
        (id) -> new PathPackResources(id, true, sourcePath),
        PackType.CLIENT_RESOURCES,
        Pack.Position.TOP,
        PackSource.BUILT_IN
    );

    event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
  }
}
*///?}
