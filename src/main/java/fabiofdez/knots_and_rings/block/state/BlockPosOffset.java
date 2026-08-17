package fabiofdez.knots_and_rings.block.state;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public enum BlockPosOffset implements StringRepresentable {

  SELF("self", UnaryOperator.identity()),
  N("north", (pos) -> pos.relative(Direction.NORTH)),
  NE("northeast", (pos) -> pos.relative(Direction.NORTH).relative(Direction.EAST)),
  E("east", (pos) -> pos.relative(Direction.EAST)),
  SE("southeast", (pos) -> pos.relative(Direction.SOUTH).relative(Direction.EAST)),
  S("south", (pos) -> pos.relative(Direction.SOUTH)),
  SW("southwest", (pos) -> pos.relative(Direction.SOUTH).relative(Direction.WEST)),
  W("west", (pos) -> pos.relative(Direction.WEST)),
  NW("northwest", (pos) -> pos.relative(Direction.NORTH).relative(Direction.WEST));

  private final UnaryOperator<BlockPos> TRANSFORM;
  private final String NAME;

  private static final Map<BlockPosOffset, BlockPosOffset> REVERSE;

  BlockPosOffset(String name, UnaryOperator<BlockPos> transform) {
    this.TRANSFORM = transform;
    this.NAME = name;
  }

  public BlockPos from(BlockPos pos) {
    return TRANSFORM.apply(pos);
  }

  public BlockPosOffset reverse() {
    return REVERSE.getOrDefault(this, SELF);
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
    REVERSE = Map.ofEntries(
        Map.entry(N, S),
        Map.entry(NE, SW),
        Map.entry(E, W),
        Map.entry(SE, NW),
        Map.entry(S, N),
        Map.entry(SW, NE),
        Map.entry(W, E),
        Map.entry(NW, SE)
    );
  }

  public static class MemberSet {
    public static final Set<BlockPosOffset> ALL_AROUND;
    public static final Set<BlockPosOffset> CARDINAL;

    public static List<BlockPosOffset> from(BlockPosOffset offset) {
      return ALL_AROUND.stream().filter(awayBy1From(offset)).toList();
    }

    private static Predicate<BlockPosOffset> awayBy1From(BlockPosOffset offset) {
      BlockPos center = new BlockPos(0, 0, 0);
      BlockPos atOffset = offset.from(center);
      return (o) -> chessboardDist(o.from(atOffset), center) == 1;
    }

    private static int chessboardDist(BlockPos a, BlockPos b) {
      int xDiff = Math.abs(a.getX() - b.getX());
      int yDiff = Math.abs(a.getY() - b.getY());
      int zDiff = Math.abs(a.getZ() - b.getZ());
      return Math.max(Math.max(xDiff, yDiff), zDiff);
    }

    static {
      ALL_AROUND = Set.of(N, NE, E, SE, S, SW, W, NW);
      CARDINAL = Set.of(N, E, S, W);
    }
  }
}
