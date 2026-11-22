package fabiofdez.knots_and_rings.resource;

import fabiofdez.knots_and_rings.KnotsAndRings;

import java.util.function.Consumer;

//? if forge {
/*import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;

import java.nio.file.Path;
*///? } else if neoforge {
/*import net.neoforged.neoforge.event.AddPackFindersEvent;
 *///? } else {
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
//? }

//? if !fabric {
/*import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
*///? }

public class PackRegistry {

  //? if forge {
  /*public static Consumer<BuiltInResourcePack> resolve(final AddPackFindersEvent event) {
    IModFileInfo modFileInfo = ModList.get().getModFileById(KnotsAndRings.MOD_ID);
    IModFile modFile = modFileInfo.getFile();

    return (pack) -> {
      Path sourcePath = modFile.findResource("resourcepacks/" + pack.id());

      Pack createdPack = Pack.readMetaAndCreate(
          pack.id(),
          pack.name(),
          pack.defaultEnabled(),
          (id) -> new PathPackResources(id, true, sourcePath),
          pack.packType(),
          Pack.Position.TOP,
          PackSource.BUILT_IN
      );

      event.addRepositorySource((packConsumer) -> packConsumer.accept(createdPack));
    };
  }
  *///? } else if neoforge {
  /*public static Consumer<BuiltInResourcePack> resolve(final AddPackFindersEvent event) {
    return (pack) -> event.addPackFinders(
        KnotsAndRings.id("resourcepacks/" + pack.id()),
        pack.packType(),
        pack.name(),
        PackSource.BUILT_IN,
        pack.defaultEnabled(),
        Pack.Position.TOP
    );
  }
  *///? } else {
  public static Consumer<BuiltInResourcePack> resolve(ModContainer container) {
    return (pack) -> ResourceLoader.registerBuiltinPack(
        KnotsAndRings.id(pack.id()),
        container,
        pack.name(),
        pack.defaultEnabled() ? PackActivationType.DEFAULT_ENABLED : PackActivationType.NORMAL
    );
  }
  //? }
}
