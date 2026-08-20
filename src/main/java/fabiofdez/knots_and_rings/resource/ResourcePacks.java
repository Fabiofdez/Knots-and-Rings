package fabiofdez.knots_and_rings.resource;

public class ResourcePacks {
  public static final String FUSION_MOD_ID = "fusion";

  public static final BuiltInResourcePack PACK_DEFAULT;
  public static final BuiltInResourcePack PACK_FUSION;
  public static final BuiltInResourcePack PACK_CTM;

  public static final BuiltInResourcePack PACK_SAPLINGS;
  public static final BuiltInResourcePack PACK_STAY_TRUE_COMPAT;

  static {
    PACK_DEFAULT = BuiltInResourcePack.create("pack_default", "Connected Wood Resources", true);
    PACK_FUSION = BuiltInResourcePack.create("pack_fusion", "Connected Wood Extras (Fusion)");
    PACK_CTM = BuiltInResourcePack.create("pack_ctm", "Connected Wood Extras (CTM)");

    PACK_SAPLINGS = BuiltInResourcePack.create("pack_saplings", "Better Saplings", true);
    PACK_STAY_TRUE_COMPAT = BuiltInResourcePack.create("pack_stay_true_compat", "Better Saplings X Stay True");
  }
}
