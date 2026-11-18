package fabiofdez.knots_and_rings.compat;

import java.util.ArrayList;
import java.util.List;

public class ModRegistry {

  private static final List<Runnable> BOOTSTRAP_ACTIONS = new ArrayList<>();

  public static void registerForBootstrap(Runnable action) {
    BOOTSTRAP_ACTIONS.add(action);
  }

  public static void bootstrap() {
    BOOTSTRAP_ACTIONS.forEach(Runnable::run);
  }
}
