package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MangrovePropaguleBlock.class)
public abstract class MangrovePropaguleMixin {

  @Shadow
  @Final
  public static BooleanProperty HANGING;

  @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/MangrovePropaguleBlock;registerDefaultState(Lnet/minecraft/world/level/block/state/BlockState;)V"))
  protected BlockState knots_and_rings$initSaplingGrowthStageProperty(BlockState state) {
    return GrowingSapling.registerDefaultState(state);
  }

  @Inject(method = "createBlockStateDefinition", at = @At("RETURN"))
  protected void knots_and_rings$addGrowthStageToBlockStateDef(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
    GrowingSapling.initBlockStateDef(builder);
  }

  @Definition(id = "randomSource", local = @Local(type = RandomSource.class, argsOnly = true))
  @Definition(id = "nextInt", method = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
  @Expression("randomSource.nextInt(7) == 0")
  @ModifyExpressionValue(method = "randomTick", at = @At("MIXINEXTRAS:EXPRESSION"))
  protected boolean knots_and_rings$conditionalRandomTick(boolean original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) RandomSource random) {
    if (!GrowingSapling.isGrowingSapling(state)) return original;
    return GrowingSapling.canRandomTick(state, random);
  }

  @ModifyReturnValue(method = "getShape", at = @At("RETURN"))
  protected VoxelShape knots_and_rings$getShape(VoxelShape original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos) {
    if (state.getValue(HANGING) || !GrowingSapling.isGrowingSapling(state)) return original;
    return GrowingSapling.getInteractShape(state, level, pos);
  }

  @ModifyReturnValue(method = "createNewHangingPropagule(I)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("RETURN"))
  private static BlockState knots_and_rings$createNewHangingPropagule(BlockState state) {
    if (!GrowingSapling.isGrowingSapling(state)) return state;
    return state.setValue(GrowingSapling.Properties.GROWTH_STAGE, GrowingSapling.Stage.HIDDEN);
  }
}
