package engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import components.SpriteRenderer;
import components.Transform;
import imgui.ImGui;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

/*
* Everything is a game object in a game.
* We have default data:  1) ID Counter: self-explanatory
*                        2) uid: The id's of the GameObject. (mainly for the serializer)
*                        3) name: GameObject name. Mainly for the Editor
*                        4) transform: Where is the game objet(and the size of it)
*                        5) zIndex: It's a 2D Game Engine, so we need it cuz we'll need
*                        6) components: We can attach Components to the GameObject. Every render loop we'll update all of it and exerts its effect.
* We can add, remove, update and start it
* With imgui if the component has imgui part it'll appear
* */

public class GameObject {
    private List<Component> components;
    public transient Transform transform;
    public static int ID_COUNTER = 0;
    public int uid = -1;
    public String name;
    private boolean doSerialization = true;
    private boolean isDead = false;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();

        this.uid = ID_COUNTER++;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if(componentClass.isAssignableFrom(c.getClass())){
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting a component.";
                }
            }
        }
        return null;
    }

    public <T extends Component> void removeComponents(Class <T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        c.generateID();
        this.components.add(c);
        c.gameObject = this;
    }

    public void update (float dt) {
        for(int i = 0; i < components.size(); i++) {
            components.get(i).update(dt);
        }
    }

    public void editorUpdate(float dt) {
        for(int i = 0; i < components.size(); i++) {
            components.get(i).editorUpdate(dt);
        }
    }

    public void start() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).start();
        }
    }

    public void imgui() {
        for (Component c: components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName())) {
                c.imgui();
            }
        }
    }

    public void destroy() {
        this.isDead = true;
        for (int i = 0; i < components.size(); i++) {
            components.get(i).destroy();
        }
    }

    public GameObject copy() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
        String objAsJson = gson.toJson(this);
        GameObject obj = gson.fromJson(objAsJson, GameObject.class);
        obj.generateUid();
        for(Component c : obj.getAllComponents()) {
            c.generateID();
        }
        SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
        if(sprite != null && sprite.getTexture() != null) {
            sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilePath()));
        }
        return obj;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public static void init(int maxID) {
        ID_COUNTER = maxID;
    }

    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public boolean getIsItDoSerialization() {
        return this.doSerialization;
    }

    public int getUid() {
        return this.uid;
    }

    public void generateUid() {
        this.uid = ID_COUNTER++;
    }

    public List<Component> getAllComponents() {
        return this.components;
    }
}
