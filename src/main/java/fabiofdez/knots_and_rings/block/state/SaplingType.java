package fabiofdez.knots_and_rings.block.state;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import fabiofdez.knots_and_rings.ModBlocks.SaplingStems;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class SaplingType {

  private static Set<SaplingType> TYPES = new HashSet<>();
  private static Map<Block, SaplingType> SAPLING_TO_TYPE = new HashMap<>();
  private static Map<Block, SaplingType> STEM_TO_TYPE = new HashMap<>();

  private final String NAME;
  private final Supplier<Block> SAPLING;
  private final Supplier<Block> LEAVES;
  private final Supplier<Integer> TINT;
  private Supplier<Block> STEM;

  public static final SaplingType NONE;

  SaplingType(String name, Supplier<Block> sapling, Supplier<Block> leaves) {
    this(name, sapling, leaves, () -> FoliageColor.FOLIAGE_DEFAULT);
  }

  SaplingType(String name, Supplier<Block> sapling, Supplier<Block> leaves, Supplier<Integer> tint) {
    this.NAME = name;
    this.SAPLING = sapling;
    this.LEAVES = leaves;
    this.TINT = tint;
  }

  public Block sapling() {
    return SAPLING.get();
  }

  public Block leaves() {
    return LEAVES.get();
  }

  public Block stem() {
    return STEM.get();
  }

  public int tint() {
    return TINT.get();
  }

  public void setStem(Supplier<Block> blockSupplier) {
    this.STEM = blockSupplier;
  }

  public static SaplingType ofSapling(Block sapling) {
    return SAPLING_TO_TYPE.getOrDefault(sapling, NONE);
  }

  public static SaplingType ofStem(Block saplingStem) {
    return STEM_TO_TYPE.getOrDefault(saplingStem, NONE);
  }

  public static SaplingType resolve(Block block) {
    SaplingType type = ofSapling(block);
    if (type == NONE) type = ofStem(block);
    return type;
  }

  public static void add(String name, Supplier<Block> sapling, Supplier<Block> leaves) {
    TYPES.add(new SaplingType(name, sapling, leaves));
  }

  public static void add(String name, Supplier<Block> sapling, Supplier<Block> leaves, Supplier<Integer> tint) {
    TYPES.add(new SaplingType(name, sapling, leaves, tint));
  }

  public static void freezeTypes() {
    TYPES = ImmutableSet.copyOf(TYPES);

    for (SaplingType type : TYPES) {
      SaplingStems.mapStemFor(type);

      SAPLING_TO_TYPE.put(type.sapling(), type);
      STEM_TO_TYPE.put(type.stem(), type);
    }

    SAPLING_TO_TYPE = ImmutableMap.copyOf(SAPLING_TO_TYPE);
    STEM_TO_TYPE = ImmutableMap.copyOf(STEM_TO_TYPE);
  }

  @Override
  public String toString() {
    return NAME;
  }

  static {
    NONE = new SaplingType("none", () -> null, () -> null);

    add("acacia", () -> Blocks.ACACIA_SAPLING, () -> Blocks.ACACIA_LEAVES);
    add("birch", () -> Blocks.BIRCH_SAPLING, () -> Blocks.BIRCH_LEAVES, () -> FoliageColor.FOLIAGE_BIRCH);
    add("cherry", () -> Blocks.CHERRY_SAPLING, () -> Blocks.CHERRY_LEAVES);
    add("dark_oak", () -> Blocks.DARK_OAK_SAPLING, () -> Blocks.DARK_OAK_LEAVES);
    add("jungle", () -> Blocks.JUNGLE_SAPLING, () -> Blocks.JUNGLE_LEAVES);
    add("mangrove", () -> Blocks.MANGROVE_PROPAGULE, () -> Blocks.MANGROVE_LEAVES);
    add("oak", () -> Blocks.OAK_SAPLING, () -> Blocks.OAK_LEAVES);
    //? > 1.21.1
    add("pale_oak", () -> Blocks.PALE_OAK_SAPLING, () -> Blocks.PALE_OAK_LEAVES);
    add("spruce", () -> Blocks.SPRUCE_SAPLING, () -> Blocks.SPRUCE_LEAVES, () -> FoliageColor.FOLIAGE_EVERGREEN);
  }
}
