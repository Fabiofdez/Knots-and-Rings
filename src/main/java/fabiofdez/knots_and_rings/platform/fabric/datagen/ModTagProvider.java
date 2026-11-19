package fabiofdez.knots_and_rings.platform.fabric.datagen;

//? fabric {

import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.feature.SaplingType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
//? >= 1.21.11
//import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
//? < 26.2
import net.minecraft.tags.BlockTags;
//? >= 26.2
//import net.minecraft.tags.BlockItemTags;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModTagProvider extends FabricTagProvider.BlockTagProvider {

  public ModTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  protected void addTags(HolderLookup.Provider wrapperLookup) {
    //? if < 1.21.11 {
    FabricTagProvider<Block>.FabricTagBuilder saplingsTag = getOrCreateTagBuilder(BlockTags.SAPLINGS);
    //? } else if < 26.2 {
    /*TagAppender<ResourceKey<Block>, Block> saplingsTag = builder(BlockTags.SAPLINGS);
     *///? } else {
    /*TagAppender<Block> saplingsTag = builder(BlockTags.SAPLINGS);
     *///? }

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
  }
}
//? }
