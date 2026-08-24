//~ uses_tree_grower

package fabiofdez.knots_and_rings;

import fabiofdez.knots_and_rings.block.state.SaplingType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

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

  private static BlockSupplier registerBlockOnly(BlockDef.Builder builder) {
    return BlockDef.create(builder);
  }

  private static BlockSupplier register(BlockDef.Builder builder) {
    BlockSupplier block = registerBlockOnly(builder);
    ModItems.register(builder.name, (props) -> builder.itemClass.apply(block.get(), props));
    return block;
  }

  public static void initialize() {
    SaplingStems.initialize();
  }

  //? if fabric {
  public interface BlockSupplier extends Supplier<Block> {
  }
  //? } else {
  /*public static void register(IEventBus eventBus) {
    BLOCKS.register(eventBus);
  }
  *///? }

  public static class SaplingStems {
    private static final Map<String, BlockSupplier> STEMS = new HashMap<>();

    public static String saplingId(String name) {
      return name.replaceAll("_stem$", "");
    }

    static Block cherrySaplingBlock(BlockBehaviour.Properties props) {
      return new SaplingBlock(
          TreeGrower.CHERRY,
          forSapling(props).mapColor(MapColor.COLOR_PINK).sound(SoundType.CHERRY_SAPLING)
      );
    }

    static Block mangrovePropaguleBlock(BlockBehaviour.Properties props) {
      return new MangrovePropaguleBlock(/*? > 1.21 >> 'forSapling' */TreeGrower.MANGROVE, forSapling(props));
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

    public static void add(String name, Function<BlockBehaviour.Properties, Block> blockClass) {
      STEMS.put(saplingId(name), registerBlockOnly(new BlockDef.Builder(name, blockClass)));
    }

    public static void mapStemFor(SaplingType type) {
      ResourceLocation saplingId = BuiltInRegistries.BLOCK.getKey(type.sapling());
      BlockSupplier stemBlock = STEMS.get(saplingId.getPath());
      if (stemBlock != null) type.setStem(stemBlock);
    }

    public static void forEach(Consumer<Block> action) {
      for (BlockSupplier stem : STEMS.values()) action.accept(stem.get());
    }

    public static void initialize() {
    }

    static {
      add("acacia_sapling_stem", (props) -> saplingBlock(TreeGrower.ACACIA, props));
      add("birch_sapling_stem", (props) -> saplingBlock(TreeGrower.BIRCH, props));
      add("cherry_sapling_stem", SaplingStems::cherrySaplingBlock);
      add("dark_oak_sapling_stem", (props) -> saplingBlock(TreeGrower.DARK_OAK, props));
      add("jungle_sapling_stem", (props) -> saplingBlock(TreeGrower.JUNGLE, props));
      add("mangrove_propagule_stem", SaplingStems::mangrovePropaguleBlock);
      add("oak_sapling_stem", (props) -> saplingBlock(TreeGrower.OAK, props));
      //? > 1.21.1
      add("pale_oak_sapling_stem", (props) -> paleOakSaplingBlock(TreeGrower.PALE_OAK, props));
      add("spruce_sapling_stem", (props) -> saplingBlock(TreeGrower.SPRUCE, props));
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
