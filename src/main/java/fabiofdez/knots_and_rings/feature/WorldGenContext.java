package fabiofdez.knots_and_rings.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

public class WorldGenContext {

  private static final Map<ResourceKey<Level>, Map<ChunkPos, Set<BlockPos>>> GENERATING = new ConcurrentHashMap<>();

  public static void startDecorating(Level level, ChunkPos chunk) {
    withGeneratingInChunk(level, chunk, Function.identity());
  }

  public static void endDecorating(Level level, ChunkPos chunk) {
    ifGenerating(level, (chunkMap) -> chunkMap.remove(chunk));
  }

  public static void markVisited(Level level, BlockPos pos) {
    withGeneratingInChunk(level, ChunkPos.containing(pos), (trees) -> trees.add(pos));
  }

  public static boolean isNewTree(Level level, BlockPos pos) {
    ChunkMapConsumer<Boolean> checkAction = (chunkMap) -> {
      if (chunkMap == null) return false;

      Set<BlockPos> levelTrees = chunkMap.get(ChunkPos.containing(pos));
      return levelTrees == null || !levelTrees.contains(pos);
    };

    return ifGenerating(level, checkAction);
  }

  private static <T> void withGeneratingInChunk(Level level, ChunkPos chunk, Function<Set<BlockPos>, T> action) {
    ChunkRemapFunction remap = (levelKey, chunkMap) -> {
      if (chunkMap == null) chunkMap = new ConcurrentHashMap<>();

      Set<BlockPos> chunkTrees = chunkMap.computeIfAbsent(chunk, (c) -> ConcurrentHashMap.newKeySet());
      action.apply(chunkTrees);

      return chunkMap;
    };

    GENERATING.compute(level.dimension(), remap);
  }

  private static <T> T ifGenerating(Level level, ChunkMapConsumer<T> action) {
    AtomicReference<T> result = new AtomicReference<>();
    ChunkRemapFunction remap = (levelKey, chunkMap) -> {
      result.set(action.apply(chunkMap));
      return chunkMap.isEmpty() ? null : chunkMap;
    };

    GENERATING.computeIfPresent(level.dimension(), remap);
    return result.get();
  }

  private interface ChunkRemapFunction extends BiFunction<ResourceKey<Level>, Map<ChunkPos, Set<BlockPos>>, Map<ChunkPos, Set<BlockPos>>> {
  }

  private interface ChunkMapConsumer<T> extends Function<Map<ChunkPos, Set<BlockPos>>, T> {
  }
}
