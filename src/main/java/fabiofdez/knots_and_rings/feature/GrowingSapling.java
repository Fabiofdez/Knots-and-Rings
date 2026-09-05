package fabiofdez.knots_and_rings.feature;

import fabiofdez.knots_and_rings.block.state.BlockPosOffset;
import fabiofdez.knots_and_rings.util.ShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public class GrowingSapling {

  public static BlockState registerDefaultState(BlockState state) {
    if (!state.hasProperty(Properties.GROWTH_STAGE)) return state;
    return state
        .setValue(Properties.GIANT, false)
        .setValue(Properties.PRUNED, false)
        .setValue(Properties.GROWTH_STAGE, Stage.SAPLING)
        .setValue(Properties.TREE_SHAPE, SaplingShape.Layout.SINGLETON)
        .setValue(Properties.TREE_ROOT, BlockPosOffset.SELF)
        .setValue(Properties.HALF, DoubleBlockHalf.LOWER);
  }

  public static void initBlockStateDef(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(
        Properties.GIANT,
        Properties.PRUNED,
        Properties.GROWTH_STAGE,
        Properties.TREE_ROOT,
        Properties.TREE_SHAPE,
        Properties.HALF
    );
  }

  public static boolean canRandomTick(BlockState state, RandomSource random) {
    Stage stage = growthStage(state);
    boolean isUpper = half(state) == DoubleBlockHalf.UPPER;
    if (isPruned(state) || stage.LEQ(Stage.DECAYING) || isUpper) return false;

    return random.nextInt(stage.GT(Stage.SAPLING) ? 6 : 4) == 0;
  }

  public static boolean markedGiant(BlockState state) {
    return state.getValue(Properties.GIANT);
  }

  public static boolean isPruned(BlockState state) {
    return state.getValue(Properties.PRUNED);
  }

  public static Stage growthStage(BlockState state) {
    return state.getValue(Properties.GROWTH_STAGE);
  }

  public static SaplingShape.Layout treeShape(BlockState state) {
    return state.getValue(Properties.TREE_SHAPE);
  }

  public static BlockPosOffset offsetToRoot(BlockState state) {
    return state.getValue(Properties.TREE_ROOT);
  }

  public static DoubleBlockHalf half(BlockState state) {
    return state.getValue(Properties.HALF);
  }

  public static boolean isDoubleSapling(BlockState state) {
    return growthStage(state).GEQ(Stage.TALL_SAPLING);
  }

  public static boolean isGrowingSapling(BlockState state) {
    if (!state.hasProperty(Properties.GROWTH_STAGE)) return false;
    return SaplingType.resolve(state.getBlock()) != SaplingType.NONE;
  }

  public static boolean isImmature(BlockState state) {
    Stage stage = growthStage(state);
    Stage finalStage = markedGiant(state) ? Stage.GIANT : Stage.TALL_SAPLING;

    return stage.LT(finalStage);
  }

  public static boolean partsOfSameSapling(BlockState state1, BlockState state2) {
    if (!isGrowingSapling(state1) || !isGrowingSapling(state2)) return false;

    SaplingType thisType = SaplingType.resolve(state1.getBlock());
    if (thisType == SaplingType.NONE) return false;

    SaplingType otherType = SaplingType.resolve(state2.getBlock());
    if (otherType == SaplingType.NONE) return false;

    return thisType == otherType;
  }

  public static SaplingShape resolveTreeShape(BlockState state, BlockPos pos) {
    return treeShape(state).inPlace(offsetToRoot(state).from(pos));
  }

  public static BlockState makeSaplingRoot(BlockState state, SaplingShape.Layout layout, boolean advanceStage) {
    if (advanceStage && growthStage(state).GEQ(Stage.TALL_SAPLING)) {
      state = state.setValue(Properties.GROWTH_STAGE, Stage.GIANT);
    } else if (advanceStage) {
      state = state.cycle(Properties.GROWTH_STAGE);
    }

    return state
        .setValue(Properties.GIANT, layout != SaplingShape.Layout.SINGLETON)
        .setValue(Properties.TREE_SHAPE, layout)
        .setValue(Properties.TREE_ROOT, BlockPosOffset.SELF);
  }

  public static BlockState absorbSapling(BlockState state, SaplingShape.Layout layout, BlockPosOffset toRoot, RandomSource random) {
    Stage saplingStage = growthStage(state);
    if (saplingStage.GT(Stage.SPROUT)) saplingStage = Stage.SPROUT;

    if (random.nextInt(3) == 0) {
      saplingStage = saplingStage.GT(Stage.DECAYING) ? Stage.DECAYING : Stage.HIDDEN;
    }

    return state
        .setValue(Properties.GROWTH_STAGE, saplingStage)
        .setValue(Properties.GIANT, false)
        .setValue(Properties.TREE_SHAPE, layout)
        .setValue(Properties.TREE_ROOT, toRoot);
  }

  public static BlockState convertToSapling(BlockState state, BlockGetter level, BlockPos pos) {
    SaplingType type = SaplingType.resolve(state.getBlock());
    if (type == SaplingType.NONE) return state;

    return type
        .placedSapling(level, pos)
        .setValue(Properties.GIANT, markedGiant(state))
        .setValue(Properties.PRUNED, isPruned(state))
        .setValue(Properties.GROWTH_STAGE, growthStage(state))
        .setValue(Properties.TREE_SHAPE, treeShape(state))
        .setValue(Properties.TREE_ROOT, offsetToRoot(state))
        .setValue(Properties.HALF, half(state));
  }

  public static BlockState convertToStem(BlockState state, BlockGetter level, BlockPos pos) {
    SaplingType type = SaplingType.resolve(state.getBlock());
    if (type == SaplingType.NONE) return state;

    return type
        .placedStem(level, pos)
        .setValue(Properties.GIANT, markedGiant(state))
        .setValue(Properties.PRUNED, isPruned(state))
        .setValue(Properties.GROWTH_STAGE, growthStage(state))
        .setValue(Properties.TREE_SHAPE, treeShape(state))
        .setValue(Properties.TREE_ROOT, offsetToRoot(state))
        .setValue(Properties.HALF, half(state));
  }

  public static void stompOnSapling(BlockState state, Level level, BlockPos pos) {
    Stage newStage = growthStage(state) == Stage.SPROUT ? Stage.DECAYING : Stage.HIDDEN;
    if (newStage == Stage.HIDDEN) {
      if (offsetToRoot(state) == BlockPosOffset.SELF) {
        level.destroyBlock(pos, true);
        return;
      }

      level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
    }

    BlockState newState = state.setValue(Properties.GROWTH_STAGE, newStage);
    level.setBlockAndUpdate(pos, newState);
  }

  public static SaplingShape findShapeForSapling(BlockState state, BlockGetter level, BlockPos pos) {
    for (SaplingShape.Layout layout : SaplingShape.Layout.values()) {
      SaplingShape inPlace = layout.nearbySource(state, level, pos);
      if (!inPlace.isEmpty()) return inPlace;
    }

    return SaplingShape.NO_SHAPE;
  }

  public static Vec3i getPosForRandomOffset(BlockState state, Vec3i pos) {
    return half(state) == DoubleBlockHalf.UPPER ? pos.below() : pos;
  }

  public static List<ItemStack> getDrops(List<ItemStack> drops, BlockState state, ServerLevel level) {
    if (drops.isEmpty() || growthStage(state).LEQ(Stage.DECAYING)) return List.of();

    boolean isUpper = half(state) == DoubleBlockHalf.UPPER;
    boolean isTall = isDoubleSapling(state);
    RandomSource random = level.getRandom();
    if (isTall) drops.clear();

    if (isTall) {
      int numSticks = isUpper ? 0 : 2;
      numSticks += random.nextInt(3);
      if (growthStage(state) == Stage.GIANT) numSticks += random.nextInt(4);

      drops.addAll(Collections.nCopies(numSticks, Items.STICK.getDefaultInstance()));
    }

    if (!isTall || isUpper) {
      int numSaplings = treeShape(state).offsets().size();
      if (isUpper) numSaplings += random.nextInt(3);

      drops.addAll(Collections.nCopies(numSaplings, state.getBlock().asItem().getDefaultInstance()));
    }

    return drops;
  }

  public static VoxelShape getInteractShape(BlockState state, BlockGetter level, BlockPos pos) {
    VoxelShape selfShape = VisualShape.INTERACT.parse(state, pos);
    BlockPosOffset toRoot = offsetToRoot(state);

    if (toRoot == BlockPosOffset.SELF) {
      Vec3 rootOffset = treeShape(state).centerOffset();
      if (rootOffset.length() == 0) return selfShape;

      if (growthStage(state) == Stage.SPROUT) {
        VoxelShape singleSelfShape = selfShape.move(-rootOffset.x, 0, -rootOffset.z);
        return Shapes.or(selfShape, singleSelfShape);
      }

      return selfShape;
    }

    BlockPos treeRootPos = toRoot.from(pos);
    BlockState treeRoot = level.getBlockState(treeRootPos);
    if (!isGrowingSapling(treeRoot)) return selfShape;

    Vec3 rootOffset = treeShape(treeRoot).centerOffset();
    if (rootOffset.length() == 0) return selfShape;

    Vec3 shiftToRoot = Vec3.atLowerCornerOf(treeRootPos.subtract(pos));
    VoxelShape rootShape = getInteractShape(treeRoot, level, treeRootPos).move(shiftToRoot.x, 0, shiftToRoot.z);

    if (half(state) == DoubleBlockHalf.UPPER) return rootShape;
    else return Shapes.or(rootShape, selfShape);
  }

  public static void playBranchesBreakSound(Level level, BlockPos pos) {
    float pitch = 0.8F + level.getRandom().nextFloat() * 0.4F;
    level.playLocalSound(pos, SoundEvents.MANGROVE_ROOTS_HIT, SoundSource.BLOCKS, 1F, pitch, false);
  }

  public enum Stage implements StringRepresentable, Comparable<Stage> {
    HIDDEN(0),
    DECAYING(1),
    SPROUT(2),
    SAPLING(3),
    TALL_SAPLING(4),
    GIANT(5);

    private final int VALUE;

    Stage(int value) {
      this.VALUE = value;
    }

    public int value() {
      return VALUE;
    }

    public boolean LT(Stage other) {
      return value() < other.value();
    }

    public boolean GT(Stage other) {
      return value() > other.value();
    }

    public boolean LEQ(Stage other) {
      return value() <= other.value();
    }

    public boolean GEQ(Stage other) {
      return value() >= other.value();
    }

    @Override
    public String toString() {
      return String.valueOf(VALUE);
    }

    @NotNull
    @Override
    public String getSerializedName() {
      return toString();
    }
  }

  public static class Properties {
    public static final BooleanProperty GIANT;
    public static final BooleanProperty PRUNED;
    public static final EnumProperty<Stage> GROWTH_STAGE;
    public static final EnumProperty<SaplingShape.Layout> TREE_SHAPE;
    public static final EnumProperty<BlockPosOffset> TREE_ROOT;
    public static final EnumProperty<DoubleBlockHalf> HALF; // TODO: grow sapling taller than 2 blocks?

    static {
      GIANT = BooleanProperty.create("giant");
      PRUNED = BooleanProperty.create("pruned");
      GROWTH_STAGE = EnumProperty.create("growth_stage", Stage.class);
      TREE_SHAPE = EnumProperty.create("tree_shape", SaplingShape.Layout.class);
      TREE_ROOT = EnumProperty.create("tree_root", BlockPosOffset.class);
      HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    }
  }

  public enum VisualShape implements ShapeResolver {
    INTERACT,
    COLLISION;

    private ShapeResolver SHAPE_RESOLVER;

    @Override
    public VoxelShape parse(BlockState state, BlockPos pos) {
      return SHAPE_RESOLVER.parse(state, pos);
    }

    private void defineShapes(BiFunction<Stage, DoubleBlockHalf, VoxelShape> builder) {
      this.SHAPE_RESOLVER = (state, pos) -> {
        VoxelShape shape = builder.apply(growthStage(state), half(state));
        Vec3 randomOffset = state.getOffset((BlockPos) getPosForRandomOffset(state, pos));

        if (markedGiant(state)) {
          Vec3 centerOffset = treeShape(state).centerOffset();
          shape = shape.move(centerOffset.x, 0, centerOffset.z);
        }

        shape = shape.move(randomOffset.x, randomOffset.y, randomOffset.z);
        return shape;
      };
    }

    static {
      INTERACT.defineShapes((stage, half) -> switch (stage) {
        case DECAYING, SPROUT -> ShapeUtil.column(12, 8);
        case SAPLING -> ShapeUtil.column(12, 12);
        case TALL_SAPLING -> {
          if (half == DoubleBlockHalf.UPPER) yield ShapeUtil.block(-4);
          yield ShapeUtil.columnOffsetXZ(6, 16, -0.5, 0.5);
        }
        case GIANT -> {
          if (half == DoubleBlockHalf.LOWER) yield ShapeUtil.column(8, 16);
          else yield Shapes.or(ShapeUtil.column(8, 14), ShapeUtil.block(10));
        }

        default -> Shapes.empty();
      });

      COLLISION.defineShapes((stage, half) -> switch (stage) {
        case TALL_SAPLING -> {
          if (half == DoubleBlockHalf.UPPER) yield Shapes.empty();
          yield ShapeUtil.columnOffsetXZ(3, 16, -0.5, 0.5);
        }
        case GIANT -> ShapeUtil.column(4, half == DoubleBlockHalf.LOWER ? 16 : 10);

        default -> Shapes.empty();
      });
    }
  }

  @FunctionalInterface
  private interface ShapeResolver {
    VoxelShape parse(BlockState state, BlockPos pos);
  }

}
