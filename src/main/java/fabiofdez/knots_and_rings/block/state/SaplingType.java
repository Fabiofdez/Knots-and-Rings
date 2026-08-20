package fabiofdez.knots_and_rings.block.state;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public enum SaplingType implements StringRepresentable {
  NONE("none"),

  ACACIA("acacia", () -> Blocks.ACACIA_SAPLING, () -> Blocks.ACACIA_LEAVES),
  BIRCH("birch", () -> Blocks.BIRCH_SAPLING, () -> Blocks.BIRCH_LEAVES),
  CHERRY("cherry", () -> Blocks.CHERRY_SAPLING, () -> Blocks.CHERRY_LEAVES),
  DARK_OAK("dark_oak", () -> Blocks.DARK_OAK_SAPLING, () -> Blocks.DARK_OAK_LEAVES),
  JUNGLE("jungle", () -> Blocks.JUNGLE_SAPLING, () -> Blocks.JUNGLE_LEAVES),
  MANGROVE("mangrove", () -> Blocks.MANGROVE_PROPAGULE, () -> Blocks.MANGROVE_LEAVES),
  OAK("oak", () -> Blocks.OAK_SAPLING, () -> Blocks.OAK_LEAVES),
  //? > 1.21.1
  PALE_OAK("pale_oak", () -> Blocks.PALE_OAK_SAPLING, () -> Blocks.PALE_OAK_LEAVES),
  SPRUCE("spruce", () -> Blocks.SPRUCE_SAPLING, () -> Blocks.SPRUCE_LEAVES);

  private static Map<Block, SaplingType> SAPLING_TO_TYPE;
  private static Map<Block, SaplingType> STEM_TO_TYPE;
  private static Set<SaplingType> DEFINED_VALUES;

  private final String NAME;
  private final Supplier<Block> SAPLING;
  private final Supplier<Block> LEAVES;
  private Supplier<Block> STEM;

  SaplingType(String name) {
    this(name, () -> null, () -> null);
  }

  SaplingType(String name, Supplier<Block> sapling, Supplier<Block> leaves) {
    this.NAME = name;
    this.SAPLING = sapling;
    this.LEAVES = leaves;
  }

  public static Set<SaplingType> definedValues() {
    return DEFINED_VALUES;
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

  public void setStem(Supplier<Block> blockSupplier) {
    this.STEM = blockSupplier;
  }

  public static SaplingType of(Block sapling) {
    return SAPLING_TO_TYPE.getOrDefault(sapling, NONE);
  }

  public static SaplingType ofStem(Block saplingStem) {
    return STEM_TO_TYPE.getOrDefault(saplingStem, NONE);
  }

  public static void mapVanillaBlocks() {
    for (SaplingType type : values()) {
      if (type == NONE) continue;
      SAPLING_TO_TYPE.put(type.sapling(), type);
    }

    DEFINED_VALUES = ImmutableSet.copyOf(SAPLING_TO_TYPE.values());
  }

  public static void freezeRegistry() {
    for (SaplingType type : values()) {
      if (type == NONE) continue;
      STEM_TO_TYPE.put(type.stem(), type);
    }

    SAPLING_TO_TYPE = ImmutableMap.copyOf(SAPLING_TO_TYPE);
    STEM_TO_TYPE = ImmutableMap.copyOf(STEM_TO_TYPE);
  }

  @Override
  public String toString() {
    return NAME;
  }

  @NotNull
  @Override
  public String getSerializedName() {
    return toString();
  }

  static {
    SAPLING_TO_TYPE = new HashMap<>();
    STEM_TO_TYPE = new HashMap<>();
  }
}
