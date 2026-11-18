package fabiofdez.knots_and_rings.mixin.mstv_base;

//? if fabric {
import de.pnku.mstv_base.item.MoreStickVariantItems;
import fabiofdez.knots_and_rings.ModItems;
import fabiofdez.knots_and_rings.compat.ModRegistry;
import fabiofdez.knots_and_rings.feature.SaplingType;
import fabiofdez.knots_and_rings.feature.SaplingTypeID;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.pnku.mstv_base.item.MoreStickVariantItems.*;
import static fabiofdez.knots_and_rings.feature.SaplingTypeID.*;

@SuppressWarnings({"UnusedMixin", "unused"})
@Mixin(MoreStickVariantItems.class)
public class MoreStickVariantsMixin {

  @Unique
  private static final Map<SaplingTypeID, ModItems.ItemSupplier> STICKS = new LinkedHashMap<>();

  @Inject(method = "registerStickItems", at = @At("RETURN"))
  private static void knots_and_rings$mapSaplingsToSticks(CallbackInfo ci) {
    ModRegistry.registerForBootstrap(MoreStickVariantsMixin::applyMapping);

    add(ACACIA, ACACIA_STICK);
    add(BIRCH, BIRCH_STICK);
    add(CHERRY, CHERRY_STICK);
    add(DARK_OAK, DARK_OAK_STICK);
    add(JUNGLE, JUNGLE_STICK);
    add(MANGROVE, MANGROVE_STICK);
    add(OAK, Items.STICK);
    //? if > 1.21.1
    add(PALE_OAK, PALE_OAK_STICK);
    add(SPRUCE, SPRUCE_STICK);
  }

  @Unique
  private static void add(SaplingTypeID saplingType, Item stick) {
    STICKS.put(saplingType, () -> stick);
  }

  @Unique
  private static void applyMapping() {
    STICKS.forEach((id, stick) -> SaplingType.of(id).setStick(stick));
  }
}
//? }
