package fabiofdez.knots_and_rings.client.model;

//? !fabric && <= 1.21.1 {

/*import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import fabiofdez.knots_and_rings.KnotsAndRings;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
//? forge
//import net.minecraftforge.client.model.geometry.IGeometryLoader;
//? neoforge
//import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;import java.util.Map;

public final class TexturedCompositeLoader implements IGeometryLoader<TexturedCompositeModel> {

  public static final ResourceLocation ID = KnotsAndRings.id("textured_composite");
  public static final TexturedCompositeLoader INSTANCE = new TexturedCompositeLoader();

  private TexturedCompositeLoader() {
  }

  @NotNull
  @Override
  public TexturedCompositeModel read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException {
    JsonObject childrenJson = json.getAsJsonObject("children");

    if (childrenJson == null) {
      throw new JsonParseException("A model using the \"%s\" loader must have a \"children\" object.".formatted(ID));
    }

    Map<String, BlockModel> children = new HashMap<>();
    childrenJson.asMap().forEach((key, element) -> {
      BlockModel childModel = ctx.deserialize(element, BlockModel.class);
      children.put(key, childModel);
    });

    return new TexturedCompositeModel(ImmutableMap.copyOf(children));
  }
}
*///? }
