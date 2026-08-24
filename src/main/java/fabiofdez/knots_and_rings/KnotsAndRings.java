package fabiofdez.knots_and_rings;

import fabiofdez.knots_and_rings.platform.Platform;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

//? !fabric
//import java.util.function.Supplier;

//? fabric {
import fabiofdez.knots_and_rings.platform.fabric.FabricPlatform;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.Registries;
//?} neoforge {
/*import fabiofdez.knots_and_rings.platform.neoforge.NeoforgePlatform;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
 *///?} forge {
/*import fabiofdez.knots_and_rings.platform.forge.ForgePlatform;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
 *///?}

@SuppressWarnings("LoggingSimilarMessage")
public class KnotsAndRings {

  public static final String MOD_ID = /*$ mod_id*/ "knots_and_rings";
  public static final String MOD_VERSION = /*$ mod_version*/ "2.2.2";
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
    //? fabric {
    return new FabricPlatform();
    //?} neoforge {
    /*return new NeoforgePlatform();
     *///?} forge {
    /*return new ForgePlatform();
     *///?}
  }

  public static ResourceLocation id(String path) {
    //? >= 1.21
    return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    //? < 1.21
    //return new ResourceLocation(MOD_ID, path);
  }

  public static ResourceLocation id(String namespace, String path) {
    //? >= 1.21
    return ResourceLocation.fromNamespaceAndPath(namespace, path);
    //? < 1.21
    //return new ResourceLocation(namespace, path);
  }

  public static ResourceLocation fromKey(ResourceKey<Block> blockKey) {
    //? < 1.21.11
    return blockKey.location();
    //? >= 1.21.11
    //return blockKey.identifier();
  }

  //? fabric {
  public static ResourceKey<Block> blockKey(String path) {
    return ResourceKey.create(Registries.BLOCK, id(path));
  }

  public static ResourceKey<Item> itemKey(String path) {
    return ResourceKey.create(Registries.ITEM, id(path));
  }
  //?}

  public static void modifyCreativeTabs(/*? if !fabric >> 'Consumer' */ /*BuildCreativeModeTabContentsEvent event, */Consumer<CreativeTabsModifier> runnable) {
    //? fabric
    runnable.accept(new CreativeTabsModifier());
    //? !fabric
    //runnable.accept(new CreativeTabsModifier(event));
  }

  public static class CreativeTabsModifier {
    private ResourceKey<CreativeModeTab> currentTab;

    public CreativeTabsModifier forTab(ResourceKey<CreativeModeTab> tab) {
      this.currentTab = tab;
      return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public CreativeTabsModifier addItems(ItemEntryModifier entryModifier) {
      //? fabric
      return addEntries((entries) -> entryModifier.accept((item) -> entries.accept(item.get())));
      //? !fabric
      //return addEntries(entryModifier);
    }

    @SuppressWarnings("UnusedReturnValue")
    public CreativeTabsModifier addBlocks(BlockEntryModifier entryModifier) {
      //? fabric
      return addEntries((entries) -> entryModifier.accept((item) -> entries.accept(item.get())));
      //? !fabric
      //return addEntries(entryModifier);
    }

    //? if fabric {
    private CreativeTabsModifier addEntries(ItemGroupEvents.ModifyEntries entryModifier) {
      if (currentTab == null) return this;
      ItemGroupEvents.modifyEntriesEvent(currentTab).register(entryModifier);
      return this;
    }

    public interface ItemEntryModifier extends Consumer<Consumer<ModItems.ItemSupplier>> {
    }

    public interface BlockEntryModifier extends Consumer<Consumer<ModBlocks.BlockSupplier>> {
    }
    //? } else {
    /*private BuildCreativeModeTabContentsEvent event;

    public CreativeTabsModifier(BuildCreativeModeTabContentsEvent event) {
      this.event = event;
    }

    private CreativeTabsModifier addEntries(Consumer<BuildCreativeModeTabContentsEvent> entryModifier) {
      if (this.event == null || this.currentTab == null) return this;
      if (this.event.getTabKey() != this.currentTab) return this;

      entryModifier.accept(this.event);
      return this;
    }

    public interface ItemEntryModifier extends Consumer<BuildCreativeModeTabContentsEvent> {
    }

    public interface BlockEntryModifier extends Consumer<BuildCreativeModeTabContentsEvent> {
    }
    *///? }
  }

  public static String packageName() {
    return KnotsAndRings.class.getPackageName();
  }
}
