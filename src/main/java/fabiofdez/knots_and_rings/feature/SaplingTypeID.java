package fabiofdez.knots_and_rings.feature;

public enum SaplingTypeID {
  NONE,

  ACACIA,
  BIRCH,
  CHERRY,
  DARK_OAK,
  JUNGLE,
  MANGROVE,
  OAK,
  PALE_OAK,
  SPRUCE;

  private String withSuffix(String suffix) {
    return this.toString().concat(suffix);
  }

  public String saplingId() {
    if (this == MANGROVE) return this.withSuffix("_propagule");
    return this.withSuffix("_sapling");
  }

  public String stemId() {
    return this.saplingId().concat("_stem");
  }

  public String seedId() {
    return switch (this) {
      case ACACIA, BIRCH, SPRUCE -> this.withSuffix("_seeds");
      default -> this.withSuffix("_seed");
    };
  }

  public void registerPlacedState(SaplingType.PlacedStatePredicate predicate) {
    SaplingType.registerPlacedState(this, predicate);
  }

  @Override
  public String toString() {
    return this.name().toLowerCase();
  }
}
