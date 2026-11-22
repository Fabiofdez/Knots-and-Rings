package fabiofdez.knots_and_rings.platform.fabric.datagen;

//? if fabric {

import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.platform.fabric.datagen.compat.ArboriaSaplingsTagProvider;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.resources.Identifier;

//? if < 1.21
//import fabiofdez.knots_and_rings.feature.SaplingType;

//? if <= 1.21.1 {
/*import fabiofdez.knots_and_rings.platform.fabric.datagen.compat.VanillaBackportBlockLootTableProvider;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
*///? }

public class FabricDataGeneratorEntrypoint implements DataGeneratorEntrypoint {

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    //? if < 1.21
    //SaplingType.freezeTypes();

    FabricDataGenerator.Pack basePack = generator.createPack();
    //? if > 1.21.1
    basePack.addProvider(ModSoundsProvider::new);
    basePack.addProvider(ModLangProvider::new);
    basePack.addProvider(ModTagProvider::new);
    basePack.addProvider(ModBlockLootTableProvider::new);

    //? if <= 1.21.1 {
    /*Identifier vanillaBackportPackId = KnotsAndRings.id(ResourcePacks.PACK_VANILLA_BACKPORT_COMPAT.id());
    FabricDataGenerator.Pack vanillaBackportPack = generator.createBuiltinResourcePack(vanillaBackportPackId);
    vanillaBackportPack.addProvider(VanillaBackportBlockLootTableProvider::new);
    *///? }

    Identifier arboriaSaplingsPackId = KnotsAndRings.id(ResourcePacks.PACK_ARBORIA_SAPLINGS_COMPAT.id());
    FabricDataGenerator.Pack arboriaSaplingsPack = generator.createBuiltinResourcePack(arboriaSaplingsPackId);
    arboriaSaplingsPack.addProvider(ArboriaSaplingsTagProvider::new);
  }
}
//? }
