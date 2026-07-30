package fabiofdez.knots_and_rings.mixin.regions_unexplored;

//? 1.20.1 {

/*import com.google.common.collect.ImmutableMap;
import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.block.LogBlock;
import fabiofdez.knots_and_rings.compat.regions_unexplored.AspenLogBlock;
import fabiofdez.knots_and_rings.compat.regions_unexplored.PineLogBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.regions_unexplored.registry.RUBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(RUBlocks.class)
public class RUBlocksMixin {

  @Unique
  private static final Map<String, Supplier<Block>> TYPES_MAP = ImmutableMap.ofEntries(
      Map.entry("silver_birch_log", RUBlocksMixin::knots_and_rings$customAspenLog),
      Map.entry("pine_log", RUBlocksMixin::knots_and_rings$customPineLog)
  );

  @ModifyArgs(method = "addBlocks()V", at = @At(value = "INVOKE", target = "Lnet/regions_unexplored/registry/BlockRegistry;registerDefaultBlock(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/minecraftforge/registries/RegistryObject;"), remap = false)
  private static void knots_and_rings$customLogs(Args args) {
    String blockId = args.get(0);
    if (!TYPES_MAP.containsKey(blockId)) return;

    args.set(1, TYPES_MAP.get(blockId));
  }

  @Unique
  private static Block knots_and_rings$customAspenLog() {
    return new AspenLogBlock(BlockBehaviour.Properties
        .of()
        .mapColor((state) -> state.getValue(LogBlock.AXIS) == Direction.Axis.Y ? MapColor.SAND : MapColor.QUARTZ)
        .instrument(NoteBlockInstrument.BASS)
        .strength(2.0F)
        .sound(SoundType.BAMBOO_WOOD)
        .ignitedByLava());
  }

  @Unique
  private static Block knots_and_rings$customPineLog() {
    return new PineLogBlock(BlockBehaviour.Properties
        .of()
        .mapColor(MapColor.WOOD)
        .instrument(NoteBlockInstrument.BASS)
        .strength(2.0F)
        .sound(SoundType.BAMBOO_WOOD)
        .ignitedByLava());
  }
}
*///? }
