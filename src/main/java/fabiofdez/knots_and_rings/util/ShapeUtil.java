package fabiofdez.knots_and_rings.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class ShapeUtil {

  public static VoxelShape block(double offsetY) {
    return Shapes.block().move(0, offsetY / 16, 0);
  }

  public static VoxelShape column(double width, double height) {
    return columnOffsetXZ(width, height, 0, 0);
  }

  public static VoxelShape column(double width, double yBottom, double yTop) {
    double halfWidth = width / 2.0;
    return Block.box(8 - halfWidth, yBottom, 8 - halfWidth, 8 + halfWidth, yTop, 8 + halfWidth);
  }

  public static VoxelShape columnOffsetXZ(double width, double height, double offsetX, double offsetZ) {
    double halfWidth = width / 2.0;
    offsetX += 8.0;
    offsetZ += 8.0;

    return Block.box(offsetX - halfWidth, 0, offsetZ - halfWidth, offsetX + halfWidth, height, offsetZ + halfWidth);
  }

  public static VoxelShape[] boxes(int numBoxes, IntFunction<VoxelShape> shapeBuilder) {
    return IntStream.rangeClosed(0, numBoxes).mapToObj(shapeBuilder).toArray(VoxelShape[]::new);
  }
}
