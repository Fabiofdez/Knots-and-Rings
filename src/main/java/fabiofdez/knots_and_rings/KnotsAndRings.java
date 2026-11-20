package fabiofdez.knots_and_rings;

import fabiofdez.knots_and_rings.platform.Platform;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

//? if !fabric {
/*import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
*///? }
//? if fabric {
import fabiofdez.knots_and_rings.platform.fabric.FabricPlatform;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.core.registries.Registries;
//? } else if neoforge {
/*import fabiofdez.knots_and_rings.platform.neoforge.NeoforgePlatform;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
 *///? } else if forge {
/*import fabiofdez.knots_and_rings.platform.forge.ForgePlatform;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
 *///? }

@SuppressWarnings("LoggingSimilarMessage")
public class KnotsAndRings {

  public static final String MOD_ID = /*$ mod_id*/ "knots_and_rings";
  public static final String MOD_VERSION = /*$ mod_version*/ "2.4.0";
  public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Knots & Rings";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  private static final Platform PLATFORM = createPlatformInstance();

  public static void onInitialize() {
    LOGGER.info("Initializing {} on {}", MOD_ID, KnotsAndRings.xplat().loader());
    LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
  }

  public static void onInitializeClient() {
    LOGGER.info("Initializing {} Client on {}", MOD_ID, KnotsAndRings.xplat().loader());
    LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
  }

  public static Platform xplat() {
    return PLATFORM;
  }

  private static Platform createPlatformInstance() {
    //? if fabric {
    return new FabricPlatform();
    //? } else if neoforge {
    /*return new NeoforgePlatform();
     *///? } else if forge {
    /*return new ForgePlatform();
     *///? }
  }

  public static Identifier id(String path) {
    return id(MOD_ID, path);
  }

  public static Identifier minecraftId(String path) {
    return id(Identifier.DEFAULT_NAMESPACE, path);
  }

  public static Identifier id(String namespace, String path) {
    //? if >= 1.21
    return Identifier.fromNamespaceAndPath(namespace, path);
    //? if < 1.21
    //return new Identifier(namespace, path);
  }

  public static Identifier fromKey(ResourceKey<Block> blockKey) {
    //? if < 1.21.11
    //return blockKey.location();
    //? if >= 1.21.11
    return blockKey.identifier();
  }

  //? if fabric {
  public static ResourceKey<Block> blockKey(String path) {
    return ResourceKey.create(Registries.BLOCK, id(path));
  }

  public static ResourceKey<Item> itemKey(String path) {
    return ResourceKey.create(Registries.ITEM, id(path));
  }
  //? }

  public static void modifyCreativeTabs(/*? if !fabric { *//*BuildCreativeModeTabContentsEvent event, *//*? } */Consumer<CreativeTabsModifier> runnable) {
    runnable.accept(new CreativeTabsModifier(/*? if !fabric { *//*event*//*? } */));
  }

  public static class CreativeTabsModifier {

    private ResourceKey<CreativeModeTab> currentTab;
    //? if !fabric {
    /*private BuildCreativeModeTabContentsEvent event;

    public CreativeTabsModifier(BuildCreativeModeTabContentsEvent event) {
      this.event = event;
    }
    *///? }

    public CreativeTabsModifier switchTo(ResourceKey<CreativeModeTab> tab) {
      this.currentTab = tab;
      return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public CreativeTabsModifier add(Collection<ItemStack> toAdd) {
      return addEntries((entries) -> entries.acceptAll(toAdd));
    }

    @SuppressWarnings("UnusedReturnValue")
    public CreativeTabsModifier addAfter(Supplier<ItemLike> item, Supplier<Collection<ItemStack>> toAdd) {
      // TODO: add seeds before saplings?
      //? if !fabric {
      /*return addEntries((entries) -> insertAllAfter(entries, item.get(), toAdd.get()));
       *///? } else if >= 26.1 {
      return addEntries((entries) -> entries.insertAfter(item.get(), toAdd.get()));
      //? } else {
      /*return addEntries((entries) -> entries.addAfter(item.get(), toAdd.get()));
       *///? }
    }

    private CreativeTabsModifier addEntries(ItemEntryModifier entryModifier) {
      if (currentTab == null) return this;
      //? if !fabric
      //if (event == null || event.getTabKey() != currentTab) return this;

      //? if fabric
      CreativeModeTabEvents.modifyOutputEvent(currentTab).register(entryModifier::accept);
      //? if !fabric
      //entryModifier.accept(event);

      return this;
    }

    //? if !fabric {
    /*private void insertAllAfter(BuildCreativeModeTabContentsEvent event, ItemLike lastItem, Collection<ItemStack> toAdd) {
      AtomicReference<ItemStack> currentItem = new AtomicReference<>(lastItem.asItem().getDefaultInstance());
      toAdd.forEach((newItem) -> {
        //? if forge
        //event.getEntries().putAfter(currentItem.get(), newItem, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        //? if neoforge
        //event.insertAfter(currentItem.get(), newItem, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        currentItem.set(newItem);
      });
    }
    *///? }

    private interface ItemEntryModifier extends/*? if fabric { */ Consumer<FabricCreativeModeTabOutput> /*? } else { */ /*Consumer<BuildCreativeModeTabContentsEvent> *//*? } */ {
    }
  }

  public static String packageName() {
    return KnotsAndRings.class.getPackageName();
  }
}
