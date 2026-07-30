package fabiofdez.knots_and_rings.util;

import com.google.common.collect.ImmutableMap;
import fabiofdez.knots_and_rings.block.LogBlock;
import fabiofdez.knots_and_rings.block.state.LogSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class LivingWoodBlock {

  public static final String OLD_CONSTRUCTOR = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/RotatedPillarBlock;";

  private static final ImmutableMap<Direction.Axis, LinkedList<Direction>> SIDES_BY_AXIS = ImmutableMap.ofEntries(
      Map.entry(Direction.Axis.X, makeLoop(Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH)),
      Map.entry(Direction.Axis.Y, makeLoop(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)),
      Map.entry(Direction.Axis.Z, makeLoop(Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST))
  );

  public static boolean isLogBlock(ResourceLocation id) {
    return id.getPath().endsWith("_log");
  }

  public static boolean isWoodBlock(ResourceLocation id) {
    return id.getPath().endsWith("_wood");
  }

  public static boolean isStripped(ResourceLocation id) {
    return id.getPath().startsWith("stripped_");
  }

  public static boolean isNaturalWood(BlockState state) {
    if (!state.hasProperty(Properties.ALIVE)) return false;

    ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
    boolean isWood = isLogBlock(blockId) || isWoodBlock(blockId);
    return isWood && !isStripped(blockId);
  }

  public static boolean isNaturalLeaves(BlockState state) {
    if (!state.hasProperty(LeavesBlock.PERSISTENT)) return false;
    return state.is(BlockTags.LEAVES) && !state.getValue(LeavesBlock.PERSISTENT);
  }

  public static NeighborIterable neighborsOf(LevelReader level, BlockPos pos) {
    return new NeighborIterable(level, pos);
  }

  public static boolean isAliveNearby(BlockState state, ServerLevel level, BlockPos pos) {
    if (!isNaturalWood(state)) return false;

    return neighborsOf(level, pos).any((neighbor, neighborPos) -> {
      if (isNaturalLeaves(neighbor)) {
        return true;
      }

      if (isNaturalWood(neighbor)) {
        return isAlive(neighbor);
      }

      return false;
    });
  }

  public static boolean isTrunkNearby(BlockState state, ServerLevel level, BlockPos pos) {
    if (!isNaturalWood(state)) return false;

    return neighborsOf(level, pos).any((neighbor, neighborPos) -> {
      if (isNaturalLeaves(neighbor)) {
        return true;
      }

      if (isNaturalWood(neighbor)) {
        return isTrunk(neighbor);
      }

      return false;
    });
  }

  public static Direction.Axis getAxis(BlockState state) {
    return state.getValue(LogBlock.AXIS);
  }

  public static LogSide.Mapping getSides(BlockState state) {
    return state.getValue(Properties.SIDES);
  }

  public static boolean isAlive(BlockState state) {
    return state.getValue(Properties.ALIVE);
  }

  public static boolean isSingleton(BlockState state) {
    return state.getValue(Properties.SINGLETON);
  }

  public static boolean isTrunk(BlockState state) {
    return state.getValue(Properties.IS_TRUNK);
  }

  public static boolean changedSides(BlockState oldState, BlockState newState) {
    return !getSides(oldState).equals(getSides(newState));
  }

  public static boolean compatibleLogs(BlockState thisState, BlockState neighborState) {
    if (blockId(thisState) != blockId(neighborState)) return false;
    if (getAxis(thisState) != getAxis(neighborState)) return false;

    return isTrunk(thisState) == isTrunk(neighborState);
  }

  public static BlockState getLogShape(BlockState state, BlockGetter level, BlockPos pos) {
    Direction.Axis axis = getAxis(state);

    StringBuilder sides = new StringBuilder();
    LinkedList<Direction> loop = SIDES_BY_AXIS.get(axis);
    for (@SuppressWarnings("DataFlowIssue") ListIterator<Direction> it = loop.listIterator(); it.hasNext(); ) {
      Direction prevFace = it.hasPrevious() ? loop.get(it.previousIndex()) : loop.getLast();
      Direction currFace = it.next();
      Direction nextFace = it.hasNext() ? loop.get(it.nextIndex()) : loop.getFirst();

      BlockPos atFace = pos.relative(currFace);
      BlockState faceBlock = level.getBlockState(atFace);
      if (!compatibleLogs(state, faceBlock)) {
        sides.append("0");
        continue;
      }

      BlockState blockThruCCW = level.getBlockState(pos.relative(prevFace));
      BlockState blockThruCW = level.getBlockState(pos.relative(nextFace));

      Direction clockWise = currFace.getClockWise(axis);
      Direction counterClockWise = currFace.getCounterClockWise(axis);
      BlockState atFaceCW = level.getBlockState(atFace.relative(clockWise));
      BlockState atFaceCCW = level.getBlockState(atFace.relative(counterClockWise));
      boolean sameAtFaceCW = compatibleLogs(state, atFaceCW);
      boolean sameAtFaceCCW = compatibleLogs(state, atFaceCCW);
      boolean accessToCW = compatibleLogs(state, blockThruCW);
      boolean accessToCCW = compatibleLogs(state, blockThruCCW);

      if ((sameAtFaceCW && accessToCW) && (sameAtFaceCCW && accessToCCW)) sides.append("2");
      else if (sameAtFaceCCW && accessToCCW) sides.append("l");
      else if (sameAtFaceCW && accessToCW) sides.append("r");
      else sides.append("1");
    }

    LogSide.Mapping parsedSides = LogSide.Mapping.parse(sides.toString());
    return state.setValue(Properties.SIDES, parsedSides);
  }

  public static void updateLivingState(ServerLevel level, BlockPos pos, boolean nowAlive) {
    BlockState state = level.getBlockState(pos);
    if (!state.hasProperty(Properties.ALIVE)) return;

    boolean stateChanged = false;
    if (isSingleton(state)) {
      state = state.setValue(Properties.SINGLETON, false).setValue(Properties.IS_TRUNK, nowAlive);
      stateChanged = true;
    }
    if (isAlive(state) != nowAlive) {
      state = state.setValue(Properties.ALIVE, nowAlive);
      stateChanged = true;
    }

    if (stateChanged) level.setBlockAndUpdate(pos, state);
  }

  public static void updateIsTrunk(BlockState state, ServerLevel level, BlockPos pos, boolean isTrunk) {
    if (isTrunk(state) == isTrunk) return;
    level.setBlockAndUpdate(pos, state.setValue(Properties.IS_TRUNK, isTrunk));
  }

  public static void resetSingleton(ServerLevel level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    if (isSingleton(state)) return;

    state = state.setValue(Properties.SINGLETON, true).setValue(Properties.IS_TRUNK, false);
    level.setBlockAndUpdate(pos, state);
  }

  private static ResourceLocation blockId(BlockState state) {
    return BuiltInRegistries.BLOCK.getKey(state.getBlock());
  }

  private static LinkedList<Direction> makeLoop(Direction... directions) {
    return new LinkedList<>(List.of(directions));
  }

  public static class Properties {
    public static final BooleanProperty ALIVE = BooleanProperty.create("alive");
    public static final BooleanProperty SINGLETON = BooleanProperty.create("singleton");
    public static final BooleanProperty IS_TRUNK = BooleanProperty.create("is_trunk");
    public static final EnumProperty<LogSide.Mapping> SIDES = EnumProperty.create("sides", LogSide.Mapping.class);
  }
}
