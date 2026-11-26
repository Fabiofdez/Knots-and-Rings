package fabiofdez.knots_and_rings.feature;

import com.google.common.collect.ImmutableMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

import static fabiofdez.knots_and_rings.ModTags.*;
import static fabiofdez.knots_and_rings.feature.SaplingTypeID.*;
import static java.util.Map.entry;

public class PossibleSaplingShapes {

  private static final List<TagKey<Block>> MAX_SINGLETON = List.of(GROWS_SINGLETON);
  private static final List<TagKey<Block>> MAX_SQ_2X2 = List.of(GROWS_SINGLETON, GROWS_SQUARE_SM);
  private static final List<TagKey<Block>> MAX_SQ_3X3 = List.of(GROWS_SINGLETON, GROWS_SQUARE_SM, GROWS_SQUARE_LG);

  public static final Map<SaplingTypeID, List<TagKey<Block>>> VANILLA = ImmutableMap.ofEntries(
      entry(ACACIA, MAX_SINGLETON),
      entry(BIRCH, MAX_SINGLETON),
      entry(CHERRY, MAX_SINGLETON),
      entry(MANGROVE, MAX_SINGLETON),
      entry(OAK, MAX_SINGLETON),

      entry(DARK_OAK, MAX_SQ_2X2),
      entry(JUNGLE, MAX_SQ_2X2),
      entry(PALE_OAK, MAX_SQ_2X2),
      entry(SPRUCE, MAX_SQ_2X2)
  );

  public static final Map<SaplingTypeID, List<TagKey<Block>>> ARBORIA_SAPLINGS = ImmutableMap.ofEntries(
      entry(BIRCH, MAX_SQ_2X2),
      entry(CHERRY, MAX_SQ_2X2),
      entry(MANGROVE, MAX_SQ_2X2),
      entry(OAK, MAX_SQ_2X2),

      entry(ACACIA, MAX_SQ_3X3),
      entry(DARK_OAK, MAX_SQ_3X3),
      entry(JUNGLE, MAX_SQ_3X3),
      entry(PALE_OAK, MAX_SQ_3X3),
      entry(SPRUCE, MAX_SQ_3X3)
  );
}
