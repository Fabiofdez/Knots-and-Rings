package fabiofdez.knots_and_rings.feature;

import fabiofdez.knots_and_rings.block.state.BlockPosOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class SaplingPlacement {
  public static final SaplingPlacement CENTER;

  private final Vec3 offsetVec;

  private SaplingPlacement(BlockPosOffset centerOffset) {

    if (centerOffset == BlockPosOffset.SELF) {
      this.offsetVec = new Vec3(0, 0, 0);
      return;
    }

    BlockPos center = new BlockPos(0, 0, 0);
    BlockPos shifted = centerOffset.from(center);
    this.offsetVec = new Vec3(
        shifted.getX() - center.getX(),
        shifted.getY() - center.getY(),
        shifted.getZ() - center.getZ()
    ).scale(0.5);
  }

  public Vec3 vector() {
    return this.offsetVec;
  }

  public static SaplingPlacement to(BlockPosOffset offset) {
    return new SaplingPlacement(offset);
  }

  static {
    CENTER = new SaplingPlacement(BlockPosOffset.SELF);
  }
}
