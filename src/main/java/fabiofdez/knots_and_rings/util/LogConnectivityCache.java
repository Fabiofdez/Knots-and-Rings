package fabiofdez.knots_and_rings.util;

import fabiofdez.knots_and_rings.KnotsAndRings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LogConnectivityCache {
  private static int clusterCounter = 1;

  private static final Set<BlockPos> currentlyExploring = ConcurrentHashMap.newKeySet();
  private static final Map<ChunkPos, Set<Integer>> clustersPerChunk = new ConcurrentHashMap<>();
  private static final Map<BlockPos, Integer> clusterByPos = new ConcurrentHashMap<>();
  private static final Map<Integer, Boolean> clusterAlive = new ConcurrentHashMap<>();
  private static final Map<Integer, Set<BlockPos>> clusterById = new ConcurrentHashMap<>();

  public static boolean exploring(BlockPos pos) {
    return currentlyExploring.contains(pos);
  }

  public static void markExploring(BlockPos pos) {
    currentlyExploring.add(pos);
  }

  public static void forgetExplored(BlockPos pos) {
    currentlyExploring.remove(pos);
  }

  public static void forgetExplored(Iterable<BlockPos> cluster) {
    for (BlockPos pos : cluster) forgetExplored(pos);
  }

  public static Boolean checkCached(BlockPos pos) {
    Integer clusterId = clusterByPos.get(pos);
    if (clusterId != null) {
      return clusterAlive.get(clusterId);
    }

    return null;
  }

  public static void cacheCluster(ChunkAccess chunk, Set<BlockPos> cluster, boolean alive) {
    int clusterId = clusterCounter++;
    clusterAlive.put(clusterId, alive);
    clustersPerChunk.computeIfAbsent(chunk.getPos(), (k) -> ConcurrentHashMap.newKeySet()).add(clusterId);

    for (BlockPos pos : cluster) {
      clusterByPos.put(pos.immutable(), clusterId);
    }

    Set<BlockPos> toStore = ConcurrentHashMap.newKeySet(cluster.size());
    toStore.addAll(cluster);

    clusterById.put(clusterId, toStore);
  }

  public static void attachToCluster(BlockPos origin, Iterable<BlockPos> path) {
    Integer clusterId = clusterByPos.get(origin);
    if (clusterId == null) return;

    Set<BlockPos> attachedBlocks = clusterById.get(clusterId);
    if (attachedBlocks == null) return;

    for (BlockPos newPos : path) {
      clusterByPos.put(newPos.immutable(), clusterId);
      attachedBlocks.add(newPos);
    }
  }

  public static void invalidateAttachedTo(ChunkAccess chunk, BlockPos origin) {
    Integer clusterId = clusterByPos.remove(origin);
    if (clusterId == null) return;

    Set<Integer> clustersAtChunk = clustersPerChunk.get(chunk.getPos());
    if (clustersAtChunk != null) {
      clustersAtChunk.remove(clusterId);
    }

    invalidateClusterById(clusterId);
  }

  public static void invalidateInChunk(ChunkAccess chunk) {
    KnotsAndRings.LOGGER.debug("Clearing log clusters in chunk {}", chunk.getPos());

    Set<Integer> clustersAtChunk = clustersPerChunk.remove(chunk.getPos());
    if (clustersAtChunk == null) return;

    for (Integer clusterId : clustersAtChunk) {
      invalidateClusterById(clusterId);
    }
  }

  private static void invalidateClusterById(Integer clusterId) {
    if (clusterId == null) return;

    clusterAlive.remove(clusterId);
    Set<BlockPos> attachedBlocks = clusterById.remove(clusterId);
    if (attachedBlocks == null) return;

    for (BlockPos pos : attachedBlocks) {
      clusterByPos.remove(pos);
    }
  }
}
