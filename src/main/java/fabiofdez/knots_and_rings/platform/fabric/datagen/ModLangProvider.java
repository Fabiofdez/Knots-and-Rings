package fabiofdez.knots_and_rings.platform.fabric.datagen;

//? fabric {

import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.ModSounds;
import fabiofdez.knots_and_rings.feature.SaplingTypeID;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.Util;
//? > 1.21
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

//? > 1.21
import java.util.concurrent.CompletableFuture;

import static fabiofdez.knots_and_rings.feature.SaplingTypeID.*;

public class ModLangProvider extends FabricLanguageProvider {
  public ModLangProvider(FabricDataOutput dataOutput/*? if > 1.21 { */, CompletableFuture<HolderLookup.Provider> registryLookup/*? } */) {
    super(dataOutput/*? if > 1.21 { */, registryLookup/*? } */);
  }

  @Override
  public void generateTranslations(/*? if > 1.21 { */HolderLookup.Provider provider, /*? } */TranslationBuilder builder) {
    builder.add(subtitleFor(ModSounds.SPLIT_WOOD.get()), "Wood splitting");
    builder.add(subtitleFor(ModSounds.CRACK_WOOD.get()), "Wood cracking");
    builder.add(subtitleFor(ModSounds.HEAL_WOOD.get()), "Wood healing");
    builder.add(subtitleFor(ModSounds.HEAL_WOOD_ALT.get()), "Wood healing");

    addTranslationForSeed(builder, ACACIA, "Acacia Seed Pod");
    addTranslationForSeed(builder, BIRCH, "Birch Seeds");
    addTranslationForSeed(builder, CHERRY, "Cherry Seed");
    addTranslationForSeed(builder, DARK_OAK, "Dark Oak Acorn");
    addTranslationForSeed(builder, JUNGLE, "Jungle Seed");
    addTranslationForSeed(builder, MANGROVE, "Mangrove Propagule");
    addTranslationForSeed(builder, OAK, "Oak Acorn");
    addTranslationForSeed(builder, PALE_OAK, "Pale Oak Acorn");
    addTranslationForSeed(builder, SPRUCE, "Spruce Seed Cone");
  }

  private String subtitleFor(SoundEvent sound) {
    //? > 1.21.1
    ResourceLocation id = sound.location();
    //? <= 1.21.1
    //ResourceLocation id = sound.getLocation();

    return Util.makeDescriptionId("subtitles", id);
  }

  private void addTranslationForSeed(TranslationBuilder builder, SaplingTypeID id, String value) {
    ResourceLocation seedId = KnotsAndRings.id(id.seedId());
    //~ if < 1.21.5 'item' -> 'block'
    builder.add(seedId.toLanguageKey("item"), value);
  }
}

//? }
