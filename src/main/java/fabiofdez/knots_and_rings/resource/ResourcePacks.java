package fabiofdez.knots_and_rings.resource;

import fabiofdez.knots_and_rings.KnotsAndRings;

import java.util.function.Consumer;

public class ResourcePacks {
  public static final String FUSION_MOD_ID = "fusion";

  public static final BuiltInResourcePack PACK_DEFAULT;
  public static final BuiltInResourcePack PACK_FUSION;
  public static final BuiltInResourcePack PACK_CTM;
  //? !fabric && <= 1.21.1
  //public static final BuiltInResourcePack PACK_MODEL_FIX;
  public static final BuiltInResourcePack PACK_MODEL_FIX_FUSION;

  public static final BuiltInResourcePack PACK_SAPLINGS;
  public static final BuiltInResourcePack PACK_STAY_TRUE_COMPAT;

  public static void registerWith(Consumer<BuiltInResourcePack> handler) {
    handler.accept(PACK_DEFAULT);

    boolean hasFusion = KnotsAndRings.xplat().isModLoaded(FUSION_MOD_ID);
    if (hasFusion) handler.accept(ResourcePacks.PACK_FUSION);
    else handler.accept(ResourcePacks.PACK_CTM);

    if (hasFusion) handler.accept(PACK_MODEL_FIX_FUSION);
    //? !fabric && <= 1.21.1
    //else handler.accept(PACK_MODEL_FIX);

    handler.accept(PACK_SAPLINGS);
    handler.accept(PACK_STAY_TRUE_COMPAT);
  }

  static {
    PACK_DEFAULT = BuiltInResourcePack.create("pack_default", "Connected Wood Resources", true);
    PACK_FUSION = BuiltInResourcePack.create("pack_fusion", "Connected Wood Extras (Fusion)");
    PACK_CTM = BuiltInResourcePack.create("pack_ctm", "Connected Wood Extras (CTM)");
    //? !fabric && <= 1.21.1
    //PACK_MODEL_FIX = BuiltInResourcePack.create("pack_model_fix", "Connected Wood Model Fix", true);
    PACK_MODEL_FIX_FUSION = BuiltInResourcePack.create("pack_model_fix_fusion", "Connected Wood Model Fix", true);

    PACK_SAPLINGS = BuiltInResourcePack.create("pack_saplings", "Better Saplings", true);
    PACK_STAY_TRUE_COMPAT = BuiltInResourcePack.create("pack_stay_true_compat", "Better Saplings X Stay True");
  }
}
