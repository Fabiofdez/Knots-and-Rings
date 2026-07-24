package fabiofdez.knots_and_rings.resource;

public class ResourcePacks {
  public static final String FUSION_MOD_ID = "fusion";

  public static final BuiltInResourcePack PACK_DEFAULT;
  public static final BuiltInResourcePack PACK_FUSION;
  public static final BuiltInResourcePack PACK_CTM;

  static {
    PACK_DEFAULT = BuiltInResourcePack.create("pack_default", "Wood Resources (Base)", true);
    PACK_FUSION = BuiltInResourcePack.create("pack_fusion", "Wood Resources (Fusion)", true);
    PACK_CTM = BuiltInResourcePack.create("pack_ctm", "Wood Resources (CTM)", true);
  }
}
