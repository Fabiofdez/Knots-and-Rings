package fabiofdez.knots_and_rings.feature;

import fabiofdez.knots_and_rings.KnotsAndRings;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LogConnectivityCache {
  private static int clusterCounter = 1;

  private static final Map<ResourceKey<Level>, Set<BlockPos>> EXPLORING = new ConcurrentHashMap<>();
  private static final Map<ResourceKey<Level>, Map<ChunkPos, Set<Integer>>> CLUSTERS_PER_CHUNK = new ConcurrentHashMap<>();
  private static final Map<ResourceKey<Level>, Map<BlockPos, Integer>> POS_TO_CLUSTER_ID = new ConcurrentHashMap<>();
  private static final Map<Integer, Boolean> CLUSTER_ALIVE = new ConcurrentHashMap<>();
  private static final Map<Integer, Set<BlockPos>> CLUSTERS = new ConcurrentHashMap<>();

  public static boolean exploring(Level level, BlockPos pos) {
    return EXPLORING.getOrDefault(level.dimension(), ConcurrentHashMap.newKeySet()).contains(pos);
  }

  public static void markExploring(Level level, BlockPos pos) {
    EXPLORING.computeIfAbsent(level.dimension(), (levelKey) -> ConcurrentHashMap.newKeySet()).add(pos);
  }

  public static void forgetExplored(Level level, BlockPos pos) {
    Set<BlockPos> exploringInLevel = EXPLORING.get(level.dimension());
    if (exploringInLevel == null) return;

    exploringInLevel.remove(pos);
    if (exploringInLevel.isEmpty()) EXPLORING.remove(level.dimension());
  }

  public static void forgetExplored(Level level, Iterable<BlockPos> cluster) {
    for (BlockPos pos : cluster) forgetExplored(level, pos);
  }

  public static Boolean checkCached(Level level, BlockPos pos) {
    Map<BlockPos, Integer> byPos = POS_TO_CLUSTER_ID.get(level.dimension());
    if (byPos == null) return null;

    Integer clusterId = byPos.get(pos);
    if (clusterId == null) return null;

    return CLUSTER_ALIVE.get(clusterId);
  }

  public static void cacheCluster(Level level, BlockPos start, Set<BlockPos> cluster, boolean alive) {
    int clusterId = clusterCounter++;
    CLUSTER_ALIVE.put(clusterId, alive);

    ChunkPos pos = level.getChunk(start).getPos();
    CLUSTERS_PER_CHUNK
        .computeIfAbsent(level.dimension(), (k) -> new ConcurrentHashMap<>())
        .computeIfAbsent(pos, (k) -> ConcurrentHashMap.newKeySet())
        .add(clusterId);

    Map<BlockPos, Integer> byPos = newBlockPosMapping(level);
    cluster.forEach((p) -> byPos.put(p, clusterId));

    Set<BlockPos> toStore = ConcurrentHashMap.newKeySet(cluster.size());
    toStore.addAll(cluster);

    CLUSTERS.put(clusterId, toStore);
  }

  public static void attachToCluster(Level level, BlockPos origin, Iterable<BlockPos> path) {
    Map<BlockPos, Integer> byPos = newBlockPosMapping(level);
    Integer clusterId = byPos.get(origin);
    if (clusterId == null) return;

    Set<BlockPos> attachedBlocks = CLUSTERS.get(clusterId);
    if (attachedBlocks == null) return;

    path.forEach((newPos) -> {
      byPos.put(newPos.immutable(), clusterId);
      attachedBlocks.add(newPos);
    });
  }

  public static void invalidateAttachedTo(Level level, BlockPos origin) {
    Map<BlockPos, Integer> byPos = POS_TO_CLUSTER_ID.get(level.dimension());
    if (byPos == null) return;

    Integer clusterId = byPos.remove(origin);
    if (clusterId == null) return;

    Map<ChunkPos, Set<Integer>> perChunk = CLUSTERS_PER_CHUNK.get(level.dimension());
    if (perChunk == null) return;

    ChunkPos pos = level.getChunk(origin).getPos();
    Set<Integer> clustersAtChunk = perChunk.get(pos);
    if (clustersAtChunk != null) clustersAtChunk.remove(clusterId);

    invalidateClusterById(level, clusterId);
  }

  public static void invalidateInChunk(Level level, ChunkPos pos) {
    KnotsAndRings.LOGGER.debug("Clearing log clusters in chunk {}", pos);

    Map<ChunkPos, Set<Integer>> perChunk = CLUSTERS_PER_CHUNK.get(level.dimension());
    if (perChunk == null) return;

    Set<Integer> clustersAtChunk = perChunk.remove(pos);
    if (clustersAtChunk == null) return;

    clustersAtChunk.forEach(clusterId -> invalidateClusterById(level, clusterId));
  }

  private static Map<BlockPos, Integer> newBlockPosMapping(Level level) {
    return POS_TO_CLUSTER_ID.computeIfAbsent(level.dimension(), (k) -> new ConcurrentHashMap<>());
  }

  private static void invalidateClusterById(Level level, Integer clusterId) {
    if (clusterId == null) return;

    CLUSTER_ALIVE.remove(clusterId);
    Set<BlockPos> attachedBlocks = CLUSTERS.remove(clusterId);
    if (attachedBlocks == null) return;

    Map<BlockPos, Integer> byPos = POS_TO_CLUSTER_ID.get(level.dimension());
    if (byPos == null) return;

    attachedBlocks.forEach(byPos::remove);
  }
}
