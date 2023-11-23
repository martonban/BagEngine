package engine;

import com.google.gson.*;
import components.Component;
import components.Transform;

import java.lang.reflect.Type;

/*
*   This class is responsible for the GAMEOBJECT Deserializing.
*/

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {

    // We will get all GameObject from the Json file and create every game object and components
    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        // Create all components
        GameObject go = new GameObject(name);
        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }
        go.transform = go.getComponent(Transform.class);
        return go;
    }
}