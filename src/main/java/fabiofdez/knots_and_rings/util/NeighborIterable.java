package fabiofdez.knots_and_rings.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class NeighborIterable {
  private static final List<Vec3i> NEIGHBORS;

  private final LevelReader level;
  private final BlockPos pos;

  public NeighborIterable(LevelReader level, BlockPos pos) {
    this.level = level;
    this.pos = pos;
  }

  public boolean any(NeighborPredicate predicate) {
    return NEIGHBORS.stream().anyMatch((neighborOffset) -> {
      BlockPos neighborPos = this.pos.offset(neighborOffset);
      BlockState neighbor = this.level.getBlockState(neighborPos);

      return predicate.eval(neighbor, neighborPos);
    });
  }

  public void forEach(NeighborRunnable runnable) {
    NEIGHBORS.forEach((neighborOffset) -> {
      BlockPos neighborPos = this.pos.offset(neighborOffset);
      BlockState neighbor = this.level.getBlockState(neighborPos);

      runnable.apply(neighbor, neighborPos);
    });
  }

  private static List<Vec3i> neighborPositions() {
    List<Vec3i> positions = new ArrayList<>();

    // includes all face/edge/corner neighbors
    for (int dy = 1; dy >= -1; dy--) {
      for (int dx = -1; dx <= 1; dx++) {
        for (int dz = -1; dz <= 1; dz++) {
          if (dx == 0 && dy == 0 && dz == 0) continue;

          positions.add(new Vec3i(dx, dy, dz));
        }
      }
    }

    return positions;
  }

  static {
    NEIGHBORS = ImmutableList.copyOf(neighborPositions());
  }

  @FunctionalInterface
  public interface NeighborRunnable {
    void apply(BlockState state, BlockPos pos);
  }

  @FunctionalInterface
  public interface NeighborPredicate {
    boolean eval(BlockState state, BlockPos pos);
  }
}
