package fabiofdez.knots_and_rings.feature;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WorldGenContext {

  private static final Map<ResourceKey<Level>, Set<ChunkPos>> DECORATING = new ConcurrentHashMap<>();

  public static void startDecorating(Level level, ChunkPos pos) {
    DECORATING.computeIfAbsent(level.dimension(), (levelKey) -> ConcurrentHashMap.newKeySet()).add(pos);
  }

  public static void endDecorating(Level level, ChunkPos pos) {
    Set<ChunkPos> levelChunks = DECORATING.get(level.dimension());
    if (levelChunks == null) return;

    levelChunks.remove(pos);
    if (levelChunks.isEmpty()) DECORATING.remove(level.dimension());
  }

  public static boolean isDecorating(Level level, ChunkPos pos) {
    Set<ChunkPos> levelChunks = DECORATING.get(level.dimension());
    if (levelChunks == null) return false;

    return levelChunks.contains(pos);
  }
}
