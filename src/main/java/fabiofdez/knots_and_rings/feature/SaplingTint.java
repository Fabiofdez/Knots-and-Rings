package fabiofdez.knots_and_rings.feature;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;
import java.util.List;

//? if < 26.1 {
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.world.level.BlockAndTintGetter;
//? } else {
/*import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.resources.model.cuboid.BlockElementFace;
*///? }

public class SaplingTint {

  // TODO: Set up tint registration for modded saplings?
  // Idea: hold all blocks to be registered in a list,
  // add mixins for any mod that needs to add their own blocks
  // (!!) Make sure blocks are added before this function is called
  private static final List<Block> TO_BE_TINTED = List.of(
      Blocks.ACACIA_SAPLING,
      Blocks.BIRCH_SAPLING,
      Blocks.DARK_OAK_SAPLING,
      Blocks.JUNGLE_SAPLING,
      Blocks.MANGROVE_PROPAGULE,
      Blocks.OAK_SAPLING,
      Blocks.SPRUCE_SAPLING
  );

  public static void registerWith(ColorRegistryEvent event) {

    //? if < 26.1 {
    BlockColor provider = (state, tintGetter, pos, tintIdx) -> getSaplingTint(state).apply(tintGetter, pos);
    //? } else {
    /*List<BlockTintSource> provider = List.of(new BlockTintSource() {
      @Override
      public int color(BlockState state) {
        return getSaplingTint(state).resolve();
      }

      @Override
      public int colorInWorld(BlockState state, BlockAndTintGetter tintGetter, BlockPos pos) {
        return getSaplingTint(state).apply(tintGetter, pos);
      }
    });
    *///? }

    event.accept(provider, TO_BE_TINTED.toArray(Block[]::new));
  }

  private static TintResolver getSaplingTint(BlockState state) {
    boolean immatureSapling = GrowingSapling.growthStage(state).LT(GrowingSapling.Stage.TALL_SAPLING);
    boolean isSaplingTop = GrowingSapling.half(state) == DoubleBlockHalf.UPPER;
    SaplingType type = SaplingType.ofSapling(state.getBlock());

    if (immatureSapling || !isSaplingTop || type == SaplingType.NONE) {
      return (tintGetter, pos) -> BlockElementFace.NO_TINT;
    }

    int leavesTint = type.tint();
    if (leavesTint != FoliageColor.FOLIAGE_DEFAULT) {
      return (tintGetter, pos) -> leavesTint;
    }

    return (tintGetter, pos) -> {
      if (tintGetter == null || pos == null) return leavesTint;
      return BiomeColors.getAverageFoliageColor(tintGetter, pos);
    };
  }

  @FunctionalInterface
  public interface ColorRegistryEvent {
    //? < 26.1
    void accept(BlockColor provider, Block... blocks);
    //? >= 26.1
    //void accept(List<BlockTintSource> provider, Block... blocks);
  }

  @FunctionalInterface
  public interface TintResolver {
    int apply(@Nullable BlockAndTintGetter tintGetter, @Nullable BlockPos pos);

    //? >= 26.1 {
    /*default int resolve() {
      return apply(null, null);
    }
    *///? }
  }
}
