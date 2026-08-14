package fabiofdez.knots_and_rings.platform.fabric.datagen;

//? fabric {
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class FabricDataGeneratorEntrypoint implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();

    //? > 1.21.1
    pack.addProvider(ModSoundEventProvider::new);
    pack.addProvider(ModLangProvider::new);
	}
}
//?}
