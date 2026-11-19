package fabiofdez.knots_and_rings.feature;

public class SaplingTypeID {

  public static final SaplingTypeID ACACIA = Builder.create("acacia").seeds().build();
  public static final SaplingTypeID BIRCH = Builder.create("birch").seeds().build();
  public static final SaplingTypeID CHERRY = Builder.createDefault("cherry");
  public static final SaplingTypeID DARK_OAK = Builder.createDefault("dark_oak");
  public static final SaplingTypeID JUNGLE = Builder.createDefault("jungle");
  public static final SaplingTypeID MANGROVE = Builder.create("mangrove").asSapling("_propagule").build();
  public static final SaplingTypeID OAK = Builder.createDefault("oak");
  public static final SaplingTypeID PALE_OAK = Builder.createDefault("pale_oak");
  public static final SaplingTypeID SPRUCE = Builder.create("spruce").seeds().build();

  private final String TYPE_NAME;
  private final String SAPLING_PATH;
  private final String STEM_PATH;
  private final String SEED_PATH;

  private SaplingTypeID(String name, String sapling, String stem, String seed) {
    this.TYPE_NAME = name;
    this.SAPLING_PATH = sapling;
    this.STEM_PATH = stem;
    this.SEED_PATH = seed;
  }

  public String saplingId() {
    return SAPLING_PATH;
  }

  public String stemId() {
    return STEM_PATH;
  }

  public String seedId() {
    return SEED_PATH;
  }

  public void registerPlacedState(SaplingType.PlacedStatePredicate predicate) {
    SaplingType.registerPlacedState(this, predicate);
  }

  @Override
  public String toString() {
    return this.TYPE_NAME;
  }

  private static class Builder {

    private final String TYPE_NAME;
    private String SAPLING_SUFFIX = "_sapling";
    private String SEED_SUFFIX = "_seed";

    private Builder(String name) {
      this.TYPE_NAME = name;
    }

    private static SaplingTypeID createDefault(String name) {
      return new Builder(name).build();
    }

    private static Builder create(String name) {
      return new Builder(name);
    }

    @SuppressWarnings("SameParameterValue")
    private Builder asSapling(String suffix) {
      this.SAPLING_SUFFIX = suffix;
      return this;
    }

    @SuppressWarnings("SameParameterValue")
    private Builder asSeed(String suffix) {
      this.SEED_SUFFIX = suffix;
      return this;
    }

    private Builder seeds() {
      return asSeed("_seeds");
    }

    private SaplingTypeID build() {
      String sapling = TYPE_NAME.concat(SAPLING_SUFFIX);
      String stem = sapling.concat("_stem");
      String seeds = TYPE_NAME.concat(SEED_SUFFIX);

      return new SaplingTypeID(TYPE_NAME, sapling, stem, seeds);
    }
  }
}
