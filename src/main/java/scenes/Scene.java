package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import components.Transform;
import engine.Camera;
import engine.GameObject;
import engine.GameObjectDeserializer;
import imgui.ImGui;
import org.joml.Vector2f;
import org.lwjgl.system.CallbackI;
import physics2d.Physics2D;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
*   This abstract class is the "blueprint" for a scene in the game engine. So If you want to create a scene, you need to implement this calss
*
*   Attributes:
*       - Renderer: The Engine supports different renderers. Currently, we have a texture and a line renderer. This is an instance of the renderer class.
*       - Camera: Every Scene needs to have a Camera.
*       - gameObjects: It's a list of GameObjects. Every scene has their own GameObjects. There are multiples, so that's why it's a list.
*       - activeGameObject: In the editor we can edit the attributes of an object through imGUI. So we need to know which game object are we editing.
*       - isRunning: Self-explanatory
*       - levelLoaded: This engine is support serialization. That data is annotated when the engine is ready to run (loaded every data from serializer)
* */

public class Scene {
    private Renderer renderer;
    private Camera camera;
    private Physics2D physics2D;
    private List <GameObject> gameObjects;
    private boolean isRunning;

    private SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        this.physics2D = new Physics2D();
        this.renderer = new Renderer();
        this.gameObjects = new ArrayList<>();
        this.isRunning = false;
    }

    public void init() {
        this.camera = new Camera(new Vector2f());
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }

    // Go throw every game object and start it. After that, we pass the instance to the renderer.
    public void start () {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.start();
            this.renderer.add(go);
            this.physics2D.add(go);
        }
        isRunning = true;
    }

    // If the scene isn't running, we just add to the gameObject list. The main reason for that is because the scene is not running, so to start it and give it to the renderer is unnecessary, cuz when we start the Scene it's going to be automatic.
    // If the scene is running, we need to manually start it and give it to the renderer and the gameObject list.
    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
            this.physics2D.add(go);
        }
    }

    public void editorUpdate(float dt) {
        this.camera.adjustProjection();
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.editorUpdate(dt);

            if(go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }
    }

    public void update(float dt) {
        this.camera.adjustProjection();
        this.physics2D.update(dt);

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update(dt);

            if(go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics2D.destroyGameObject(go);
                i--;
            }
        }
    }

    public void render() {
        this.renderer.render();
    }

    public Camera camera() {
        return this.camera;
    }

    public void imgui() {
        this.sceneInitializer.imgui();
    }

    // When we want to create a game object and this will return a game object
    // It helps us to maintain a cleaner code
    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    // This part is responsible when we stop the engine, everything is gonna saved by the Serializer.
    public void save() {
        // We create and set up the Gson, to know which class which Serializer(class) will use.
        // If the code gets a Component, it will use ComponentDeserializer
        // If the code gets a GameObject, it will use GameObjectDeserializer
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        // We'll create level .json (this is our save file)
        // Then we will go through all the GameObjects and write into the file
        try {
            FileWriter writer = new FileWriter("level.json");
            // We need to separate game objects based on they want to get serialized or not. For example, gizmos don't want to do that
            List<GameObject> objsToSerialize = new ArrayList<>();
            for(GameObject obj : this.gameObjects) {
                if(obj.getIsItDoSerialization()) {
                    objsToSerialize.add(obj);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // This part is responsible when we start the engine, we need to load every game object and components. So we are going to be ready for start.
    public void load() {
        // We create and set up the Gson, to know which class which Deserializer(class) will use.
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If the file is not empty, we will create every GameObjects
        if (!inFile.equals("")) {
            int maxGoId = -1;
            int maxCompId = -1;
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (int i = 0; i < objs.length; i++) {
                addGameObjectToScene(objs[i]);

                for(Component c: objs[i].getAllComponents()) {
                    if(c.getUid() > maxCompId) {
                        maxCompId = c.getUid();
                    }
                }

                if(objs[i].getUid() > maxGoId) {
                    maxGoId = objs[i].getUid();
                }
            }
            maxGoId++;
            maxCompId++;
            GameObject.init(maxGoId);
            Component.init(maxCompId);
        }
    }

    public void destroy() {
        for(GameObject go: gameObjects) {
            go.destroy();
        }
    }

    public List<GameObject> getGameObjects() {
        return this.gameObjects;
    }

    public GameObject getGameObject(int gameObjectID) {
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectID)
                .findFirst();
        return result.orElse(null);
    }

    public Physics2D getPhysics() {
        return this.physics2D;
    }
}
