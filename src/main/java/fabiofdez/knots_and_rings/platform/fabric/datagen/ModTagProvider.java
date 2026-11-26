package fabiofdez.knots_and_rings.platform.fabric.datagen;

//? if fabric {

import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.feature.PossibleSaplingShapes;
import fabiofdez.knots_and_rings.feature.SaplingType;
import fabiofdez.knots_and_rings.feature.SaplingTypeID;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//? if >= 1.21.11
import net.minecraft.data.tags.TagAppender;
//? if >= 26.2
//import net.minecraft.tags.BlockItemTags;

public class ModTagProvider extends FabricTagsProvider.BlockTagsProvider {

  public ModTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  protected void addTags(HolderLookup.Provider wrapperLookup) {
    TagBuilder<Block> saplingsTag = builderForBlockTag(BlockTags.SAPLINGS);
    saplingsTag.get().setReplace(false);

    TagBuilder<Block> treeReplaceableTag = builderForBlockTag(BlockTags.REPLACEABLE_BY_TREES);
    treeReplaceableTag.get().setReplace(false);

    TagBuilder<Block> maintainsFarmlandTag = builderForBlockTag(BlockTags.MAINTAINS_FARMLAND);
    maintainsFarmlandTag.get().setReplace(false);
    maintainsFarmlandTag.get().addTag(BlockTags.SAPLINGS);

    Set<SaplingType> vanillaSet = Stream
        .of(SaplingType.VANILLA_INVENTORY_ORDER)
        .map(SaplingType::of)
        .collect(Collectors.toUnmodifiableSet());

    for (SaplingType type : SaplingType.all()) {
      ResourceKey<Block> stemKey = KnotsAndRings.blockKey(type.id().stemId());
      ResourceKey<Block> seedKey = KnotsAndRings.blockKey(type.id().seedId());

      if (vanillaSet.contains(type)) {
        treeReplaceableTag.get().add(stemKey).add(seedKey);
        maintainsFarmlandTag.get().add(stemKey);
        saplingsTag.get().add(seedKey);
      } else {
        treeReplaceableTag.get().addOptional(stemKey).addOptional(seedKey);
        maintainsFarmlandTag.get().addOptional(stemKey);
        saplingsTag.get().addOptional(seedKey);
      }
    }

    addShapeTagsFor(PossibleSaplingShapes.VANILLA);
  }

  protected void addShapeTagsFor(Map<SaplingTypeID, List<TagKey<Block>>> mapping) {
    Map<TagKey<Block>, TagBuilder<Block>> saplingShapeTags = new HashMap<>();

    mapping.forEach((id, tags) -> {
      Identifier blockId = BuiltInRegistries.BLOCK.getKey(SaplingType.of(id).sapling());
      ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, blockId);

      for (TagKey<Block> tag : tags) {
        saplingShapeTags.computeIfAbsent(tag, this::builderForBlockTag).get().addOptional(blockKey);
      }
    });
  }

  protected TagBuilder<Block> builderForBlockTag(TagKey<Block> blockTag) {
    //~ if >= 1.21.11 'getOrCreateTagBuilder(' -> 'builder('
    return new TagBuilder<>(builder(blockTag));
  }

  protected static class TagBuilder<T> {

    private final /*? if < 1.21.11 { *//*FabricTagsProvider*//*? } else { */ TagAppender
        /*? } */</*? if >= 1.21.11 < 26.2 { */ResourceKey<T>, /*? } */T>
        /*? if < 1.21.11 { *//*.FabricTagBuilder*//*? } */ TAG;

    private TagBuilder(/*? if < 1.21.11 { *//*FabricTagsProvider*//*? } else { */ TagAppender
        /*? } */</*? if >= 1.21.11 < 26.2 { */ResourceKey<T>, /*? } */T>
        /*? if < 1.21.11 { *//*.FabricTagBuilder*//*? } */ tag) {
      this.TAG = tag;
    }

    protected /*? if < 1.21.11 { *//*FabricTagsProvider*//*? } else { */ TagAppender
        /*? } */</*? if >= 1.21.11 < 26.2 { */ResourceKey<T>, /*? } */T>
    /*? if < 1.21.11 { *//*.FabricTagBuilder*//*? } */ get() {
      return this.TAG;
    }
  }
}
//? }
