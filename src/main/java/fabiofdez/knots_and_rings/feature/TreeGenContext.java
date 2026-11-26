package fabiofdez.knots_and_rings.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class TreeGenContext {

  private static final Map<ResourceKey<Level>, Map<ChunkPos, ChunkTrees>> GENERATING = new ConcurrentHashMap<>();

  public static void startDecorating(Level level, ChunkPos chunk) {
    withGeneratingInChunk(level, chunk, null);
  }

  public static void placeSaplings(Level level, ChunkPos chunk) {
    ifGenerating(level, (chunkMap) -> chunkMap.get(chunk)).ifPresent(ChunkTrees::generateSaplings);
  }

  public static void endDecorating(Level level, ChunkPos chunk) {
    ifGenerating(level, (chunkMap) -> chunkMap.remove(chunk)).ifPresent(ChunkTrees::checkSaplings);
  }

  public static void markVisited(Level level, BlockPos pos) {
    withGeneratingInChunk(level, ChunkPos.containing(pos), (trees) -> trees.visit(pos));
  }

  public static void placeWhenDecorated(Level level, BlockPos pos, Runnable action) {
    withGeneratingInChunk(level, ChunkPos.containing(pos), (trees) -> trees.placeWhenDecorated(action));
  }

  public static void checkWhenLit(Level level, BlockPos pos, Runnable action) {
    withGeneratingInChunk(level, ChunkPos.containing(pos), (trees) -> trees.checkWhenLit(action));
  }

  public static boolean isNewTree(Level level, BlockPos pos) {
    ChunkMapConsumer<Boolean> checkAction = (chunkMap) -> {
      ChunkTrees trees = chunkMap.get(ChunkPos.containing(pos));
      return trees == null || !trees.hasVisited(pos);
    };

    return ifGenerating(level, checkAction).orElse(false);
  }

  private static void withGeneratingInChunk(Level level, ChunkPos chunk, Consumer<ChunkTrees> action) {
    ChunkRemapFunction remap = (levelKey, chunkMap) -> {
      if (chunkMap == null) chunkMap = new ConcurrentHashMap<>();

      ChunkTrees trees = chunkMap.computeIfAbsent(chunk, ChunkTrees::create);
      if (action != null) action.accept(trees);

      return chunkMap;
    };

    GENERATING.compute(level.dimension(), remap);
  }

  private static <T> Optional<T> ifGenerating(Level level, ChunkMapConsumer<T> action) {
    AtomicReference<T> result = new AtomicReference<>();
    ChunkRemapFunction remap = (levelKey, chunkMap) -> {
      result.set(action.apply(chunkMap));
      return chunkMap.isEmpty() ? null : chunkMap;
    };

    GENERATING.computeIfPresent(level.dimension(), remap);
    return Optional.ofNullable(result.get());
  }

  private interface ChunkRemapFunction extends BiFunction<ResourceKey<Level>, Map<ChunkPos, ChunkTrees>, Map<ChunkPos, ChunkTrees>> {
  }

  private interface ChunkMapConsumer<T> extends Function<Map<ChunkPos, ChunkTrees>, T> {
  }

  private record ChunkTrees(Set<BlockPos> visitedTrees, List<Runnable> saplingQueue, List<Runnable> lightCheckQueue) {

    static ChunkTrees create(ChunkPos chunk) {
      return new ChunkTrees(ConcurrentHashMap.newKeySet(), new ArrayList<>(), new ArrayList<>());
    }

    boolean hasVisited(BlockPos pos) {
      return visitedTrees.contains(pos);
    }

    void visit(BlockPos pos) {
      visitedTrees.add(pos);
    }

    void placeWhenDecorated(Runnable action) {
      saplingQueue.add(action);
    }

    void checkWhenLit(Runnable action) {
      lightCheckQueue.add(action);
    }

    void generateSaplings() {
      saplingQueue.forEach(Runnable::run);
    }

    void checkSaplings() {
      lightCheckQueue.forEach(Runnable::run);
    }
  }
}
