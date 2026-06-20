package fabiofdez.knots_and_rings;

//? fabric
import net.minecraft.core.Registry;
//? !forge
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

//? neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
*///? }
//? forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
*///? }

import java.util.function.Supplier;

public class ModSounds {
  //? !fabric {
  /*public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(
      //? neoforge
      //BuiltInRegistries.SOUND_EVENT,
      //? forge
      //ForgeRegistries.SOUND_EVENTS,
      KnotsAndRings.MOD_ID
  );
  *///? }

  public static Supplier<SoundEvent> SPLIT_WOOD = register("split_wood");
  public static Supplier<SoundEvent> CRACK_WOOD = register("crack_wood");
  public static Supplier<SoundEvent> HEAL_WOOD = register("heal_wood");
  public static Supplier<SoundEvent> HEAL_WOOD_ALT = register("heal_wood_alt");

  //? if fabric {
  private static Supplier<SoundEvent> register(String name) {
    ResourceLocation soundId = KnotsAndRings.id(name);
    SoundEvent toRegister = SoundEvent.createVariableRangeEvent(soundId);
    SoundEvent registered = Registry.register(BuiltInRegistries.SOUND_EVENT, soundId, toRegister);

    return () -> registered;
  }

  public static void initialize() {
  }
  //? } else {
  /*private static Supplier<SoundEvent> register(String name) {
    ResourceLocation soundId = KnotsAndRings.id(name);
    return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(soundId));
  }

  public static void register(IEventBus eventBus) {
    SOUND_EVENTS.register(eventBus);
  }
  *///? }
}
