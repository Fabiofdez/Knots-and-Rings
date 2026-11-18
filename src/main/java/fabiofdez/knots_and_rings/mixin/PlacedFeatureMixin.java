package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import fabiofdez.knots_and_rings.feature.SaplingType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlacedFeature.class)
public class PlacedFeatureMixin {

  @Unique
  private static final String PLACE_WITH_CONTEXT =
      //? if forge && < 1.21 {
      /*"m_226362_";
      *///? } else if < 26.1 {
      /*? if fabric { */"method_39646"/*? } else { *//*"lambda$placeWithContext$4"*//*? } */;
      //? } else {
      /*"lambda$placeWithContext$1";
      *///? }

  @Inject(method = PLACE_WITH_CONTEXT, at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/mutable/MutableBoolean;setTrue()V", remap = false), remap = false)
  private static void knots_and_rings$placeSapling(CallbackInfo ci, @Local(argsOnly = true) PlacementContext ctx, @Local(argsOnly = true) BlockPos pos) {
    WorldGenLevel level = ctx.getLevel();
    BlockState placed = level.getBlockState(pos);

    SaplingType type = SaplingType.ofSapling(placed.getBlock());
    if (type == SaplingType.NONE || !level.ensureCanWrite(pos)) return;

    placed = type.placedSapling(level, pos).setValue(GrowingSapling.Properties.PRUNED, true);
    level.setBlock(pos, placed, 3);
  }
}
