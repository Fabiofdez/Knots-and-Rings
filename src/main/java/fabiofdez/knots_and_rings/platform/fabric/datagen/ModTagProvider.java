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
//? if >= 1.21.11
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags./*? if < 26.2 { */BlockTags/*? } else { *//*BlockItemTags*//*? } */;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModTagProvider extends FabricTagsProvider.BlockTagsProvider {

  public ModTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  protected void addTags(HolderLookup.Provider wrapperLookup) {
    var saplingsTag = builderForBlockTag(BlockTags.SAPLINGS);
    saplingsTag.setReplace(false);

    Set<SaplingType> vanillaSet = Stream
        .of(SaplingType.VANILLA_INVENTORY_ORDER)
        .map(SaplingType::of)
        .collect(Collectors.toUnmodifiableSet());

    for (SaplingType type : SaplingType.all()) {
      ResourceKey<Block> blockKey = KnotsAndRings.blockKey(type.id().seedId());

      if (vanillaSet.contains(type)) {
        saplingsTag.add(blockKey);
      } else {
        saplingsTag.addOptional(blockKey);
      }
    }

    addShapeTagsFor(PossibleSaplingShapes.VANILLA);
  }

  protected void addShapeTagsFor(Map<SaplingTypeID, List<TagKey<Block>>> mapping) {
    Map<TagKey<Block>, /*? if < 1.21.11 { *//*FabricTagsProvider*/
        /*? } else { */TagAppender/*? } */</*? if >= 1.21.11 < 26.2 { */ResourceKey<Block>, /*? } */Block>
        /*? if < 1.21.11 { *//*.FabricTagBuilder*/
        /*? } */> saplingShapeTags = new HashMap<>();

    mapping.forEach((id, tags) -> {
      Identifier blockId = BuiltInRegistries.BLOCK.getKey(SaplingType.of(id).sapling());
      ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, blockId);

      for (TagKey<Block> tag : tags) {
        saplingShapeTags.computeIfAbsent(tag, this::builderForBlockTag).addOptional(blockKey);
      }
    });
  }

  protected /*? if < 1.21.11 { *//*FabricTagsProvider*/
    /*? } else { */TagAppender/*? } */</*? if >= 1.21.11 < 26.2 { */ResourceKey<Block>, /*? } */Block>
  /*? if < 1.21.11 { *//*.FabricTagBuilder*/
  /*? } */ builderForBlockTag(TagKey<Block> blockTag) {

    /*? if < 1.21.11 { */
    /*return getOrCreateTagBuilder(blockTag);
     *//*? } else { */
    return builder(blockTag);
    /*? } */
  }
}
//? }
