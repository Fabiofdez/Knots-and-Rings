package fabiofdez.knots_and_rings.platform.fabric.datagen.compat;

//? if fabric && <= 1.21.1 {

/*import fabiofdez.knots_and_rings.feature.SaplingType;
import fabiofdez.knots_and_rings.platform.fabric.datagen.ModBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import static fabiofdez.knots_and_rings.feature.SaplingTypeID.PALE_OAK;

//? if >= 1.21 {
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
//? }

public class VanillaBackportBlockLootTableProvider extends ModBlockLootTableProvider {

  public VanillaBackportBlockLootTableProvider(FabricPackOutput dataOutput/^? if >= 1.21 { ^/, CompletableFuture<HolderLookup.Provider> registryLookup/^? } ^/) {
    super(dataOutput/^? if >= 1.21 { ^/, registryLookup/^? } ^/);
  }

  @Override
  public void generate() {
    SaplingType paleOak = SaplingType.of(PALE_OAK);

    add(paleOak.sapling(), createSaplingDrops(paleOak));
    add(paleOak.stem(), createSaplingStemDrops(paleOak));
  }
}
*///? }
