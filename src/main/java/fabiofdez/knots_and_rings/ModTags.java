package fabiofdez.knots_and_rings;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {

  public static final TagKey<Block> GROWS_SQUARE_LG = createBlockTag("sapling_grows_3x3");
  public static final TagKey<Block> GROWS_SQUARE_SM = createBlockTag("sapling_grows_2x2");
  public static final TagKey<Block> GROWS_SINGLETON = createBlockTag("sapling_grows_singleton");

  private static TagKey<Block> createBlockTag(String path) {
    return TagKey.create(Registries.BLOCK, KnotsAndRings.id(path));
  }
}
