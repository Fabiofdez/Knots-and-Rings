package fabiofdez.knots_and_rings.compat;

import fabiofdez.knots_and_rings.feature.GrowingSapling;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Properties;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Stage;
import fabiofdez.knots_and_rings.feature.SaplingShape;
import fabiofdez.knots_and_rings.feature.SaplingType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class Particles {
  public static final Function<BlockState, ParticleOptions> BLOCK;

  static {
    BLOCK = (state) -> new BlockParticleOption(ParticleTypes.BLOCK, state);
  }

  public static void spawn(Level level, BlockPos pos, BlockHitResult hitResult, ParticleOptions particleOpts) {
    spawn(level, pos, hitResult.getDirection(), particleOpts);
  }

  public static void spawn(Level level, BlockPos pos, Direction direction, ParticleOptions particleOpts) {
    ParticleUtils.spawnParticlesOnBlockFace(
        level,
        pos,
        particleOpts,
        UniformInt.of(10, 15),
        direction,
        () -> Vec3.ZERO,
        0.5
    );
  }

  public static BlockState getStateForSapling(BlockState state, ClientLevel level, BlockPos pos) {
    if (!GrowingSapling.isGrowingSapling(state)) return state;

    SaplingType type = SaplingType.resolve(state.getBlock());
    if (type == SaplingType.NONE) return Blocks.AIR.defaultBlockState();

    DoubleBlockHalf half = GrowingSapling.half(state);
    Stage stage = GrowingSapling.growthStage(state);
    return switch (stage) {
      case HIDDEN -> Blocks.AIR.defaultBlockState();
      case DECAYING, SPROUT, SAPLING -> {
        BlockState defaultSapling = type.sapling().defaultBlockState();
        yield GrowingSapling.treeShape(state) == SaplingShape.Layout.SINGLETON
            ? defaultSapling.setValue(Properties.GROWTH_STAGE, stage)
            : defaultSapling.setValue(Properties.GROWTH_STAGE, Stage.HIDDEN);
      }
      case TALL_SAPLING, GIANT -> {
        if (half == DoubleBlockHalf.UPPER) yield type.leaves().defaultBlockState();

        GrowingSapling.playBranchesBreakSound(level, pos);
        yield GrowingSapling.convertToStem(state, level, pos);
      }
    };
  }
}
