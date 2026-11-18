package fabiofdez.knots_and_rings.mixin;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.class)
public interface GameRulesAccessor {

  //? if <= 1.21.5 {
  @Invoker("register")
  static <T extends GameRules.Value<T>> GameRules.Key<T> knots_and_rings$registerGameRule(String string, GameRules.Category category, GameRules.Type<T> type) {
    throw new AssertionError();
  }
  //?}
}
