//~ uses_tree_grower

package fabiofdez.knots_and_rings;

import fabiofdez.knots_and_rings.block.TreeSeedBlock;
import fabiofdez.knots_and_rings.block.WaterloggedTreeSeedBlock;
import fabiofdez.knots_and_rings.feature.SaplingType;
import fabiofdez.knots_and_rings.feature.SaplingTypeID;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
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
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static fabiofdez.knots_and_rings.feature.SaplingTypeID.*;

//? if fabric {
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
//? }

//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
*///? }

//? if forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///? }

//? if < 1.21 {
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
  //? if neoforge
  //public static DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(KnotsAndRings.MOD_ID);
  //? if forge
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
        .addAfter(SaplingType::lastSaplingInOrder, SaplingType::getAsOrderedItems);
  }

  public static void registerCompostables(CompostableRegisterEvent event) {
    TreeSeeds.forEach((seed) -> event.accept(seed.asItem(), 0.3F));
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

    public static BlockConstructor seedBlock(Supplier<Block> saplingBlock) {
      return (props) -> new TreeSeedBlock(saplingBlock.get(), forSeed(props));
    }

    public static BlockConstructor aquaticSeedBlock(Supplier<Block> saplingBlock) {
      return (props) -> new WaterloggedTreeSeedBlock(saplingBlock.get(), forSeed(props));
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

    public static void add(SaplingTypeID type, BlockConstructor blockClass) {
      SEEDS.put(type, register(new BlockDef.Builder(type.seedId(), blockClass)));
    }

    public static void mapSeedFor(SaplingType type) {
      BlockSupplier seedBlock = SEEDS.get(type.id());
      if (seedBlock != null) type.setSeed(seedBlock);
    }

    public static void forEach(Consumer<Block> action) {
      for (BlockSupplier seed : SEEDS.values()) action.accept(seed.get());
    }

    public static void initialize() {
    }

    static {
      add(ACACIA, seedBlock(() -> Blocks.ACACIA_SAPLING));
      add(BIRCH, seedBlock(() -> Blocks.BIRCH_SAPLING));
      add(CHERRY, seedBlock(() -> Blocks.CHERRY_SAPLING));
      add(DARK_OAK, seedBlock(() -> Blocks.DARK_OAK_SAPLING));
      add(JUNGLE, seedBlock(() -> Blocks.JUNGLE_SAPLING));
      add(MANGROVE, aquaticSeedBlock(() -> Blocks.MANGROVE_PROPAGULE));
      add(OAK, seedBlock(() -> Blocks.OAK_SAPLING));
      //? if > 1.21.1
      add(PALE_OAK, seedBlock(() -> Blocks.PALE_OAK_SAPLING));
      add(SPRUCE, seedBlock(() -> Blocks.SPRUCE_SAPLING));
    }
  }

  public static class SaplingStems {
    private static final Map<SaplingTypeID, BlockSupplier> STEMS = new LinkedHashMap<>();

    static Block mangrovePropaguleBlock(BlockBehaviour.Properties props) {
      return new MangrovePropaguleBlock(/*? if > 1.21 { */TreeGrower.MANGROVE, /*? } */forSapling(props));
    }

    public static BlockConstructor saplingBlock(Supplier<TreeGrower> treeGrower) {
      return (props) -> new SaplingBlock(treeGrower.get(), forSapling(props));
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

    public static void add(SaplingTypeID type, BlockConstructor blockClass) {
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
      add(ACACIA, saplingBlock(() -> TreeGrower.ACACIA));
      add(BIRCH, saplingBlock(() -> TreeGrower.BIRCH));
      add(CHERRY, saplingBlock(() -> TreeGrower.CHERRY));
      add(DARK_OAK, saplingBlock(() -> TreeGrower.DARK_OAK));
      add(JUNGLE, saplingBlock(() -> TreeGrower.JUNGLE));
      add(MANGROVE, SaplingStems::mangrovePropaguleBlock);
      add(OAK, saplingBlock(() -> TreeGrower.OAK));
      //? if > 1.21.1
      add(PALE_OAK, saplingBlock(() -> TreeGrower.PALE_OAK));
      add(SPRUCE, saplingBlock(() -> TreeGrower.SPRUCE));
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

    private record Builder(String name, BlockConstructor blockClass, BiFunction<Block, Item.Properties, BlockItem> itemClass) {
      Builder(String name, BlockConstructor blockClass) {
        this(name, blockClass, BlockItem::new);
      }
    }
  }

  public interface BlockConstructor extends Function<BlockBehaviour.Properties, Block> {
  }
}
