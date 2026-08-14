//~ uses_tree_grower

package fabiofdez.knots_and_rings;

import fabiofdez.knots_and_rings.block.state.SaplingType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

//? fabric {
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;
//? }

//? neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
*///? }

//? forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///? }

//? < 1.21 {
/*import net.minecraft.world.level.block.grower.AcaciaTreeGrower;
import net.minecraft.world.level.block.grower.BirchTreeGrower;
import net.minecraft.world.level.block.grower.CherryTreeGrower;
import net.minecraft.world.level.block.grower.DarkOakTreeGrower;
import net.minecraft.world.level.block.grower.JungleTreeGrower;
import net.minecraft.world.level.block.grower.MangroveTreeGrower;
import net.minecraft.world.level.block.grower.OakTreeGrower;
import net.minecraft.world.level.block.grower.SpruceTreeGrower;
*///? }

public class ModBlocks {
  //? neoforge
  //public static DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(KnotsAndRings.MOD_ID);
  //? forge
  //public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, KnotsAndRings.MOD_ID);

  public static final Map<String, BlockSupplier> STEM_MAPPING;

  public static Map<String, BlockSupplier> buildStemBlocks() {
    return Arrays
        .stream(ModBlockBuilder.values())
        .collect(Collectors.toMap(ModBlockBuilder::saplingName, ModBlocks::registerBlockOnly));
  }

  //? if fabric {
  @SuppressWarnings("SameParameterValue")
  private static BlockSupplier registerBlockOnly(ModBlockBuilder builder) {
    Block registeredBlock = BlockDef.create(builder).register();
    return () -> registeredBlock;
  }

  private static BlockSupplier register(ModBlockBuilder builder) {
    BlockDef toRegister = BlockDef.create(builder);

    ResourceKey<Item> itemKey = KnotsAndRings.itemKey(builder.name);
    Item.Properties itemProps = new Item.Properties() /*? if > 1.21.1 >> ';' */.setId(itemKey);
    BlockItem blockItem = builder.itemClass.apply(toRegister.block(), itemProps);

    Block registeredBlock = toRegister.register();
    Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);

    return () -> registeredBlock;
  }
  //? } else {
  /*private static BlockSupplier registerBlockOnly(ModBlockBuilder builder) {
    //? neoforge
    //return BLOCKS.registerBlock(builder.name, builder.blockClass);
    //? forge
    //return BLOCKS.register(builder.name, () -> builder.blockClass.apply(BlockBehaviour.Properties.of()));
  }

  private static BlockSupplier register(ModBlockBuilder builder) {
    BlockSupplier registeredBlock = registerBlockOnly(builder);

    ////? > 1.21.1
    //ModItems.ITEMS.registerItem(builder.name, (props) -> builder.itemClass.apply(registeredBlock.get(), props));
    ////? <= 1.21.1
    ////ModItems.ITEMS.register(builder.name, () -> builder.itemClass.apply(registeredBlock.get(), new Item.Properties()));

    return registeredBlock;
  }

  public static void register(IEventBus eventBus) {
    BLOCKS.register(eventBus);
  }
  *///? }

  public static void addCreative(KnotsAndRings.CreativeTabsModifier modifier) {
    modifier.forTab(CreativeModeTabs.TOOLS_AND_UTILITIES).addBlocks((entries) -> {
//      entries.accept();
    });
  }

  enum ModBlockBuilder {
    ACACIA_STEM("acacia_sapling_stem", (props) -> saplingBlock(TreeGrower.ACACIA, props)),
    BIRCH_STEM("birch_sapling_stem", (props) -> saplingBlock(TreeGrower.BIRCH, props)),
    CHERRY_STEM("cherry_sapling_stem", (props) -> cherrySaplingBlock(TreeGrower.CHERRY, props)),
    DARK_OAK_STEM("dark_oak_sapling_stem", (props) -> saplingBlock(TreeGrower.DARK_OAK, props)),
    JUNGLE_STEM("jungle_sapling_stem", (props) -> saplingBlock(TreeGrower.JUNGLE, props)),
    MANGROVE_STEM("mangrove_propagule_stem", (props) -> propaguleBlock(TreeGrower.MANGROVE, props)),
    OAK_STEM("oak_sapling_stem", (props) -> saplingBlock(TreeGrower.OAK, props)),
    //? > 1.21.1
    PALE_OAK_STEM("pale_oak_sapling_stem", (props) -> saplingBlock(TreeGrower.PALE_OAK, props)),
    SPRUCE_STEM("spruce_sapling_stem", (props) -> saplingBlock(TreeGrower.SPRUCE, props));

    private final String name;
    private final Function<BlockBehaviour.Properties, Block> blockClass;
    private final BiFunction<Block, Item.Properties, BlockItem> itemClass;

    ModBlockBuilder(String name, Function<BlockBehaviour.Properties, Block> blockClass) {
      this(name, blockClass, BlockItem::new);
    }

    ModBlockBuilder(String name, Function<BlockBehaviour.Properties, Block> blockClass, BiFunction<Block, Item.Properties, BlockItem> itemClass) {
      this.name = name;
      this.blockClass = blockClass;
      this.itemClass = itemClass;
    }

    public static Block cherrySaplingBlock(TreeGrower treeGrower, BlockBehaviour.Properties props) {
      return new SaplingBlock(treeGrower, forSapling(props).sound(SoundType.CHERRY_SAPLING));
    }

    public static Block propaguleBlock(/*? > 1.21 >> 'BlockBehaviour' */TreeGrower treeGrower, BlockBehaviour.Properties props) {
      return new MangrovePropaguleBlock(/*? > 1.21 >> 'forSapling' */treeGrower, forSapling(props));
    }

    public static Block saplingBlock(TreeGrower treeGrower, BlockBehaviour.Properties props) {
      return new SaplingBlock(treeGrower, forSapling(props));
    }

    public static BlockBehaviour.Properties forSapling(BlockBehaviour.Properties baseProps) {
      return baseProps
          .mapColor(MapColor.PLANT)
          .noCollission()
          .randomTicks()
          .instabreak()
          .sound(SoundType.GRASS)
          .pushReaction(PushReaction.DESTROY);
    }

    public static String saplingName(ModBlockBuilder builder) {
      return builder.name.replaceAll("_stem$", "");
    }
  }

  //? fabric {
  public interface BlockSupplier extends Supplier<Block> {
  }

  public static class BlockDef {
    private final ResourceKey<Block> key;
    private final Block block;

    private BlockDef(ResourceKey<Block> key, Block block) {
      this.key = key;
      this.block = block;
    }

    static BlockDef create(ModBlockBuilder builder) {
      ResourceKey<Block> blockKey = KnotsAndRings.blockKey(builder.name);
      BlockBehaviour.Properties props = BlockBehaviour.Properties.of();
      Block toRegister = builder.blockClass.apply(props /*? if > 1.21.1 >> ');' */.setId(blockKey));

      return new BlockDef(blockKey, toRegister);
    }

    public Block block() {
      return block;
    }

    public Block register() {
      return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }
  }
  //? }

  public static void initialize() {
    SaplingType.mapVanillaBlocks();

    for (SaplingType type : SaplingType.definedValues()) {
      ResourceLocation saplingId = BuiltInRegistries.BLOCK.getKey(type.sapling());
      Block stem = STEM_MAPPING.get(saplingId.getPath()).get();
      if (stem == null) continue;

      type.setStem(stem);
      SaplingType.mapStem(stem, type);
    }

    SaplingType.freezeRegistry();
  }

  static {
    STEM_MAPPING = buildStemBlocks();
  }
}
