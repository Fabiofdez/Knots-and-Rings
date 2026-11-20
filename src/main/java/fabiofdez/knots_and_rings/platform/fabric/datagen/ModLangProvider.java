package fabiofdez.knots_and_rings.platform.fabric.datagen;

//? if fabric {

import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.ModGameRules;
import fabiofdez.knots_and_rings.ModSounds;
import fabiofdez.knots_and_rings.feature.SaplingTypeID;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.util.Util;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

//? if > 1.21 {
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;
//? }

import static fabiofdez.knots_and_rings.feature.SaplingTypeID.*;

public class ModLangProvider extends FabricLanguageProvider {
  public ModLangProvider(FabricPackOutput dataOutput/*? if > 1.21 { */, CompletableFuture<HolderLookup.Provider> registryLookup/*? } */) {
    super(dataOutput/*? if > 1.21 { */, registryLookup/*? } */);
  }

  @Override
  public void generateTranslations(/*? if > 1.21 { */HolderLookup.Provider provider, /*? } */TranslationBuilder builder) {
    builder.add(subtitleFor(ModSounds.SPLIT_WOOD.get()), "Wood splitting");
    builder.add(subtitleFor(ModSounds.CRACK_WOOD.get()), "Wood cracking");
    builder.add(subtitleFor(ModSounds.HEAL_WOOD.get()), "Wood healing");
    builder.add(subtitleFor(ModSounds.HEAL_WOOD_ALT.get()), "Wood healing");

    builder.add(ModGameRules.GENERATE_SAPLINGS.getDescriptionId(), "Generate saplings near trees");
    builder.add(
        ModGameRules.GENERATE_SAPLINGS.getDescriptionId().concat(".description"),
        "Saplings have a chance to appear near trees during world generation"
    );

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
    //~ if > 1.21.1 '.getLocation()' -> '.location()'
    return Util.makeDescriptionId("subtitles", sound.location());
  }

  private void addTranslationForSeed(TranslationBuilder builder, SaplingTypeID id, String value) {
    Identifier seedId = KnotsAndRings.id(id.seedId());
    //~ if < 1.21.5 'item' -> 'block'
    builder.add(seedId.toLanguageKey("item"), value);
  }
}

//? }
