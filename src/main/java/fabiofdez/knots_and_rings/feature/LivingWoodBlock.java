package fabiofdez.knots_and_rings.feature;

import com.google.common.collect.ImmutableMap;
import fabiofdez.knots_and_rings.block.LogBlock;
import fabiofdez.knots_and_rings.block.state.LogSide;
import fabiofdez.knots_and_rings.util.NeighborIterable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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

  //? <= 1.21.1
  //public static final String OLD_CONSTRUCTOR = "(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/RotatedPillarBlock;";

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
    if (!(state.getBlock() instanceof LogBlock)) return false;
    return isNaturalWood(blockId(state));
  }

  public static boolean isNaturalWood(ResourceLocation id) {
    boolean isWood = isLogBlock(id) || isWoodBlock(id);
    return isWood && !isStripped(id);
  }

  public static boolean isNaturalLeaves(BlockState state) {
    if (!state.hasProperty(LeavesBlock.PERSISTENT)) return false;
    return state.is(BlockTags.LEAVES) && !state.getValue(LeavesBlock.PERSISTENT);
  }

  public static NeighborIterable neighborsOf(LevelReader level, BlockPos pos) {
    return new NeighborIterable(level, pos);
  }

  public static boolean isAliveNearby(BlockState state, LevelReader level, BlockPos pos) {
    if (!isNaturalWood(state)) return false;

    return neighborsOf(level, pos).any((neighbor, neighborPos) -> {
      if (isNaturalLeaves(neighbor)) {
        return true;
      }

      if (isNaturalWood(neighbor)) {
        return isAlive(neighbor) && !isSingleton(neighbor);
      }

      return false;
    });
  }

  public static boolean isTrunkNearby(BlockState state, LevelReader level, BlockPos pos) {
    if (!isNaturalWood(state)) return false;

    return neighborsOf(level, pos).any((neighbor, neighborPos) -> {
      if (isNaturalLeaves(neighbor)) {
        return true;
      }

      if (isNaturalWood(neighbor)) {
        return isTrunk(neighbor) && !isSingleton(neighbor);
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

  public static boolean changedShape(BlockState oldState, BlockState newState) {
    return getSides(oldState) != getSides(newState);
  }

  public static boolean compatibleLogs(BlockState thisState, BlockState neighborState) {
    ResourceLocation thisId = blockId(thisState);
    ResourceLocation neighborId = blockId(neighborState);
    if (!compatibleWoods(thisId, neighborId)) return false;

    return isLogBlock(thisId) && isLogBlock(neighborId);
  }

  public static boolean compatibleWoods(BlockState thisState, BlockState neighborState) {
    ResourceLocation thisId = blockId(thisState);
    ResourceLocation neighborId = blockId(neighborState);
    return compatibleWoods(thisId, neighborId);
  }

  public static boolean compatibleWoods(ResourceLocation thisId, ResourceLocation neighborId) {
    if (!isNaturalWood(thisId) || !isNaturalWood(neighborId)) return false;

    String thisWood = thisId.getPath().replaceAll("_log|_wood", "");
    String neighborWood = neighborId.getPath().replaceAll("_log|_wood", "");

    return thisWood.equals(neighborWood);
  }

  public static boolean identicalLogs(BlockState thisState, BlockState neighborState) {
    if (!compatibleLogs(thisState, neighborState)) return false;
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
      if (!identicalLogs(state, faceBlock)) {
        sides.append("0");
        continue;
      }

      BlockState blockThruCCW = level.getBlockState(pos.relative(prevFace));
      BlockState blockThruCW = level.getBlockState(pos.relative(nextFace));

      Direction clockWise = currFace.getClockWise(axis);
      Direction counterClockWise = currFace.getCounterClockWise(axis);
      BlockState atFaceCW = level.getBlockState(atFace.relative(clockWise));
      BlockState atFaceCCW = level.getBlockState(atFace.relative(counterClockWise));
      boolean sameAtFaceCW = identicalLogs(state, atFaceCW);
      boolean sameAtFaceCCW = identicalLogs(state, atFaceCCW);
      boolean accessToCW = identicalLogs(state, blockThruCW);
      boolean accessToCCW = identicalLogs(state, blockThruCCW);

      if ((sameAtFaceCW && accessToCW) && (sameAtFaceCCW && accessToCCW)) sides.append("2");
      else if (sameAtFaceCCW && accessToCCW) sides.append("l");
      else if (sameAtFaceCW && accessToCW) sides.append("r");
      else sides.append("1");
    }

    LogSide.Mapping parsedSides = LogSide.Mapping.parse(sides.toString());
    return state.setValue(Properties.SIDES, parsedSides);
  }

  public static BlockState checkLogsNearby(BlockState state, LevelReader level, BlockPos pos) {
    if (!(state.getBlock() instanceof LogBlock)) return state;

    boolean isAlive = LivingWoodBlock.isAliveNearby(state, level, pos);
    boolean isTrunk = LivingWoodBlock.isTrunkNearby(state, level, pos);

    return state
        .setValue(Properties.ALIVE, isAlive)
        .setValue(Properties.IS_TRUNK, isTrunk)
        .setValue(Properties.SINGLETON, false);
  }

  public static void updateLivingState(Level level, BlockPos pos, boolean nowAlive) {
    BlockState state = level.getBlockState(pos);
    if (!(state.getBlock() instanceof LogBlock)) return;

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

  public static void updateIsTrunk(BlockState state, Level level, BlockPos pos, boolean isTrunk) {
    if (!(state.getBlock() instanceof LogBlock)) return;
    if (isTrunk(state) == isTrunk) return;

    level.setBlockAndUpdate(pos, state.setValue(Properties.IS_TRUNK, isTrunk));
  }

  public static void resetSingleton(Level level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    if (!(state.getBlock() instanceof LogBlock)) return;
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
