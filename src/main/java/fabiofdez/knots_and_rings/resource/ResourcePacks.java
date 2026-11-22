package fabiofdez.knots_and_rings.resource;

import fabiofdez.knots_and_rings.KnotsAndRings;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

import static fabiofdez.knots_and_rings.resource.BuiltInResourcePack.*;

public class ResourcePacks {
  public static final String FUSION_MOD_ID = "fusion";
  public static final String VANILLA_BACKPORT_MOD_ID = "vanillabackport";

  public static final BuiltInResourcePack PACK_DEFAULT;
  public static final BuiltInResourcePack PACK_FUSION;
  public static final BuiltInResourcePack PACK_CTM;
  //? if !fabric && <= 1.21.1
  //public static final BuiltInResourcePack PACK_MODEL_FIX;
  public static final BuiltInResourcePack PACK_MODEL_FIX_FUSION;

  public static final BuiltInResourcePack PACK_SAPLINGS;
  public static final BuiltInResourcePack PACK_STAY_TRUE_COMPAT;

  public static final BuiltInResourcePack PACK_VANILLA_BACKPORT_COMPAT;
  public static final BuiltInResourcePack PACK_ARBORIA_SAPLINGS_COMPAT;

  static {
    PACK_DEFAULT = ClientPack.create("pack_default", "Connected Wood Resources", true);
    PACK_FUSION = ClientPack.create("pack_fusion", "Connected Wood Extras (Fusion)");
    PACK_CTM = ClientPack.create("pack_ctm", "Connected Wood Extras (CTM)");
    //? if !fabric && <= 1.21.1
    //PACK_MODEL_FIX = ClientPack.create("pack_model_fix", "Connected Wood Model Fix", true);
    PACK_MODEL_FIX_FUSION = ClientPack.create("pack_model_fix_fusion", "Connected Wood Model Fix", true);

    PACK_SAPLINGS = ClientPack.create("pack_saplings", "Better Saplings", true);
    PACK_STAY_TRUE_COMPAT = ClientPack.create("pack_stay_true_compat", "Better Saplings X Stay True");

    PACK_VANILLA_BACKPORT_COMPAT = ServerPack.create("pack_vanilla_backport_compat", "Vanilla Backport Compat", true);
    PACK_ARBORIA_SAPLINGS_COMPAT = ServerPack.create("pack_arboria_saplings_compat", "Arboria Saplings Compat");
  }

  public static class Client {
    public static void registerPacks(Consumer<BuiltInResourcePack> handler) {
      handler.accept(PACK_DEFAULT);

      boolean hasFusion = KnotsAndRings.xplat().isModLoaded(FUSION_MOD_ID);
      if (hasFusion) handler.accept(PACK_FUSION);
      else handler.accept(PACK_CTM);

      if (hasFusion) handler.accept(PACK_MODEL_FIX_FUSION);
      //? if !fabric && <= 1.21.1
      //else handler.accept(PACK_MODEL_FIX);

      handler.accept(PACK_SAPLINGS);
      handler.accept(PACK_STAY_TRUE_COMPAT);
    }
  }

  public static class Server {
    public static final Identifier TAG_RELOAD_ID = KnotsAndRings.id("tag_reload");

    public static void registerPacks(Consumer<BuiltInResourcePack> handler) {
      boolean hasVanillabackport = KnotsAndRings.xplat().isModLoaded(VANILLA_BACKPORT_MOD_ID);
      if (hasVanillabackport) handler.accept(PACK_VANILLA_BACKPORT_COMPAT);

      handler.accept(PACK_ARBORIA_SAPLINGS_COMPAT);
    }
  }
}
