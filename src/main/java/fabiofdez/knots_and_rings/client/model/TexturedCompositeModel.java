package fabiofdez.knots_and_rings.client.model;

//? !fabric && <= 1.21.1 {

/*import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

//? forge {
/^import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.geometry.BlockGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
^///? }
//? neoforge {
/^import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.geometry.BlockGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
^///? }

public final class TexturedCompositeModel implements IUnbakedGeometry<TexturedCompositeModel> {

  private final ImmutableMap<String, BlockModel> children;

  public TexturedCompositeModel(ImmutableMap<String, BlockModel> children) {
    this.children = children;
  }

  @Override
  public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
    children.values().forEach((child) -> child.resolveParents(modelGetter));
  }

  @NotNull
  @Override
  public Set<String> getConfigurableComponentNames() {
    return children.keySet();
  }

  @NotNull
  @Override
  public BakedModel bake(IGeometryBakingContext ctx, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides/^? if forge { ^//^, ResourceLocation modelId^//^? } ^/) {
    if (!(ctx instanceof BlockGeometryBakingContext blockContext)) {
      throw new IllegalStateException("\"%s\" can only be used with block models".formatted(TexturedCompositeLoader.ID));
    }

    CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(
        ctx,
        spriteGetter.apply(ctx.getMaterial("particle")),
        overrides,
        ctx.getTransforms()
    );

    children.forEach((name, child) -> {
      if (!ctx.isComponentVisible(name, true)) return;

      builder.addLayer(bakeChild(
          child,
          baker,
          blockContext.owner,
          spriteGetter,
          modelState/^? if forge { ^//^, modelId^//^? } ^/
      ));
    });

    return builder.build();
  }

  private BakedModel bakeChild(BlockModel child, ModelBaker baker, BlockModel owner, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState/^? if forge {^//^, ResourceLocation modelId^//^? }^/) {
    Map<String, Either<Material, String>> childTextures = child.textureMap;

    Map<String, Either<Material, String>> oldChildTextures = new HashMap<>();
    owner.textureMap.forEach((key, material) -> {
      oldChildTextures.put(key, childTextures.get(key));
      childTextures.put(key, material);
    });

    BakedModel model = UnbakedGeometryHelper.bake(
        child, baker, owner, spriteGetter, modelState,
        /^? if forge { ^//^modelId,^//^? } ^/
        child.customData.isGui3d()
    );

    oldChildTextures.forEach((key, oldValue) -> {
      if (oldValue == null) childTextures.remove(key);
      else childTextures.put(key, oldValue);
    });

    return model;
  }
}
*///? }
