package fabiofdez.knots_and_rings.platform.fabric.datagen;

//? if fabric && > 1.21.1 {

import fabiofdez.knots_and_rings.ModSounds;
import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

//? if < 1.21.11
//import net.minecraft.sounds.SoundSource;

public class ModSoundsProvider extends FabricSoundsProvider {
  public ModSoundsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  protected void configure(HolderLookup.Provider provider, SoundExporter soundExporter) {
    soundExporter.add(
        ModSounds.SPLIT_WOOD.get(),
        blockSound(ModSounds.SPLIT_WOOD.get()).sound(ofEvent(SoundEvents.AXE_STRIP).volume(0.8F))
    );
    soundExporter.add(
        ModSounds.CRACK_WOOD.get(),
        blockSound(ModSounds.CRACK_WOOD.get()).sound(ofEvent(SoundEvents.BAMBOO_HIT).volume(0.4F))
    );

    soundExporter.add(
        ModSounds.HEAL_WOOD.get(),
        blockSound(ModSounds.HEAL_WOOD.get()).sound(ofEvent(SoundEvents.MOSS_PLACE).volume(0.8F))
    );
    soundExporter.add(
        ModSounds.HEAL_WOOD_ALT.get(),
        blockSound(ModSounds.HEAL_WOOD_ALT.get()).sound(ofEvent(SoundEvents.COMPOSTER_FILL_SUCCESS).volume(0.4F))
    );
  }

  private static SoundTypeBuilder blockSound(SoundEvent sound) {
    String subtitleKey = sound.location().toLanguageKey("subtitles");
    return SoundTypeBuilder.of()/*? if < 1.21.11 { *//*.source(SoundSource.BLOCKS)*//*? } */.subtitle(subtitleKey);
  }

  private static SoundTypeBuilder.RegistrationBuilder ofEvent(SoundEvent sound) {
    return SoundTypeBuilder.RegistrationBuilder.ofEvent(sound);
  }

  @Override
  public @NotNull String getName() {
    return "ModSoundsProvider";
  }
}

//? }
