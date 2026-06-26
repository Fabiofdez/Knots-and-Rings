package fabiofdez.knots_and_rings.resource;

public class ResourcePacks {
  public static final String FUSION_MOD_ID = "fusion";

  public static final BuiltInResourcePack PACK_CTM;
  public static final BuiltInResourcePack PACK_FUSION;

  static {
    PACK_CTM = BuiltInResourcePack.create("knots_and_rings_ctm", "Wood Connected Textures", true);
    PACK_FUSION = BuiltInResourcePack.create("knots_and_rings_fusion", "Wood Connected Textures", true);
  }
}
