//~ get_drops

package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviorMixin {

  @Shadow
  public abstract void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState);

  @Inject(method = "getStateForPlacement", at = @At("RETURN"))
  protected void knots_and_rings$updateSaplingDirtState(BlockPlaceContext ctx, CallbackInfoReturnable<BlockState> cir) {
  }

  @ModifyReturnValue(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)Ljava/util/List;", at = @At("RETURN"))
  private static List<ItemStack> knots_and_rings$getDrops1(List<ItemStack> drops, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) ServerLevel level) {
    if (GrowingSapling.isGrowingSapling(state)) return GrowingSapling.getDrops(drops, state, level);
    return drops;
  }

  @ModifyReturnValue(method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", at = @At("RETURN"))
  private static List<ItemStack> knots_and_rings$getDrops2(List<ItemStack> drops, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) ServerLevel level) {
    if (GrowingSapling.isGrowingSapling(state)) return GrowingSapling.getDrops(drops, state, level);
    return drops;
  }

  @Inject(method = "destroy", at = @At("RETURN"))
  protected void knots_and_rings$destroy(LevelAccessor level, BlockPos pos, BlockState state, CallbackInfo ci) {
  }
}
