package fabiofdez.knots_and_rings.feature;

import com.google.common.collect.ImmutableSet;
import fabiofdez.knots_and_rings.block.state.BlockPosOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fabiofdez.knots_and_rings.block.state.BlockPosOffset.E;
import static fabiofdez.knots_and_rings.block.state.BlockPosOffset.N;
import static fabiofdez.knots_and_rings.block.state.BlockPosOffset.NW;
import static fabiofdez.knots_and_rings.block.state.BlockPosOffset.S;
import static fabiofdez.knots_and_rings.block.state.BlockPosOffset.SE;
import static fabiofdez.knots_and_rings.block.state.BlockPosOffset.SELF;
import static fabiofdez.knots_and_rings.block.state.BlockPosOffset.W;

public class SaplingShape {
  private final Layout LAYOUT;
  private final BlockPos SRC;
  private final Set<BlockPos> NEIGHBORS;

  public static final SaplingShape NO_SHAPE = Layout.NONE.inPlace(null);

  SaplingShape(@Nullable Layout shape, BlockPos srcPos) {
    this.LAYOUT = shape;
    this.SRC = srcPos != null ? srcPos.immutable() : null;

    Set<BlockPos> neighborSet = new HashSet<>();
    if (shape != null) neighborSet.addAll(shape.membersAround(this.SRC));
    if (this.SRC != null && neighborSet.size() > 1) neighborSet.remove(this.SRC);
    this.NEIGHBORS = ImmutableSet.copyOf(neighborSet);
  }

  public boolean isEmpty() {
    return SRC == null || NEIGHBORS.isEmpty();
  }

  public Layout layout() {
    return LAYOUT;
  }

  public BlockPos src() {
    return SRC;
  }

  public Set<BlockPos> neighbors() {
    return ImmutableSet.copyOf(NEIGHBORS);
  }

  public Set<BlockPos> allPositions() {
    if (isEmpty()) return Set.of();

    Set<BlockPos> all = new HashSet<>(NEIGHBORS);
    if (SRC != null) all.add(SRC);
    return ImmutableSet.copyOf(all);
  }

  public void forEachNeighbor(Consumer<BlockPos> action) {
    NEIGHBORS.forEach(action);
  }

  public void forEach(Consumer<BlockPos> action) {
    if (isEmpty()) return;

    if (!NEIGHBORS.contains(SRC)) action.accept(SRC);
    forEachNeighbor(action);
  }

  public void forEachOffset(BiConsumer<BlockPosOffset, BlockPos> action) {
    if (isEmpty()) return;

    if (!LAYOUT.offsets().contains(SELF)) action.accept(SELF, SRC);
    forEachNeighborOffset(action);
  }

  public void forEachNeighborOffset(BiConsumer<BlockPosOffset, BlockPos> action) {
    LAYOUT.forEachOffset((offset) -> action.accept(offset, offset.from(SRC)));
  }

  public enum Layout implements StringRepresentable {
    // TODO: Other shapes? method to resolve shape from sapling type?

    SQUARE_LG("3x3", BlockPosOffset.MemberSet.ALL_AROUND, BlockPosOffset.MemberSet.ALL_AROUND),
    SQUARE_SM("2x2", Set.of(E, SE, S), Set.of(W, NW, N), SE),
    SINGLETON("single", BlockPosOffset.MemberSet.SELF),
    NONE("none");

    private final Set<BlockPosOffset> OFFSETS;
    private final Set<BlockPosOffset> POSSIBLE_SRC_OFFSETS;

    private final SaplingPlacement SRC_PLACEMENT;
    private final String NAME;

    Layout(String name) {
      this(name, null, null);
    }

    Layout(String name, Set<BlockPosOffset> offsets) {
      this(name, offsets, offsets);
    }

    Layout(String name, Set<BlockPosOffset> offsets, Set<BlockPosOffset> possibleSrcOffsets) {
      this(name, offsets, possibleSrcOffsets, SaplingPlacement.CENTER);
    }

    Layout(String name, Set<BlockPosOffset> offsets, Set<BlockPosOffset> possibleSrcOffsets, BlockPosOffset srcOffset) {
      this(name, offsets, possibleSrcOffsets, SaplingPlacement.to(srcOffset));
    }

    Layout(String name, Set<BlockPosOffset> offsets, Set<BlockPosOffset> possibleSrcOffsets, SaplingPlacement srcPlacement) {
      this.NAME = name;
      this.SRC_PLACEMENT = srcPlacement;

      if (offsets == null || possibleSrcOffsets == null) {
        this.OFFSETS = ImmutableSet.of();
        this.POSSIBLE_SRC_OFFSETS = ImmutableSet.of();
        return;
      }

      offsets = new HashSet<>(offsets);
      offsets.add(SELF);

      possibleSrcOffsets = new HashSet<>(possibleSrcOffsets);
      possibleSrcOffsets.add(SELF);

      this.OFFSETS = ImmutableSet.copyOf(offsets);
      this.POSSIBLE_SRC_OFFSETS = ImmutableSet.copyOf(possibleSrcOffsets);
    }

    public Vec3 centerOffset() {
      return this.SRC_PLACEMENT.vector();
    }

    public Set<BlockPosOffset> offsets() {
      return OFFSETS;
    }

    public void forEachOffset(Consumer<BlockPosOffset> action) {
      OFFSETS.forEach(action);
    }

    public Set<BlockPos> membersAround(BlockPos pos) {
      if (pos == null) return ImmutableSet.of();
      return OFFSETS.stream().map((o) -> o.from(pos)).collect(Collectors.toSet());
    }

    public Set<BlockPos> possibleSourcesNear(BlockPos pos) {
      if (pos == null) return ImmutableSet.of();
      return POSSIBLE_SRC_OFFSETS.stream().map((o) -> o.from(pos)).collect(Collectors.toSet());
    }

    public SaplingShape inPlace(BlockPos pos) {
      return new SaplingShape(this, pos);
    }

    public SaplingShape nearbySource(BlockState state, BlockGetter level, BlockPos pos) {
      if (!GrowingSapling.isGrowingSapling(state)) return NO_SHAPE;

      if (GrowingSapling.half(state) == DoubleBlockHalf.UPPER) pos = pos.below();
      Predicate<BlockPos> sameSapling = (at) -> saplingsMatch(state, level, at);

      for (BlockPos neighbor : possibleSourcesNear(pos)) {
        if (!sameSapling.test(neighbor)) continue;
        if (!membersAround(neighbor).stream().allMatch(sameSapling)) continue;

        return inPlace(neighbor);
      }

      return NO_SHAPE;
    }

    private static boolean saplingsMatch(BlockState state, BlockGetter level, BlockPos otherPos) {
      BlockState other = level.getBlockState(otherPos);
      if (!GrowingSapling.partsOfSameSapling(state, other)) return false;
      if (GrowingSapling.treeShape(other) != SINGLETON) return false;

      GrowingSapling.Stage thisStage = GrowingSapling.growthStage(state);
      if (thisStage.lessThanOrEqualTo(GrowingSapling.Stage.SPROUT)) return true;

      return GrowingSapling.growthStage(other).lessThanOrEqualTo(thisStage);
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
  }
}
