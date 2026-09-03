package fabiofdez.knots_and_rings.feature;

import fabiofdez.knots_and_rings.ModBlocks.SaplingStems;
import fabiofdez.knots_and_rings.ModBlocks.TreeSeeds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static fabiofdez.knots_and_rings.feature.SaplingTypeID.*;

public class SaplingType {

  private static Map<SaplingTypeID, SaplingType> TYPES = new LinkedHashMap<>();
  private static Map<Block, SaplingType> SAPLING_TO_TYPE = new LinkedHashMap<>();
  private static Map<Block, SaplingType> STEM_TO_TYPE = new LinkedHashMap<>();
  private static Map<Block, SaplingType> SEED_TO_TYPE = new LinkedHashMap<>();
  private static Map<SaplingTypeID, PlacedStatePredicate> TYPES_PLACE_TRANSFORM = new LinkedHashMap<>();

  private final SaplingTypeID ID;
  private final Supplier<Block> SAPLING;
  private final Supplier<Block> LEAVES;
  private final Supplier<Integer> TINT;
  private Supplier<Block> STEM;
  private Supplier<Block> SEED;

  public static final SaplingType NONE;

  SaplingType(SaplingTypeID id, Supplier<Block> sapling, Supplier<Block> leaves) {
    this(id, sapling, leaves, () -> FoliageColor.FOLIAGE_DEFAULT);
  }

  SaplingType(SaplingTypeID id, Supplier<Block> sapling, Supplier<Block> leaves, Supplier<Integer> tint) {
    this.ID = id;
    this.SAPLING = sapling;
    this.LEAVES = leaves;
    this.TINT = tint;
  }

  public SaplingTypeID id() {
    return ID;
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

  public Block seed() {
    return SEED.get();
  }

  public int tint() {
    return TINT.get();
  }

  public void setStem(Supplier<Block> blockSupplier) {
    this.STEM = blockSupplier;
  }

  public void setSeed(Supplier<Block> blockSupplier) {
    this.SEED = blockSupplier;
  }

  public BlockState placedSapling(Level level, BlockPos pos) {
    return placedState(sapling().defaultBlockState(), level, pos);
  }

  public BlockState placedStem(Level level, BlockPos pos) {
    return placedState(stem().defaultBlockState(), level, pos);
  }

  public BlockState placedState(BlockState state, Level level, BlockPos pos) {
    return TYPES_PLACE_TRANSFORM.getOrDefault(id(), PlacedStatePredicate.DEFAULT).resolve(state, level, pos);
  }

  public static SaplingType of(SaplingTypeID type) {
    if (SAPLING_TO_TYPE.isEmpty()) return NONE;
    return TYPES.getOrDefault(type, NONE);
  }

  public static SaplingType ofSapling(Block sapling) {
    return SAPLING_TO_TYPE.getOrDefault(sapling, NONE);
  }

  public static SaplingType ofStem(Block saplingStem) {
    return STEM_TO_TYPE.getOrDefault(saplingStem, NONE);
  }

  public static SaplingType ofSeed(Block saplingSeed) {
    return SEED_TO_TYPE.getOrDefault(saplingSeed, NONE);
  }

  public static SaplingType resolve(Block block) {
    SaplingType type = ofSapling(block);
    if (type == NONE) type = ofStem(block);
    if (type == NONE) type = ofSeed(block);
    return type;
  }

  public static void add(SaplingTypeID type, Supplier<Block> sapling, Supplier<Block> leaves) {
    TYPES.put(type, new SaplingType(type, sapling, leaves));
  }

  public static void add(SaplingTypeID type, Supplier<Block> sapling, Supplier<Block> leaves, Supplier<Integer> tint) {
    TYPES.put(type, new SaplingType(type, sapling, leaves, tint));
  }

  public static void registerPlacedState(SaplingTypeID type, PlacedStatePredicate predicate) {
    TYPES_PLACE_TRANSFORM.put(type, predicate);
  }

  public static void freezeTypes() {
    if (!SAPLING_TO_TYPE.isEmpty()) return;

    TYPES = Collections.unmodifiableMap(TYPES);

    for (SaplingType type : TYPES.values()) {
      SaplingStems.mapStemFor(type);
      TreeSeeds.mapSeedFor(type);

      SAPLING_TO_TYPE.put(type.sapling(), type);
      STEM_TO_TYPE.put(type.stem(), type);
      SEED_TO_TYPE.put(type.seed(), type);
    }

    TYPES_PLACE_TRANSFORM = Collections.unmodifiableMap(TYPES_PLACE_TRANSFORM);
    SAPLING_TO_TYPE = Collections.unmodifiableMap(SAPLING_TO_TYPE);
    STEM_TO_TYPE = Collections.unmodifiableMap(STEM_TO_TYPE);
    SEED_TO_TYPE = Collections.unmodifiableMap(SEED_TO_TYPE);
  }

  public static Block lastSaplingInOrder() {
    SaplingType lastType = of(PALE_OAK);
    if (lastType == SaplingType.NONE) lastType = of(CHERRY);

    return lastType.sapling();
  }

  @Override
  public String toString() {
    return ID.toString();
  }

  static {
    NONE = new SaplingType(SaplingTypeID.NONE, () -> null, () -> null);

    add(ACACIA, () -> Blocks.ACACIA_SAPLING, () -> Blocks.ACACIA_LEAVES);
    add(BIRCH, () -> Blocks.BIRCH_SAPLING, () -> Blocks.BIRCH_LEAVES, () -> FoliageColor.FOLIAGE_BIRCH);
    add(CHERRY, () -> Blocks.CHERRY_SAPLING, () -> Blocks.CHERRY_LEAVES);
    add(DARK_OAK, () -> Blocks.DARK_OAK_SAPLING, () -> Blocks.DARK_OAK_LEAVES);
    add(JUNGLE, () -> Blocks.JUNGLE_SAPLING, () -> Blocks.JUNGLE_LEAVES);
    add(MANGROVE, () -> Blocks.MANGROVE_PROPAGULE, () -> Blocks.MANGROVE_LEAVES);
    add(OAK, () -> Blocks.OAK_SAPLING, () -> Blocks.OAK_LEAVES);
    //? > 1.21.1
    add(PALE_OAK, () -> Blocks.PALE_OAK_SAPLING, () -> Blocks.PALE_OAK_LEAVES);
    add(SPRUCE, () -> Blocks.SPRUCE_SAPLING, () -> Blocks.SPRUCE_LEAVES, () -> FoliageColor.FOLIAGE_EVERGREEN);

    MANGROVE.registerPlacedState((state, level, pos) -> {
      FluidState fluid = level.getFluidState(pos);
      boolean isWaterlogged = fluid.getType() == Fluids.WATER;
      return state
          .setValue(BlockStateProperties.WATERLOGGED, isWaterlogged)
          .setValue(MangrovePropaguleBlock.AGE, MangrovePropaguleBlock.MAX_AGE);
    });
  }

  @FunctionalInterface
  public interface PlacedStatePredicate {
    PlacedStatePredicate DEFAULT = (state, level, pos) -> state;

    BlockState resolve(BlockState state, Level level, BlockPos pos);
  }
}
