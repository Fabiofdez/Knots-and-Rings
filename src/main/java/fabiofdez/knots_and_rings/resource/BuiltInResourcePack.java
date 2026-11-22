package fabiofdez.knots_and_rings.resource;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

public class BuiltInResourcePack {
  private final String ID;
  private final Component NAME;
  private final boolean DEFAULT_ENABLED;
  private final PackType PACK_TYPE;

  private BuiltInResourcePack(String id, Component name, boolean defaultEnabled, PackType packType) {
    this.ID = id;
    this.NAME = name;
    this.DEFAULT_ENABLED = defaultEnabled;
    this.PACK_TYPE = packType;
  }

  public static class ClientPack {

    public static BuiltInResourcePack create(String id, Component name, boolean defaultEnabled) {
      return new BuiltInResourcePack(id, name, defaultEnabled, PackType.CLIENT_RESOURCES);
    }

    public static BuiltInResourcePack create(String id, String name, boolean defaultEnabled) {
      return create(id, Component.literal(name), defaultEnabled);
    }

    public static BuiltInResourcePack create(String id, Component name) {
      return create(id, name, false);
    }

    public static BuiltInResourcePack create(String id, String name) {
      return create(id, Component.literal(name));
    }
  }

  public static class ServerPack {

    public static BuiltInResourcePack create(String id, Component name, boolean defaultEnabled) {
      return new BuiltInResourcePack(id, name, defaultEnabled, PackType.SERVER_DATA);
    }

    public static BuiltInResourcePack create(String id, String name, boolean defaultEnabled) {
      return create(id, Component.literal(name), defaultEnabled);
    }

    public static BuiltInResourcePack create(String id, Component name) {
      return create(id, name, false);
    }

    public static BuiltInResourcePack create(String id, String name) {
      return create(id, Component.literal(name));
    }
  }

  public String id() {
    return this.ID;
  }

  public Component name() {
    return this.NAME;
  }

  public boolean defaultEnabled() {
    return this.DEFAULT_ENABLED;
  }

  public PackType packType() {
    return this.PACK_TYPE;
  }
}
