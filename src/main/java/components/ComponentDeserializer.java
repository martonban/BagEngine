package components;

import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;

/*
*   This class is responsible for the COMPONENT Serializing and Deserializing. We're using GSON for reflections and write the data to a JSON file.
*   We implement JsonSerializer and JsonDeserializer Interfaces from GSON.
*/

public class ComponentDeserializer implements JsonSerializer<Component>,
        JsonDeserializer<Component> {

    // This is not going to create the components, this is just pass the data for the GameObjectDeserializer
    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }

    // We will upload the component to the Json file.
    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("properties", context.serialize(src, src.getClass()));
        return result;
    }
}