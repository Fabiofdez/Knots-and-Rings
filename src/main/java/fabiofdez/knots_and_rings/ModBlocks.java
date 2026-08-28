//~ uses_tree_grower

package fabiofdez.knots_and_rings;

import fabiofdez.knots_and_rings.block.TreeSeedBlock;
import fabiofdez.knots_and_rings.block.WaterloggedTreeSeedBlock;
import fabiofdez.knots_and_rings.feature.SaplingType;
import fabiofdez.knots_and_rings.feature.SaplingTypeID;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static fabiofdez.knots_and_rings.feature.SaplingTypeID.*;

//? fabric {
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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

  private static BlockSupplier registerBlockOnly(BlockDef.Builder builder) {
    return BlockDef.create(builder);
  }

  private static BlockSupplier register(BlockDef.Builder builder) {
    BlockSupplier block = BlockDef.create(builder);
    ModItems.register(builder.name, (props) -> builder.itemClass.apply(block.get(), props));
    return block;
  }

  public static void initialize() {
    SaplingStems.initialize();
    TreeSeeds.initialize();
  }

  public static void addCreative(KnotsAndRings.CreativeTabsModifier modifier) {
    modifier
        .switchTo(CreativeModeTabs.NATURAL_BLOCKS)
        .addAfter(SaplingType::lastSaplingInOrder, TreeSeeds::getAsOrderedItems);
  }

  public static void registerCompostables(CompostableRegisterEvent event) {
    ModBlocks.TreeSeeds.forEach((seed) -> event.accept(seed.asItem(), 0.3F));
  }

  //? if fabric {
  public interface BlockSupplier extends Supplier<Block> {
  }
  //? } else {
  /*public static void register(IEventBus eventBus) {
    BLOCKS.register(eventBus);
  }
  *///? }

  public interface CompostableRegisterEvent extends BiConsumer<Item, Float> {
  }

  public static class TreeSeeds {
    private static final Map<SaplingTypeID, BlockSupplier> SEEDS = new LinkedHashMap<>();

    public static Block seedBlock(Block saplingBlock, BlockBehaviour.Properties props) {
      return new TreeSeedBlock(saplingBlock, forSeed(props));
    }

    public static Block aquaticSeedBlock(Block saplingBlock, BlockBehaviour.Properties props) {
      return new WaterloggedTreeSeedBlock(saplingBlock, forSeed(props));
    }

    public static BlockBehaviour.Properties forSeed(BlockBehaviour.Properties baseProps) {
      return baseProps
          .offsetType(BlockBehaviour.OffsetType.XZ)
          .mapColor(MapColor.PLANT)
          .noCollission()
          .randomTicks()
          .instabreak()
          .sound(SoundType.CROP)
          .pushReaction(PushReaction.DESTROY);
    }

    public static void add(SaplingTypeID type, Function<BlockBehaviour.Properties, Block> blockClass) {
      SEEDS.put(type, register(new BlockDef.Builder(type.seedId(), blockClass)));
    }

    public static void mapSeedFor(SaplingType type) {
      BlockSupplier seedBlock = SEEDS.get(type.id());
      if (seedBlock != null) type.setSeed(seedBlock);
    }

    public static void forEach(Consumer<Block> action) {
      for (BlockSupplier seed : SEEDS.values()) action.accept(seed.get());
    }

    private static List<ItemStack> getAsOrderedItems() {
      return SEEDS.values().stream().map((seed) -> seed.get().asItem().getDefaultInstance()).toList();
    }

    public static void initialize() {
    }

    static {
      add(OAK, (props) -> seedBlock(Blocks.OAK_SAPLING, props));
      add(SPRUCE, (props) -> seedBlock(Blocks.SPRUCE_SAPLING, props));
      add(BIRCH, (props) -> seedBlock(Blocks.BIRCH_SAPLING, props));
      add(JUNGLE, (props) -> seedBlock(Blocks.JUNGLE_SAPLING, props));
      add(ACACIA, (props) -> seedBlock(Blocks.ACACIA_SAPLING, props));
      add(DARK_OAK, (props) -> seedBlock(Blocks.DARK_OAK_SAPLING, props));
      add(MANGROVE, (props) -> aquaticSeedBlock(Blocks.MANGROVE_PROPAGULE, props));
      add(CHERRY, (props) -> seedBlock(Blocks.CHERRY_SAPLING, props));
      //? > 1.21.1
      add(PALE_OAK, (props) -> seedBlock(Blocks.PALE_OAK_SAPLING, props));
    }
  }

  public static class SaplingStems {
    private static final Map<SaplingTypeID, BlockSupplier> STEMS = new LinkedHashMap<>();

    static Block cherrySaplingBlock(BlockBehaviour.Properties props) {
      return new SaplingBlock(
          TreeGrower.CHERRY,
          forSapling(props).mapColor(MapColor.COLOR_PINK).sound(SoundType.CHERRY_SAPLING)
      );
    }

    static Block mangrovePropaguleBlock(BlockBehaviour.Properties props) {
      return new MangrovePropaguleBlock(/*? > 1.21 { */TreeGrower.MANGROVE, /*? } */forSapling(props));
    }

    public static Block paleOakSaplingBlock(TreeGrower treeGrower, BlockBehaviour.Properties props) {
      return new SaplingBlock(treeGrower, forSapling(props).mapColor(MapColor.METAL));
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

    public static void add(SaplingTypeID type, Function<BlockBehaviour.Properties, Block> blockClass) {
      STEMS.put(type, registerBlockOnly(new BlockDef.Builder(type.stemId(), blockClass)));
    }

    public static void mapStemFor(SaplingType type) {
      BlockSupplier stemBlock = STEMS.get(type.id());
      if (stemBlock != null) type.setStem(stemBlock);
    }

    public static void forEach(Consumer<Block> action) {
      for (BlockSupplier stem : STEMS.values()) action.accept(stem.get());
    }

    public static void initialize() {
    }

    static {
      add(OAK, (props) -> saplingBlock(TreeGrower.OAK, props));
      add(SPRUCE, (props) -> saplingBlock(TreeGrower.SPRUCE, props));
      add(BIRCH, (props) -> saplingBlock(TreeGrower.BIRCH, props));
      add(JUNGLE, (props) -> saplingBlock(TreeGrower.JUNGLE, props));
      add(ACACIA, (props) -> saplingBlock(TreeGrower.ACACIA, props));
      add(DARK_OAK, (props) -> saplingBlock(TreeGrower.DARK_OAK, props));
      add(MANGROVE, SaplingStems::mangrovePropaguleBlock);
      add(CHERRY, SaplingStems::cherrySaplingBlock);
      //? > 1.21.1
      add(PALE_OAK, (props) -> paleOakSaplingBlock(TreeGrower.PALE_OAK, props));
    }
  }

  public static class BlockDef {

    private BlockDef() {
    }

    static BlockSupplier create(Builder builder) {
      BlockBehaviour.Properties blockProps = BlockBehaviour.Properties.of();

      //? if neoforge {
      /*return BLOCKS.registerBlock(builder.name, builder.blockClass);
       *///? } else if forge {
      /*return BLOCKS.register(builder.name, () -> builder.blockClass.apply(blockProps));
       *///? } else {
      ResourceKey<Block> blockKey = KnotsAndRings.blockKey(builder.name);
      Block toRegister = builder.blockClass.apply(blockProps /*? if > 1.21.1 { */.setId(blockKey)/*? } */);
      Block registered = Registry.register(BuiltInRegistries.BLOCK, blockKey, toRegister);

      return () -> registered;
      //? }
    }

    private record Builder(String name, Function<BlockBehaviour.Properties, Block> blockClass, BiFunction<Block, Item.Properties, BlockItem> itemClass) {
      Builder(String name, Function<BlockBehaviour.Properties, Block> blockClass) {
        this(name, blockClass, BlockItem::new);
      }
    }
  }
}
