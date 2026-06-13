package fabiofdez.knots_and_rings.platform.fabric;

//? fabric {

import fabiofdez.knots_and_rings.KnotsAndRings;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;

import java.util.Optional;

@Entrypoint("client")
public class FabricClientEntrypoint implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    KnotsAndRings.onInitializeClient();

    Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(KnotsAndRings.MOD_ID);
    modContainer.ifPresent((container) -> ResourceManagerHelper.registerBuiltinResourcePack(
        KnotsAndRings.id("knots_and_rings_resources"),
        container,
        Component.literal("Wood Connected Textures"),
        ResourcePackActivationType.DEFAULT_ENABLED
    ));
  }
}
//?}
