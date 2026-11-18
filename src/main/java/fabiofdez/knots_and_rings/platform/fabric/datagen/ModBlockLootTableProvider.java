package fabiofdez.knots_and_rings.platform.fabric.datagen;

//? if fabric {

import fabiofdez.knots_and_rings.block.state.BlockPosOffset;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Properties;
import fabiofdez.knots_and_rings.feature.GrowingSapling.Stage;
import fabiofdez.knots_and_rings.feature.SaplingShape;
import fabiofdez.knots_and_rings.feature.SaplingType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements./*? if < 1.21.11 { */critereon/*? } else if < 26.2 { *//*criterion*//*? } else { *//*predicates*//*? } */.StatePropertiesPredicate;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.Function;
import java.util.stream.Stream;

import static fabiofdez.knots_and_rings.block.state.BlockPosOffset.*;
import static fabiofdez.knots_and_rings.feature.GrowingSapling.Stage.*;
import static fabiofdez.knots_and_rings.feature.SaplingShape.Layout.*;
import static net.minecraft.advancements./*? if < 1.21.11 { */critereon/*? } else if < 26.2 { *//*criterion*//*? } else { *//*predicates*//*? } */.StatePropertiesPredicate.Builder.properties;
import static net.minecraft.world.level.storage.loot.LootPool.lootPool;
import static net.minecraft.world.level.storage.loot.LootTable.lootTable;
import static net.minecraft.world.level.storage.loot.entries.AlternativesEntry.alternatives;
import static net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem;
import static net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.setCount;
import static net.minecraft.world.level.storage.loot.predicates.AllOfCondition.allOf;
import static net.minecraft.world.level.storage.loot.predicates.AnyOfCondition.anyOf;

//? if >= 1.21 {
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
//? }

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {

  private static final BlockConditionProvider IS_SELF_ROOT = (block) -> blockCondition(block, root(SELF));

  protected ModBlockLootTableProvider(FabricDataOutput dataOutput/*? if >= 1.21 { */, CompletableFuture<HolderLookup.Provider> registryLookup/*? } */) {
    super(dataOutput/*? if >= 1.21 { */, registryLookup/*? } */);
  }

  @Override
  public void generate() {
    for (SaplingType type : SaplingType.all()) {
      add(type.sapling(), createSaplingDrops(type));
      add(type.stem(), createSaplingStemDrops(type));
    }
  }

  private LootTable.Builder createSaplingDrops(SaplingType type) {
    Block sapling = type.sapling();
    LootItemCondition.Builder selfRoot = IS_SELF_ROOT.apply(sapling);
    LootItemCondition.Builder isSingleton = blockCondition(sapling, shape(SINGLETON));
    LootItemCondition.Builder isWeakOrHidden = anyOf(blockCondition(sapling, stage(HIDDEN, DECAYING)));

    LootPool.Builder saplingPool = lootPool().add(alternatives(
        lootTableItem(sapling)
            .when(allOf(selfRoot, anyOf(blockCondition(sapling, stage(TALL_SAPLING, GIANT)))))
            .apply(setCount(UniformGenerator.between(1, 3))),
        lootTableItem(sapling)
            .when(anyOf(selfRoot.invert(), allOf(isSingleton, isWeakOrHidden.invert())))
            .apply(setCount(ConstantValue.exactly(1)))
    ));

    return applyExplosionDecay(sapling, lootTable().withPool(saplingPool));
  }

  private LootTable.Builder createSaplingStemDrops(SaplingType type) {
    Block stem = type.stem();
    LootItemCondition.Builder selfRoot = IS_SELF_ROOT.apply(stem);

    LootPool.Builder stickPool = lootPool()
        .add(lootTableItem(type.stick()))
        .when(selfRoot)
        .apply(setCount(UniformGenerator.between(1, 4)));

    return applyExplosionDecay(stem, lootTable().withPool(stickPool));
  }

  private static LootItemBlockStatePropertyCondition.Builder propsOf(Block block) {
    return LootItemBlockStatePropertyCondition.hasBlockStateProperties(block);
  }

  private static LootItemCondition.Builder blockCondition(Block block, StatePropertiesPredicate.Builder propValue) {
    return propsOf(block).setProperties(propValue);
  }

  private static LootItemCondition.Builder[] blockCondition(Block block, StatePropertiesPredicate.Builder... propValues) {
    return Stream
        .of(propValues)
        .map((propValue) -> blockCondition(block, propValue))
        .toArray(LootItemCondition.Builder[]::new);
  }

  private static <T extends Comparable<T> & StringRepresentable> StatePropertiesPredicate.Builder propValue(Property<T> property, T value) {
    return properties().hasProperty(property, value.getSerializedName());
  }

  @SafeVarargs
  private static <T extends Comparable<T> & StringRepresentable> StatePropertiesPredicate.Builder[] propValues(Property<T> property, T... values) {
    return Stream
        .of(values)
        .map((value) -> propValue(property, value))
        .toArray(StatePropertiesPredicate.Builder[]::new);
  }

  @SuppressWarnings("unused")
  private static StatePropertiesPredicate.Builder stage(Stage value) {
    return propValue(Properties.GROWTH_STAGE, value);
  }

  @SuppressWarnings("SameParameterValue")
  private static StatePropertiesPredicate.Builder[] stage(Stage... values) {
    return propValues(Properties.GROWTH_STAGE, values);
  }

  @SuppressWarnings("SameParameterValue")
  private static StatePropertiesPredicate.Builder shape(SaplingShape.Layout value) {
    return propValue(Properties.TREE_SHAPE, value);
  }

  @SuppressWarnings("unused")
  private static StatePropertiesPredicate.Builder[] shape(SaplingShape.Layout... values) {
    return propValues(Properties.TREE_SHAPE, values);
  }

  @SuppressWarnings("SameParameterValue")
  private static StatePropertiesPredicate.Builder root(BlockPosOffset value) {
    return propValue(Properties.TREE_ROOT, value);
  }

  @SuppressWarnings("unused")
  private static StatePropertiesPredicate.Builder[] root(BlockPosOffset... values) {
    return propValues(Properties.TREE_ROOT, values);
  }

  private interface BlockConditionProvider extends Function<Block, LootItemCondition.Builder> {
  }
}
//? }
