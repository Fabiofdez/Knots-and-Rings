package fabiofdez.knots_and_rings.feature;

import fabiofdez.knots_and_rings.ModBlocks.SaplingStems;
import fabiofdez.knots_and_rings.ModBlocks.TreeSeeds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static fabiofdez.knots_and_rings.feature.SaplingTypeID.*;
import static net.minecraft.world.level.block.Blocks.*;

public class SaplingType {

  private static Map<SaplingTypeID, SaplingType> TYPES = new LinkedHashMap<>();
  private static Map<Block, SaplingType> WOOD_TO_TYPE = new LinkedHashMap<>();
  private static Map<Block, SaplingType> LEAVES_TO_TYPE = new LinkedHashMap<>();
  private static Map<Block, SaplingType> SAPLING_TO_TYPE = new LinkedHashMap<>();
  private static Map<Block, SaplingType> STEM_TO_TYPE = new LinkedHashMap<>();
  private static Map<Block, SaplingType> SEED_TO_TYPE = new LinkedHashMap<>();
  private static Map<SaplingTypeID, PlacedStatePredicate> TYPES_PLACE_TRANSFORM = new LinkedHashMap<>();

  private final SaplingTypeID ID;
  private final Supplier<Block> WOOD;
  private final Supplier<Block> LEAVES;
  private final Supplier<Block> SAPLING;
  private final Supplier<Integer> TINT;
  private Supplier<Block> STEM;
  private Supplier<Block> SEED;
  private Supplier<Item> STICK = () -> Items.STICK;

  public static final SaplingType NONE = new SaplingType();

  private SaplingType() {
    this(SaplingTypeID.NONE, () -> null, () -> null, () -> null);
  }

  SaplingType(SaplingTypeID id, Supplier<Block> sapling, Supplier<Block> leaves, Supplier<Block> wood) {
    this(id, sapling, leaves, wood, () -> FoliageColor.FOLIAGE_DEFAULT);
  }

  SaplingType(SaplingTypeID id, Supplier<Block> sapling, Supplier<Block> leaves, Supplier<Block> wood, Supplier<Integer> tint) {
    this.ID = id;
    this.SAPLING = sapling;
    this.LEAVES = leaves;
    this.WOOD = wood;
    this.TINT = tint;
  }

  public SaplingTypeID id() {
    return ID;
  }

  public Block wood() {
    return WOOD.get();
  }

  public Block leaves() {
    return LEAVES.get();
  }

  public Block sapling() {
    return SAPLING.get();
  }

  public Block stem() {
    return STEM.get();
  }

  public Block seed() {
    return SEED.get();
  }

  public Item stick() {
    return STICK.get();
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

  public void setStick(Supplier<Item> itemSupplier) {
    this.STICK = itemSupplier;
  }

  public BlockState placedSapling(BlockGetter level, BlockPos pos) {
    return placedState(sapling().defaultBlockState(), level, pos);
  }

  public BlockState placedStem(BlockGetter level, BlockPos pos) {
    return placedState(stem().defaultBlockState(), level, pos);
  }

  public BlockState placedState(BlockState state, BlockGetter level, BlockPos pos) {
    return TYPES_PLACE_TRANSFORM.getOrDefault(id(), PlacedStatePredicate.DEFAULT).resolve(state, level, pos);
  }

  public static SaplingType of(SaplingTypeID type) {
    if (SAPLING_TO_TYPE.isEmpty()) return NONE;
    return TYPES.getOrDefault(type, NONE);
  }

  public static SaplingType ofWood(Block wood) {
    return WOOD_TO_TYPE.getOrDefault(wood, NONE);
  }

  public static SaplingType ofLeaves(Block leaves) {
    return LEAVES_TO_TYPE.getOrDefault(leaves, NONE);
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

  public static void add(SaplingTypeID type, Supplier<Block> sapling, Supplier<Block> leaves, Supplier<Block> wood) {
    TYPES.put(type, new SaplingType(type, sapling, leaves, wood));
  }

  public static void add(SaplingTypeID type, Supplier<Block> sapling, Supplier<Block> leaves, Supplier<Block> wood, Supplier<Integer> tint) {
    TYPES.put(type, new SaplingType(type, sapling, leaves, wood, tint));
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

      WOOD_TO_TYPE.put(type.wood(), type);
      LEAVES_TO_TYPE.put(type.leaves(), type);
      SAPLING_TO_TYPE.put(type.sapling(), type);
      STEM_TO_TYPE.put(type.stem(), type);
      SEED_TO_TYPE.put(type.seed(), type);
    }

    TYPES_PLACE_TRANSFORM = Collections.unmodifiableMap(TYPES_PLACE_TRANSFORM);
    WOOD_TO_TYPE = Collections.unmodifiableMap(WOOD_TO_TYPE);
    LEAVES_TO_TYPE = Collections.unmodifiableMap(LEAVES_TO_TYPE);
    SAPLING_TO_TYPE = Collections.unmodifiableMap(SAPLING_TO_TYPE);
    STEM_TO_TYPE = Collections.unmodifiableMap(STEM_TO_TYPE);
    SEED_TO_TYPE = Collections.unmodifiableMap(SEED_TO_TYPE);
  }

  public static Block lastSaplingInOrder() {
    SaplingType lastType = of(PALE_OAK);
    if (lastType == SaplingType.NONE) lastType = of(CHERRY);

    return lastType.sapling();
  }

  public static Set<SaplingType> all() {
    return Set.copyOf(TYPES.values());
  }

  @Override
  public String toString() {
    return ID.toString();
  }

  static {
    add(ACACIA, () -> ACACIA_SAPLING, () -> ACACIA_LEAVES, () -> ACACIA_LOG);
    add(BIRCH, () -> BIRCH_SAPLING, () -> BIRCH_LEAVES, () -> BIRCH_LOG, () -> FoliageColor.FOLIAGE_BIRCH);
    add(CHERRY, () -> CHERRY_SAPLING, () -> CHERRY_LEAVES, () -> CHERRY_LOG);
    add(DARK_OAK, () -> DARK_OAK_SAPLING, () -> DARK_OAK_LEAVES, () -> DARK_OAK_LOG);
    add(JUNGLE, () -> JUNGLE_SAPLING, () -> JUNGLE_LEAVES, () -> JUNGLE_LOG);
    add(MANGROVE, () -> MANGROVE_PROPAGULE, () -> MANGROVE_LEAVES, () -> MANGROVE_LOG);
    add(OAK, () -> OAK_SAPLING, () -> OAK_LEAVES, () -> OAK_LOG);
    //? if > 1.21.1
    add(PALE_OAK, () -> PALE_OAK_SAPLING, () -> PALE_OAK_LEAVES, () -> PALE_OAK_LOG);
    add(SPRUCE, () -> SPRUCE_SAPLING, () -> SPRUCE_LEAVES, () -> SPRUCE_LOG, () -> FoliageColor.FOLIAGE_EVERGREEN);

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

    BlockState resolve(BlockState state, BlockGetter level, BlockPos pos);
  }
}
