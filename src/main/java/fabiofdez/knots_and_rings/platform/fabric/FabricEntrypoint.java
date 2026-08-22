package fabiofdez.knots_and_rings.platform.fabric;

//? fabric {

import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.ModBlocks;
import fabiofdez.knots_and_rings.ModItems;
import fabiofdez.knots_and_rings.ModSounds;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

  @Override
  public void onInitialize() {
    KnotsAndRings.onInitialize();
    ModBlocks.initialize();
    ModItems.initialize();
    ModSounds.initialize();

    FabricEventSubscriber.registerEvents();
  }
}
//?}
