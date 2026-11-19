package fabiofdez.knots_and_rings.platform.fabric.datagen;

//? if fabric {

//? if < 1.21
//import fabiofdez.knots_and_rings.feature.SaplingType;
import fabiofdez.knots_and_rings.KnotsAndRings;
import fabiofdez.knots_and_rings.resource.ResourcePacks;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.resources.ResourceLocation;

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

    ResourceLocation saplingsPackId = KnotsAndRings.id(ResourcePacks.PACK_SAPLINGS.id());
    FabricDataGenerator.Pack vanillaSaplings = generator.createBuiltinResourcePack(saplingsPackId);
    // TODO: create datapack(s) for sapling shape tags

  }
}
//? }
