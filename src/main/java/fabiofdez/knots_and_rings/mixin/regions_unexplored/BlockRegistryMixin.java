package fabiofdez.knots_and_rings.mixin.regions_unexplored;

//? 1.20.1 {

/*import fabiofdez.knots_and_rings.block.LogBlock;
import fabiofdez.knots_and_rings.util.LivingWoodBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.regions_unexplored.registry.BlockRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(BlockRegistry.class)
public class BlockRegistryMixin {

  @Redirect(method = "log", at = @At(value = "NEW", target = LivingWoodBlock.OLD_CONSTRUCTOR))
  private static RotatedPillarBlock knots_and_rings$customLogRU(BlockBehaviour.Properties properties) {
    return new LogBlock(properties);
  }

  @Redirect(method = "fireproofLog", at = @At(value = "NEW", target = LivingWoodBlock.OLD_CONSTRUCTOR))
  private static RotatedPillarBlock knots_and_rings$customFireproofLogRU(BlockBehaviour.Properties properties) {
    return new LogBlock(properties);
  }

  // TODO: override "alpha_log", new Block
}
*///? }
