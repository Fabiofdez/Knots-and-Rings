package fabiofdez.knots_and_rings;

//? if <= 1.21.5 {
/*import fabiofdez.knots_and_rings.mixin.BooleanValueAccessor;
import fabiofdez.knots_and_rings.mixin.GameRulesAccessor;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.gamerules.GameRules.BooleanValue;
*///? } else {
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;

//? if fabric {
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
//? } else {
/*import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
*///? }
//? }

public class ModGameRules {

  //? if neoforge && > 1.21.5 {
  /*public static DeferredRegister<GameRule<?>> GAME_RULES = DeferredRegister.create(
      Registries.GAME_RULE,
      KnotsAndRings.MOD_ID
  );
  *///? }

  //? if <= 1.21.5 {
  /*public static final GameRules.Key<BooleanValue> GENERATE_SAPLINGS = GameRulesAccessor.knots_and_rings$registerGameRule(
      "generate_saplings",
      GameRules.Category.MISC,
      BooleanValueAccessor.knots_and_rings$create(true)
  );
  *///? } else {
  public static /*? if fabric { */ GameRule<Boolean>/*? } else { *//*DeferredHolder<GameRule<?>, GameRule<Boolean>>*//*? } */ GENERATE_SAPLINGS = registerBooleanGameRule(
      "generate_saplings",
      true,
      GameRuleCategory.MISC
  );

  @SuppressWarnings("SameParameterValue")
  private static /*? if fabric { */GameRule<Boolean>/*? } else { *//*DeferredHolder<GameRule<?>, GameRule<Boolean>>*//*? } */ registerBooleanGameRule(String path, boolean defaultValue, GameRuleCategory category) {
    GameRule<Boolean> rule = new GameRule<>(
        category,
        GameRuleType.BOOL,
        BoolArgumentType.bool(),
        GameRuleTypeVisitor::visitBoolean,
        Codec.BOOL,
        (bool) -> bool ? 1 : 0,
        defaultValue,
        FeatureFlagSet.of()
    );

    //? if fabric {
    return Registry.register(BuiltInRegistries.GAME_RULE, KnotsAndRings.id(path), rule);
    //? } else {
    /*return GAME_RULES.register(path, () -> rule);
     *///? }
  }
  //? }

  public static void initialize() {
  }

  //? if neoforge && > 1.21.5 {
  /*public static void register(IEventBus eventBus) {
    GAME_RULES.register(eventBus);
  }
  *///? }
}
