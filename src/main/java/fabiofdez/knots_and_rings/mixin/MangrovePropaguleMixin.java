package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import fabiofdez.knots_and_rings.feature.SaplingType;
//? <= 1.21.1
//import fabiofdez.knots_and_rings.util.ShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static fabiofdez.knots_and_rings.feature.SaplingTypeID.*;

@Mixin(MangrovePropaguleBlock.class)
public abstract class MangrovePropaguleMixin extends SaplingMixin {

  //? > 1.21.1 {
  @Mutable
  @Shadow
  @Final
  private static int[] SHAPE_MIN_Y;
  //? }

  @Mutable
  @Shadow
  @Final
  private static VoxelShape[] SHAPE_PER_AGE;

  @Shadow
  private static boolean isHanging(BlockState blockState) {
    throw new UnsupportedOperationException("Implemented via mixin");
  }

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
    if (isHanging(state) || !GrowingSapling.isGrowingSapling(state)) return original;
    return GrowingSapling.getInteractShape(state, level, pos);
  }

  @ModifyReturnValue(method = "createNewHangingPropagule(I)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("RETURN"))
  private static BlockState knots_and_rings$createNewHangingPropagule(BlockState state) {
    if (!GrowingSapling.isGrowingSapling(state)) return state;
    return state.setValue(GrowingSapling.Properties.GROWTH_STAGE, GrowingSapling.Stage.HIDDEN);
  }

  @Override
  protected List<ItemStack> knots_and_rings$getDrops(List<ItemStack> drops, BlockState state, ServerLevel level) {
    if (!isHanging(state)) return super.knots_and_rings$getDrops(drops, state, level);

    drops.replaceAll((stack) -> {
      if (!stack.is(Items.MANGROVE_PROPAGULE)) return stack;

      SaplingType type = SaplingType.of(MANGROVE);
      ItemStack newStack = type.seed().asItem().getDefaultInstance();
      newStack.setCount(stack.getCount());
      return newStack;
    });

    return drops;
  }

  @Override
  protected ItemStack knots_and_rings$pickBlock(ItemStack original, BlockState state) {
    if (!isHanging(state)) return super.knots_and_rings$pickBlock(original, state);

    SaplingType type = SaplingType.ofSapling(state.getBlock());
    if (type == SaplingType.NONE) return super.knots_and_rings$pickBlock(original, state);

    return type.seed().asItem().getDefaultInstance();
  }

  @Inject(method = "<clinit>", at = @At("TAIL"))
  private static void knots_and_rings$modifyHangingPropaguleShapes(CallbackInfo ci) {
    //? if <= 1.21.1 {
    /*final int[] SHAPE_MIN_Y = new int[]{12, 9, 5, 3, 0};
    SHAPE_PER_AGE = ShapeUtil.boxes(4, (i) -> ShapeUtil.column(4, SHAPE_MIN_Y[i], 16));
    *///? } else {
    SHAPE_MIN_Y = new int[]{12, 9, 5, 3, 0};
    SHAPE_PER_AGE = Block.boxes(4, (i) -> Block.column(4, SHAPE_MIN_Y[i], 16));
    //? }
  }
}
