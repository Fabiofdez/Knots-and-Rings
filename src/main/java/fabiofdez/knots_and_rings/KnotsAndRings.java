package fabiofdez.knots_and_rings;

import fabiofdez.knots_and_rings.platform.Platform;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import fabiofdez.knots_and_rings.platform.fabric.FabricPlatform;
//?} neoforge {
/*import fabiofdez.knots_and_rings.platform.neoforge.NeoforgePlatform;
 *///?} forge {
/*import fabiofdez.knots_and_rings.platform.forge.ForgePlatform;
 *///?}

@SuppressWarnings("LoggingSimilarMessage")
public class KnotsAndRings {

	public static final String MOD_ID = /*$ mod_id*/ "knots_and_rings";
	public static final String MOD_VERSION = /*$ mod_version*/ "1.4.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Knots & Rings";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {
		LOGGER.info("Initializing {} on {}", MOD_ID, KnotsAndRings.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public static void onInitializeClient() {
		LOGGER.info("Initializing {} Client on {}", MOD_ID, KnotsAndRings.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		 *///?} forge {
		/*return new ForgePlatform();
		 *///?}
	}

  public static ResourceLocation id(String path) {
    //? >= 1.21
    return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    //? < 1.21
    //return new ResourceLocation(MOD_ID, path);
  }

  public static ResourceLocation id(String namespace, String path) {
    //? >= 1.21
    return ResourceLocation.fromNamespaceAndPath(namespace, path);
    //? < 1.21
    //return new ResourceLocation(namespace, path);
  }
}
