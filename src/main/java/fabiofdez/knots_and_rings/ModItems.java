package fabiofdez.knots_and_rings;

import net.minecraft.world.item.Item;

import java.util.function.Function;

//? fabric {
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;
//? }

//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
*///? } else {
import net.minecraft.core.registries.BuiltInRegistries;
//? }

//? forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///? }

public class ModItems {
  //? neoforge
  //public static DeferredRegister.Items ITEMS = DeferredRegister.createItems(KnotsAndRings.MOD_ID);
  //? forge
  //public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, KnotsAndRings.MOD_ID);

  public static ItemSupplier register(String name, Function<Item.Properties, Item> itemBuilder) {
    Item.Properties itemProps = new Item.Properties();

    //? if neoforge {
    /*return ITEMS.registerItem(name, itemBuilder);
     *///? } else if forge {
    /*return ITEMS.register(name, () -> itemBuilder.apply(itemProps));
     *///? } else {
    ResourceKey<Item> itemKey = KnotsAndRings.itemKey(name);
    Item toRegister = itemBuilder.apply(itemProps/*? if > 1.21.1 { */.setId(itemKey)/*? } */);
    Item registered = Registry.register(BuiltInRegistries.ITEM, itemKey, toRegister);

    return () -> registered;
    //? }
  }

  public static void initialize() {
  }

  //? if fabric {
  public interface ItemSupplier extends Supplier<Item> {
  }
  //? } else {
  /*public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
  *///? }
}
