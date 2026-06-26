package fabiofdez.knots_and_rings.platform.fabric;

//? fabric {

import fabiofdez.knots_and_rings.KnotsAndRings;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import fabiofdez.knots_and_rings.resource.BuiltInResourcePack;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    KnotsAndRings.onInitializeClient();

    FabricLoader.getInstance().getModContainer(KnotsAndRings.MOD_ID).ifPresent((container) -> {
      boolean hasFusion = KnotsAndRings.xplat().isModLoaded(ResourcePacks.FUSION_MOD_ID);

      if (hasFusion) addPack(container, ResourcePacks.PACK_FUSION);
      else addPack(container, ResourcePacks.PACK_CTM);
    });
  }

  private static void addPack(ModContainer container, BuiltInResourcePack pack) {
    ResourceManagerHelper.registerBuiltinResourcePack(
        KnotsAndRings.id(pack.id()),
        container,
        pack.name(),
        activationFor(pack)
    );
  }

  private static ResourcePackActivationType activationFor(BuiltInResourcePack pack) {
    return pack.defaultEnabled() ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL;
  }
}
//?}
