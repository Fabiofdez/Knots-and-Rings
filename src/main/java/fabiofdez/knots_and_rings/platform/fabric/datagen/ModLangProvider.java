package fabiofdez.knots_and_rings.platform.fabric.datagen;

//? fabric {

import fabiofdez.knots_and_rings.ModSounds;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.Util;
//? > 1.21
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

//? > 1.21
import java.util.concurrent.CompletableFuture;

public class ModLangProvider extends FabricLanguageProvider {
  public ModLangProvider(FabricDataOutput dataOutput/*? if > 1.21 >> ') {' */, CompletableFuture<HolderLookup.Provider> registryLookup) {
    super(dataOutput/*? if > 1.21 >> ');' */, registryLookup);
  }

  @Override
  public void generateTranslations(/*? if > 1.21 >> 'TranslationBuilder' */HolderLookup.Provider provider, TranslationBuilder builder) {
    builder.add(subtitleFor(ModSounds.SPLIT_WOOD.get()), "Wood splitting");
    builder.add(subtitleFor(ModSounds.CRACK_WOOD.get()), "Wood cracking");
    builder.add(subtitleFor(ModSounds.HEAL_WOOD.get()), "Wood healing");
    builder.add(subtitleFor(ModSounds.HEAL_WOOD_ALT.get()), "Wood healing");
  }

  private String subtitleFor(SoundEvent sound) {
    //? > 1.21.1
    ResourceLocation id = sound.location();
    //? <= 1.21.1
    //ResourceLocation id = sound.getLocation();

    return Util.makeDescriptionId("subtitles", id);
  }
}

//? }
