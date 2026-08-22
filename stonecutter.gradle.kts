@file:OptIn(dev.kikugie.stonecutter.StonecutterExperimentalAPI::class)

plugins {
	alias(libs.plugins.stonecutter)
	alias(libs.plugins.dotenv)
	alias(libs.plugins.fabric.loom).apply(false)
	alias(libs.plugins.fabric.loom.remap).apply(false)
	alias(libs.plugins.neoforged.moddev).apply(false)
	alias(libs.plugins.jsonlang.postprocess).apply(false)
	alias(libs.plugins.mod.publish.plugin).apply(false)
	alias(libs.plugins.kotlin.jvm).apply(false)
	alias(libs.plugins.devtools.ksp).apply(false)
	alias(libs.plugins.fletching.table).apply(false)
	alias(libs.plugins.legacyforge.moddev).apply(false)
}

stonecutter active file(".sc_active_version")

tasks.register("runActiveClient") {
	group = "stonecutter"
	description = "Run client of the active Stonecutter version"
	dependsOn(stonecutter.current!!.project + ":runClient")
}

tasks.register("runActiveServer") {
	group = "stonecutter"
	description = "Run server of the active Stonecutter version"
	dependsOn(stonecutter.current!!.project + ":runServer")
}

stonecutter parameters {
	var loader = current.project.substringAfterLast("-")

	constants.match(loader, "fabric", "neoforge", "forge")
	swaps["mod_version"] = "\"${properties.get<String>("mod.version")}\";"
	swaps["mod_id"] = "\"${properties.get<String>("mod.id")}\";"
	swaps["mod_name"] = "\"${properties.get<String>("mod.name")}\";"
	swaps["mod_group"] = "\"${properties.get<String>("mod.group")}\";"
	swaps["minecraft"] = "\"${current.version}\";"
	constants["release"] = properties.get<String>("mod.id") != "modtemplate"

	replacements {
		string(current.parsed >= "1.21") {
			replace("public VoxelShape getShape", "protected VoxelShape getShape")
			replace("public RenderShape getRenderShape", "protected RenderShape getRenderShape")
			replace("public BlockState updateShape", "protected BlockState updateShape")
			replace("public VoxelShape getCollisionShape", "protected VoxelShape getCollisionShape")
			replace("public boolean isCollisionShapeFullBlock", "protected boolean isCollisionShapeFullBlock")
			replace("public boolean canSurvive", "protected boolean canSurvive")
			replace("public List<ItemStack> getDrops", "protected List<ItemStack> getDrops")
			replace("public InteractionResult use(", "protected ItemInteractionResult useItemOn(")
			replace("public boolean isRandomlyTicking", "protected boolean isRandomlyTicking")
			replace("public void randomTick", "protected void randomTick")
			replace("public void tick", "protected void tick")

			replace("block.RuBlocks", "registry.RUBlocks")
			replace("RuBlocks", "RUBlocks")
			replace("feature.tree.PineTreeFeature", "feature.tree.StrippedPineTreeFeature")
			replace("PineTreeFeature.class", "StrippedPineTreeFeature.class")
			replace("RuTreeConfiguration", "RUTreeConfiguration")
			replace(".trunkProvider", ".trunkProvider()")
			replace("grower.AbstractTreeGrower", "grower.TreeGrower")
			replace("AbstractTreeGrower.class", "TreeGrower.class")
			replace("/grower/AbstractTreeGrower;", "/grower/TreeGrower;")

			replace("new AcaciaTreeGrower()", "TreeGrower.ACACIA")
			replace("new BirchTreeGrower()", "TreeGrower.BIRCH")
			replace("new CherryTreeGrower()", "TreeGrower.CHERRY")
			replace("new DarkOakTreeGrower()", "TreeGrower.DARK_OAK")
			replace("new JungleTreeGrower()", "TreeGrower.JUNGLE")
			replace("new MangroveTreeGrower()", "TreeGrower.MANGROVE")
			replace("new OakTreeGrower()", "TreeGrower.OAK")
			replace("new SpruceTreeGrower()", "TreeGrower.SPRUCE")
		}

		string(current.parsed >= "1.21", "uses_tree_grower") {
			replace("(AbstractTreeGrower ", "(TreeGrower ")
		}

		string(current.parsed > "1.21", "blocks_mixin") {
			replace("block/RotatedPillarBlock;\", at = @At(value = \"NEW\"", "block/Block;\", at = @At(value = \"NEW\"")
		}

		string(current.parsed eq "1.21.1", "has_interaction_result") {
			replace("InteractionResult", "ItemInteractionResult")
			replace("InteractionResult.PASS", "ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION")
		}

		string(current.parsed > "1.21.1") {
			replace("RecipeProvider.has", "provider.has")
			replace("protected ItemInteractionResult useItemOn(", "protected InteractionResult useItemOn(")
			replace("level.block.BushBlock", "level.block.VegetationBlock")
			replace("BushBlock.class", "VegetationBlock.class")
			replace("level/block/BushBlock;", "level/block/VegetationBlock;")
			replace("block.MossBlock", "block.BonemealableFeaturePlacerBlock")
			replace("MossBlock.class", "BonemealableFeaturePlacerBlock.class")

			replace("FoliageColor::getDefaultColor", "() -> FoliageColor.FOLIAGE_DEFAULT")
			replace("FoliageColor::getEvergreenColor", "() -> FoliageColor.FOLIAGE_EVERGREEN")
			replace("FoliageColor::getBirchColor", "() -> FoliageColor.FOLIAGE_BIRCH")
			replace("FoliageColor.getDefaultColor()", "FoliageColor.FOLIAGE_DEFAULT")
			replace("FoliageColor.getEvergreenColor()", "FoliageColor.FOLIAGE_EVERGREEN")
			replace("FoliageColor.getBirchColor()", "FoliageColor.FOLIAGE_BIRCH")

			replace("state.getOffset(null, ", "state.getOffset(") // BlockBehaviour.BlockStateBase.getOffset
		}

		string(current.parsed >= "1.21.4") {
			replace("${property("mod.group")}.${property("mod.id")}.util.ARGB", "net.minecraft.util.ARGB")
		}

		string(current.parsed >= "1.21.9") {
			replace("FMLEnvironment.dist", "FMLEnvironment.getDist()")
		}

		string(current.parsed >= "1.21.11") {
			replace("net.minecraft.Util", "net.minecraft.util.Util")
			replace("world.level.GameRules", "world.level.gamerules.GameRules")
			replace("blockrenderlayer.v1.BlockRenderLayerMap", "client.rendering.v1.BlockRenderLayerMap")
			replace("BlockRenderLayerMap.INSTANCE.putBlock", "BlockRenderLayerMap.putBlock")
			replace("renderer.RenderType", "renderer.rendertype.RenderType")
			replace("ARGB.lerp", "ARGB.srgbLerp")
			replace("ResourceLocation", "Identifier")
			replace("LoadingModList.get()", "FMLLoader.getCurrent().getLoadingModList()")
			replace(".noCollission()", ".noCollision()")
			replace(".getGameRules().getRule", ".getGameRules().get")
			replace("GameRules.RULE_MOBGRIEFING", "GameRules.MOB_GRIEFING")
		}

		string(current.parsed >= "26.1") {
			replace("renderer.state.CameraRenderState", "renderer.state.level.CameraRenderState")
			replace("rendering.v1.ColorProviderRegistry", "rendering.v1.BlockColorRegistry")
			replace("itemgroup.v1.ItemGroupEvents", "creativetab.v1.CreativeModeTabEvents")
			replace("ColorProviderRegistry.BLOCK.register", "BlockColorRegistry.register")
			replace("ColorProviderRegistry.BLOCK::register", "BlockColorRegistry::register")
			replace("ItemGroupEvents.ModifyEntries", "CreativeModeTabEvents.ModifyOutput")
			replace("ItemGroupEvents.modifyEntriesEvent", "CreativeModeTabEvents.modifyOutputEvent")
			replace("SoundTypeBuilder.EntryBuilder", "SoundTypeBuilder.RegistrationBuilder")
			replace("SoundTypeBuilder.of().category", "SoundTypeBuilder.of().source")
			replace("FabricBlockLootTableProvider", "FabricBlockLootSubProvider")
			replace("FabricTagProvider.BlockTagProvider", "FabricTagsProvider.BlockTagsProvider")
			replace("FabricTagProvider", "FabricTagsProvider")
			replace("FabricDataOutput", "FabricPackOutput")
			replace("entityCutoutNoCull", "entityCutout")
			replace("level.getDayTime()", "level.getDefaultClockTime()")
			replace("PayloadTypeRegistry.playC2S", "PayloadTypeRegistry.serverboundPlay")
			replace("ServerWorldEvents", "ServerLevelEvents")
			replace("RegisterColorHandlersEvent.Block", "RegisterColorHandlersEvent.BlockTintSources")
			replace("BlockElementFace", "CuboidFace")

			replace("block.FarmBlock", "block.FarmlandBlock")
			replace("block/FarmBlock;", "block/FarmlandBlock;")
			replace("FarmBlock.class", "FarmlandBlock.class")
			replace("block.SnowyDirtBlock", "block.SnowyBlock")
			replace("block/SnowyDirtBlock;", "block/SnowyBlock;")
			replace("SnowyDirtBlock.class", "SnowyBlock.class")
			replace("block.SpreadingSnowyDirtBlock", "block.SpreadingSnowyBlock")
			replace("block/SpreadingSnowyDirtBlock;", "block/SpreadingSnowyBlock;")
		}

		string(current.parsed >= "26.1", "place_log") {
			replace("level/LevelSimulatedReader;", "level/WorldGenLevel;")
		}

		string(current.parsed >= "26.1", "get_drops") {
			replace("world/item/ItemStack;", "world/item/ItemInstance;")
		}

		string(loader == "neoforge") {
			replace("BlockSupplier", "DeferredBlock<Block>")
			replace("ItemSupplier", "DeferredItem<Item>")
		}

		string(loader == "forge") {
			replace("BlockSupplier", "RegistryObject<Block>")
			replace("ItemSupplier", "RegistryObject<Item>")
		}

		string(loader == "forge" || current.parsed > "1.21") {
			replace("io.github.uhq_games.regions_unexplored", "net.regions_unexplored")
			replace("Lio/github/uhq_games/regions_unexplored", "Lnet/regions_unexplored")
		}
	}
}

for (version in stonecutter.versions.map { it.version }.distinct()) tasks.register("publish$version") {
	group = "publishing"
	dependsOn(stonecutter.tasks.named("publishMods") { metadata.version == version })
}
