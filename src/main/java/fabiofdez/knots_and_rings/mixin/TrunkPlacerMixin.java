//~ place_log

package fabiofdez.knots_and_rings.mixin;

import fabiofdez.knots_and_rings.block.LogBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TrunkPlacer.class)
public class TrunkPlacerMixin {

  @Unique
  private static final String PLACE_LOG = "placeLog(Lnet/minecraft/world/level/LevelSimulatedReader;Ljava/util/function/BiConsumer;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;Ljava/util/function/Function;)Z";

  @SuppressWarnings("unchecked")
  @ModifyArg(method = PLACE_LOG, at = @At(value = "INVOKE", target = "Ljava/util/function/BiConsumer;accept(Ljava/lang/Object;Ljava/lang/Object;)V"), index = 1)
  private <T> T knots_and_rings$markTrunk(T t) {
    if (!(t instanceof BlockState state)) return t;
    if (!(state.getBlock() instanceof LogBlock)) return (T) state;

    return (T) state.setValue(LogBlock.IS_TRUNK, true);
  }
}
