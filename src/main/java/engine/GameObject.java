package engine;

import components.Component;

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
    public Transform transform;
    public static int ID_COUNTER = 0;
    public int uid = -1;
    private String name;
    private int zIndex;


    public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
        this.zIndex = zIndex;
        this.components = new ArrayList<>();
        this.transform = transform;

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

    public void start() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).start();
        }
    }

    public void imgui() {
        for (Component c: components) {
            c.imgui();
        }
    }

    public int zIndex() {
        return zIndex;
    }

    public static void init(int maxID) {
        ID_COUNTER = maxID;
    }

    public int getUid() {
        return this.uid;
    }

    public List<Component> getAllComponents() {
        return this.components;
    }
}
