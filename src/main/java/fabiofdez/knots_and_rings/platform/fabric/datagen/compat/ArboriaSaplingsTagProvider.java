package fabiofdez.knots_and_rings.platform.fabric.datagen.compat;

//? if fabric {

import fabiofdez.knots_and_rings.feature.PossibleSaplingShapes;
import fabiofdez.knots_and_rings.platform.fabric.datagen.ModTagProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ArboriaSaplingsTagProvider extends ModTagProvider {

  public ArboriaSaplingsTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  protected void addTags(HolderLookup.Provider wrapperLookup) {
    addShapeTagsFor(PossibleSaplingShapes.ARBORIA_SAPLINGS);
  }
}
//? }
