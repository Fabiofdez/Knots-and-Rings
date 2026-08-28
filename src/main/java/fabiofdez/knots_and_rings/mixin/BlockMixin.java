//~ get_drops

package fabiofdez.knots_and_rings.mixin;

import fabiofdez.knots_and_rings.block.TreeSeedBlock;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Stage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? <= 1.21.1 {
/*import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
*///? }

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviorMixin {

  @Inject(method = "fallOn", at = @At("HEAD"))
  protected void knots_and_rings$fallOn(Level level, BlockState ignored, BlockPos pos, Entity entity, /*? if <= 1.21.1 { *//*float*//*? } else { */double/*? } */ fallDistance, CallbackInfo ci) {
    if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof LivingEntity)) return;

    BlockPos abovePos = pos.above();
    BlockState aboveState = level.getBlockState(abovePos);

    Runnable stompAction;
    if (GrowingSapling.isGrowingSapling(aboveState)) {
      Stage saplingStage = GrowingSapling.growthStage(aboveState);
      if (saplingStage.GT(Stage.SPROUT) || saplingStage == Stage.HIDDEN) return;

      stompAction = () -> GrowingSapling.stompOnSapling(aboveState, level, abovePos);
    } else if (aboveState.getBlock() instanceof TreeSeedBlock) {
      stompAction = () -> level.destroyBlock(abovePos, true);
    } else return;

    RandomSource random = level.getRandom();
    boolean chance = random.nextFloat() < fallDistance - 0.5;
    if (!chance) return;

    boolean mobGriefing = serverLevel
        //~ if >= 1.21.11 '.get();' -> ';'
        .getGameRules().getRule(GameRules.RULE_MOBGRIEFING).get();

    boolean entityLargeEnough = entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F;
    if ((entity instanceof Player || mobGriefing) && entityLargeEnough) stompAction.run();
  }

  @Inject(method = "destroy", at = @At("RETURN"))
  protected void knots_and_rings$destroy(LevelAccessor level, BlockPos pos, BlockState state, CallbackInfo ci) {
  }

  //? <= 1.21.1 {
  /*@ModifyReturnValue(method = "getCloneItemStack", at = @At("RETURN"))
  protected ItemStack knots_and_rings$pickBlock(ItemStack original, @Local(argsOnly = true) BlockState state) {
    return original;
  }
  *///? }
}
