package fabiofdez.knots_and_rings.platform.forge;

//? forge {

/*import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.ModBlocks;
import fabiofdez.knots_and_rings.ModSounds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(KnotsAndRings.MOD_ID)
public class ForgeEntrypoint {

  public ForgeEntrypoint() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    KnotsAndRings.onInitialize();
    ModBlocks.initialize();
    ModSounds.initialize();

    MinecraftForge.EVENT_BUS.register(this);
    ModSounds.register(modEventBus);
  }
}
*///?}
