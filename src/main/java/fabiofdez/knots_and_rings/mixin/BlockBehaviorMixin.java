//~ has_interaction_result

package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviorMixin {

  @Unique
      //? < 1.21
  //private static final String USE_ITEM_ON_METHOD = "use";
      //? >= 1.21
  private static final String USE_ITEM_ON_METHOD = "useItemOn";

  @Inject(method = "randomTick", at = @At("HEAD"))
  protected void knots_and_rings$randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
  }

  @ModifyReturnValue(method = USE_ITEM_ON_METHOD, at = @At("RETURN"))
  protected InteractionResult knots_and_rings$useItemOn(InteractionResult result, @Local(argsOnly = true) /*? if >= 1.21 >> 'BlockState' */ItemStack stack, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) Player player, @Local(argsOnly = true) InteractionHand hand) {
    return result;
  }

  @ModifyReturnValue(method = "getInteractionShape", at = @At("RETURN"))
  protected VoxelShape knots_and_rings$getInteractionShape(VoxelShape original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos) {
    return original;
  }

  @ModifyReturnValue(method = "getCollisionShape", at = @At("RETURN"))
  protected VoxelShape knots_and_rings$getCollisionShape(VoxelShape original, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockGetter level, @Local(argsOnly = true) BlockPos pos) {
    return original;
  }

  @ModifyArg(method = "getSeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;getSeed(Lnet/minecraft/core/Vec3i;)J"))
  protected Vec3i knots_and_rings$setPosForSeed(Vec3i pos, @Local(argsOnly = true) BlockState state) {
    return pos;
  }
}
