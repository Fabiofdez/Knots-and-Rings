package fabiofdez.knots_and_rings.mixin.regions_unexplored;

//? 1.21.1 {

/*import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.block.LogBlock;
import fabiofdez.knots_and_rings.compat.regions_unexplored.AspenLogBlock;
import fabiofdez.knots_and_rings.compat.regions_unexplored.BlockUtilsExtender;
import fabiofdez.knots_and_rings.compat.regions_unexplored.PineLogBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.regions_unexplored.block.BlockFactory;
import net.regions_unexplored.block.set.WoodSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(WoodSet.class)
public class WoodSetMixin {

  @Unique
  private static final Map<String, BlockFactory<Block>> TYPES_MAP = ImmutableMap.ofEntries(
      Map.entry(
          "pine",
          PineLogBlock::new
      ), Map.entry("silver_birch", AspenLogBlock::new)
  );

  @Inject(method = "addLogs", at = @At(value = "HEAD"))
  private static void knots_and_rings$customLogRU(CallbackInfo ci, @Local(argsOnly = true, ordinal = 0) String name, @Local(argsOnly = true) LocalRef<BlockFactory<Block>> original) {
    original.set(TYPES_MAP.getOrDefault(name, LogBlock::new));
  }

  @Redirect(method = "lambda$addLogs$2", at = @At(value = "INVOKE", target = "Lnet/regions_unexplored/block/RUBlockUtils;wood(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;Lnet/minecraft/world/level/material/MapColor;Lnet/minecraft/world/level/block/SoundType;Z)Lnet/minecraft/world/level/block/RotatedPillarBlock;"))
  private static RotatedPillarBlock knots_and_rings$customWoodRU(BlockBehaviour.Properties properties, MapColor colour, SoundType sound, boolean fireproof) {
    return BlockUtilsExtender.customWood(properties, colour, sound, fireproof);
  }

  // TODO: override "alpha_log", new Block
}
*///? }
