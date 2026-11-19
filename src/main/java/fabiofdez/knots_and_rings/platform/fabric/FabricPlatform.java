package fabiofdez.knots_and_rings.platform.fabric;

//? if fabric {

import fabiofdez.knots_and_rings.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;
//? if <= 1.21.1
//import net.minecraft.client.Minecraft;

public class FabricPlatform implements Platform {

  @Override
  public boolean isModLoaded(String modId) {
    return FabricLoader.getInstance().isModLoaded(modId);
  }

  @Override
  public boolean isModLoading(String modId) {
    return this.isModLoaded(modId);
  }

  @Override
  public ModLoader loader() {
    return ModLoader.FABRIC;
  }

  @Override
  public String mcVersion() {
    //? if > 1.21.1
    return FabricLoader.getInstance().getRawGameVersion();
    //? if <= 1.21.1
    //return Minecraft.getInstance().getLaunchedVersion();
  }

  @Override
  public boolean isDevelopmentEnvironment() {
    return FabricLoader.getInstance().isDevelopmentEnvironment();
  }
}
//? }
