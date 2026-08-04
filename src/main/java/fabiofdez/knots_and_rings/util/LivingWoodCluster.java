package fabiofdez.knots_and_rings.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class LivingWoodCluster {

  public static void attemptRevivePath(Level level, BlockPos pos) {
    revivePathOrDecay(level, pos, false);
  }

  public static void revivePathOrDecay(Level level, BlockPos pos, boolean forceDecay) {
    Set<BlockPos> attachedLogs = new HashSet<>();
    Iterable<BlockPos> foundPath = LivingWoodCluster.findPathToLeaves(level, pos, attachedLogs);

    if (foundPath != null) {
      LivingWoodCluster.revive(level, attachedLogs);
    } else {
      LivingWoodCluster.decay(level, attachedLogs);
      if (forceDecay) {
        attachedLogs.forEach((attached) -> LivingWoodBlock.resetSingleton(level, attached));
      }
    }
  }

  public static Iterable<BlockPos> findPathToLeaves(Level level, BlockPos start, Set<BlockPos> cluster) {
    Queue<BlockPos> queue = new ArrayDeque<>();
    Map<BlockPos, BlockPos> pathTrace = new HashMap<>();
    AtomicReference<List<BlockPos>> foundPath = new AtomicReference<>(null);
    AtomicReference<BlockPos> existingAttachment = new AtomicReference<>(null);

    queue.add(start);
    pathTrace.put(start, null);
    LogConnectivityCache.markExploring(start);
    BlockState startState = level.getBlockState(start);

    while (!queue.isEmpty()) {
      BlockPos current = queue.poll();
      if (!cluster.add(current)) continue; // already visited

      LivingWoodBlock.neighborsOf(level, current).forEach((neighbor, neighborPos) -> {
        if (cluster.contains(neighborPos)) return;

        if (LivingWoodBlock.isNaturalLeaves(neighbor)) {
          if (foundPath.get() == null) foundPath.set(buildTracedPath(pathTrace, current));
          return;
        }

        if (!LivingWoodBlock.compatibleWoods(startState, neighbor)) return;

        queue.add(neighborPos);
        Boolean cachedNeighborAlive = LogConnectivityCache.checkCached(neighborPos);
        if (cachedNeighborAlive != null) {
          if (cachedNeighborAlive && foundPath.get() == null) foundPath.set(buildTracedPath(pathTrace, current));
          existingAttachment.set(neighborPos.immutable());
          return;
        }

        if (!pathTrace.containsKey(neighborPos)) {
          pathTrace.put(neighborPos, current);
          LogConnectivityCache.markExploring(start);
        }
      });
    }

    Iterable<BlockPos> resolvedPath = foundPath.get();
    Iterable<BlockPos> explored = Optional.ofNullable(resolvedPath).orElse(cluster);
    LogConnectivityCache.forgetExplored(explored);

    if (existingAttachment.get() != null) {
      LogConnectivityCache.attachToCluster(existingAttachment.get(), explored);
    } else {
      LogConnectivityCache.cacheCluster(level.getChunk(start), cluster, resolvedPath != null);
    }

    return resolvedPath;
  }

  private static List<BlockPos> buildTracedPath(Map<BlockPos, BlockPos> pathTrace, BlockPos current) {
    List<BlockPos> path = new ArrayList<>();
    BlockPos backtrack = current;

    while (backtrack != null) {
      path.add(backtrack);
      backtrack = pathTrace.get(backtrack);
    }

    return path;
  }

  public static void revive(Level level, Iterable<BlockPos> cluster) {
    for (BlockPos pos : cluster) {
      LivingWoodBlock.updateLivingState(level, pos, true);
    }
  }

  public static void decay(Level level, Iterable<BlockPos> cluster) {
    for (BlockPos pos : cluster) {
      LivingWoodBlock.updateLivingState(level, pos, false);
    }
  }
}
