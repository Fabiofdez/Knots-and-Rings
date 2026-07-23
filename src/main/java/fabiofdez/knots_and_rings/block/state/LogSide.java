package fabiofdez.knots_and_rings.block.state;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LogSide {
  public enum Mapping implements StringRepresentable {
    M_0000("0000"),
    M_0001("0001"),
    M_0010("0010"),
    M_0011("0011"),
    M_00RL("00rl"),
    M_0100("0100"),
    M_0101("0101"),
    M_0110("0110"),
    M_0111("0111"),
    M_01RL("01rl"),
    M_0RL0("0rl0"),
    M_0RL1("0rl1"),
    M_0R2L("0r2l"),
    M_1000("1000"),
    M_1001("1001"),
    M_1010("1010"),
    M_1011("1011"),
    M_10RL("10rl"),
    M_1100("1100"),
    M_1101("1101"),
    M_1110("1110"),
    M_1111("1111"),
    M_11RL("11rl"),
    M_1RL0("1rl0"),
    M_1RL1("1rl1"),
    M_1R2L("1r2l"),
    M_L00R("l00r"),
    M_L01R("l01r"),
    M_L0R2("l0r2"),
    M_L10R("l10r"),
    M_L11R("l11r"),
    M_L1R2("l1r2"),
    M_LRLR("lrlr"),
    M_LR22("lr22"),
    M_2L0R("2l0r"),
    M_2L1R("2l1r"),
    M_2LR2("2lr2"),
    M_22LR("22lr"),
    M_2222("2222"),
    M_RL00("rl00"),
    M_RL01("rl01"),
    M_RL10("rl10"),
    M_RL11("rl11"),
    M_RLRL("rlrl"),
    M_R2L0("r2l0"),
    M_R2L1("r2l1"),
    M_R22L("r22l");

    private static final Map<String, LogSide.Mapping> FROM_STRING = ImmutableMap.copyOf(Arrays
        .stream(values())
        .collect(Collectors.toMap((sides) -> sides.name, Function.identity())));

    private final String name;

    Mapping(String value) {
      this.name = value;
    }

    public static LogSide.Mapping parse(String str) {
      return FROM_STRING.getOrDefault(str, M_0000);
    }

    @Override
    public String toString() {
      return this.name;
    }

    @NotNull
    @Override
    public String getSerializedName() {
      return toString();
    }
  }
}
