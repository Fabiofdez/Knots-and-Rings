package fabiofdez.knots_and_rings.mixin.regions_unexplored;

//? <= 1.21.1 {

/*import fabiofdez.knots_and_rings.compat.regions_unexplored.PineLogBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.regions_unexplored.registry.RUBlocks;
import net.regions_unexplored.world.level.feature.configuration.RUTreeConfiguration;
import net.regions_unexplored.world.level.feature.tree.StrippedPineTreeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(StrippedPineTreeFeature.class)
public class PineTreeFeatureMixin {

  @Inject(method = "placeLog", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/stateproviders/BlockStateProvider;getState(Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 0))
  private void knots_and_rings$placePineLog(LevelAccessor level, BlockPos pos, RandomSource randomSource, RUTreeConfiguration treeConfiguration, Direction.Axis axis, boolean isStripped, boolean isTransition, CallbackInfoReturnable<Boolean> cir) {
    BlockState state = treeConfiguration.trunkProvider().getState(randomSource, pos);

    if (state.getBlock() instanceof PineLogBlock) {
      if (isStripped) {
        Block STRIPPED_PINE = RUBlocks
            //? < 1.21
            //.STRIPPED_PINE_LOG/^? forge >> ';' ^//^.get()^/;
            //? >= 1.21
            .PINE_WOOD_SET.getStrippedLog();

        state = STRIPPED_PINE.defaultBlockState();
      } else {
        state = state.setValue(PineLogBlock.TRANSITION_BLOCK, isTransition);
      }
    }

    level.setBlock(pos, state.setValue(RotatedPillarBlock.AXIS, axis), 2);
  }

  @Redirect(method = "placeLog", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 5))
  private boolean knots_and_rings$ignorePineLogPlacement(LevelAccessor instance, BlockPos pos, BlockState state, int i) {
    return false;
  }
}
*///? }
