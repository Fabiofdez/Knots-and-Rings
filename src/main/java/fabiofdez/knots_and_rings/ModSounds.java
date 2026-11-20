package fabiofdez.knots_and_rings;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

//? if fabric
import net.minecraft.core.Registry;
//? if !forge
import net.minecraft.core.registries.BuiltInRegistries;
//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
*///? }
//? if forge {
/*import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
*///? }

public class ModSounds {
  //? if !fabric {
  /*public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(
      //? if neoforge
      //BuiltInRegistries.SOUND_EVENT,
      //? if forge
      //ForgeRegistries.SOUND_EVENTS,
      KnotsAndRings.MOD_ID
  );
  *///? }

  public static final Supplier<SoundEvent> SPLIT_WOOD = register("split_wood");
  public static final Supplier<SoundEvent> CRACK_WOOD = register("crack_wood");
  public static final Supplier<SoundEvent> HEAL_WOOD = register("heal_wood");
  public static final Supplier<SoundEvent> HEAL_WOOD_ALT = register("heal_wood_alt");

  //? if fabric {
  private static Supplier<SoundEvent> register(String name) {
    Identifier soundId = KnotsAndRings.id(name);
    SoundEvent toRegister = SoundEvent.createVariableRangeEvent(soundId);
    SoundEvent registered = Registry.register(BuiltInRegistries.SOUND_EVENT, soundId, toRegister);

    return () -> registered;
  }
  //? } else {
  /*private static Supplier<SoundEvent> register(String name) {
    Identifier soundId = KnotsAndRings.id(name);
    return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(soundId));
  }

  public static void register(IEventBus eventBus) {
    SOUND_EVENTS.register(eventBus);
  }
  *///? }

  public static void initialize() {
  }
}
