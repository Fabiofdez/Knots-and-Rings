//~ get_drops

package fabiofdez.knots_and_rings.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import fabiofdez.knots_and_rings.feature.GrowingSapling;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Stage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviorMixin {

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

  @Inject(method = "fallOn", at = @At("HEAD"))
  protected void knots_and_rings$fallOn(Level level, BlockState ignored, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
    BlockPos abovePos = pos.above();
    BlockState aboveState = level.getBlockState(abovePos);
    if (!GrowingSapling.isGrowingSapling(aboveState)) return;

    Stage saplingStage = GrowingSapling.growthStage(aboveState);
    if (saplingStage.GT(Stage.SPROUT) || saplingStage == Stage.HIDDEN) return;
    if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof LivingEntity)) return;

    RandomSource random = level.getRandom();
    boolean chance = random.nextFloat() < fallDistance - 0.5;
    if (!chance) return;

    boolean mobGriefing = serverLevel.getGameRules().getRule(GameRules.RULE_MOBGRIEFING)/*? < 1.21.11 >> ';' */.get();
    boolean entityLargeEnough = entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F;
    if ((entity instanceof Player || mobGriefing) && entityLargeEnough) {
      GrowingSapling.stompOnSapling(aboveState, level, abovePos);
    }
  }

  @Inject(method = "destroy", at = @At("RETURN"))
  protected void knots_and_rings$destroy(LevelAccessor level, BlockPos pos, BlockState state, CallbackInfo ci) {
  }
}
