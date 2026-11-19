package fabiofdez.knots_and_rings.mixin;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

//~ if > 1.21.5 'GameRules.BooleanValue' -> 'GameRules'
@Mixin(GameRules.BooleanValue.class)
public interface BooleanValueAccessor {

  //? if <= 1.21.5 {
  @Invoker("create")
  static GameRules.Type<GameRules.BooleanValue> knots_and_rings$create(boolean value) {
    throw new AssertionError();
  }
  //? }
}
