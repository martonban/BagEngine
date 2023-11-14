package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import engine.Camera;
import engine.GameObject;
import engine.GameObjectDeserializer;
import imgui.ImGui;
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

public abstract class Scene {
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    protected List <GameObject> gameObjects = new ArrayList<>();
    private boolean isRunning = false;
    protected boolean levelLoaded = false;

    public Scene() {}

    public void init(){}

    // Go throw every game object and start it. After that, we pass the instance to the renderer.
    public void start () {
        for (GameObject go : gameObjects) {
            go.start();
            this.renderer.add(go);
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
        }
    }

    public abstract void update(float dt);
    public abstract void render();

    public Camera camera() {
        return this.camera;
    }

    public void imgui() {

    }

    // This part is responsible when we stop the engine, everything is gonna saved by the Serializer.
    public void saveExit() {
        // We create and set up the Gson, to know which class which Serializer(class) will use.
        // If the code gets a Component, it will use ComponentDeserializer
        // If the code gets a GameObject, it will use GameObjectDeserializer
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();

        // We'll create level .json (this is our save file)
        // Then we will go through all the GameObjects and write into the file
        try {
            FileWriter writer = new FileWriter("level.json");
            writer.write(gson.toJson(this.gameObjects));
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
            this.levelLoaded = true;
        }
    }

    public GameObject getGameObject(int gameObjectID) {
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectID)
                .findFirst();
        return result.orElse(null);
    }
}
