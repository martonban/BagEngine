package engine;

import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;
import physics2d.Physics2D;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;
import util.AssetPool;


import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/*
*   This is the most important class in the project
*   Mainly we implement the
*
*
*/

public class Window implements Observer {
    // Singleton instance
    private static Window window = null;

    // GLFW related data fields
    private long glfwWindow;
    private int width, height;
    private String title;

    // Scene Manager related data fields
    private static Scene currentScene;
    private boolean runtimePlaying = false;

    // Engine related instances
    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;

    // Audio System related data fields
    private long audioContext;
    private long audioDevice;

    // The constructor is private because the Window needs to be Singleton.
    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Bag Engine - Editor";
        EventSystem.abbObserver(this);
    }

    // Because this class need to be Singleton when we call it, we need to create it.
    public static Window get() {
        if (Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
    }

    // "Scene Manager": Based on the Observer ("onNotify" function) this function is going to change the scene
    public static void changeScene(SceneInitializer sceneInitializer){
        if(currentScene != null) {
            currentScene.destroy();
        }

        getImGuiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    // We implemented the Observer system that's why we can use get notified when certain events happened
    // e.g., start to play, back to the editor, etc.
    @Override
    public void onNotify(GameObject object, Event event) {
        switch (event.type) {
            case GameEngineStartPlay:
                this.runtimePlaying = true;
                currentScene.save();
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case GameEngineStopPlay:
                this.runtimePlaying = false;
                Window.changeScene(new LevelEditorSceneInitializer());
                break;
            case LoadLevel:
                window.changeScene(new LevelEditorSceneInitializer());
                break;
            case SaveLevel:
                currentScene.save();
                break;
        }
    }

    // Runtime
    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        // Start the whole Engine
        init();
        loop();

        // Destroy audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        // When we close the program, we need to terminate the Window
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init(){
        // General error Handler
        GLFWErrorCallback.createPrint(System.err).set();

        // Init GLFW
        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Init the Window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        if(glfwWindow == NULL){
            throw new IllegalStateException("Failed to load GLFW !");
        }

        // Setting up the mouse and the keyboard callback functions
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Create OpenGL Context
        glfwMakeContextCurrent(glfwWindow);

        // Enable """"v-sync""""
        glfwSwapInterval(1);

        // Show the Window
        glfwShowWindow(glfwWindow);

        // Initialize audio device
        String defaultDeviceName = alcGetString(0 , ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        // Setup audio context
        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);


        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if(!alCapabilities.OpenAL10) {
            assert false: "Audio library is not supported";
        }

        // VERY IMPORTANT!!
        // Create the default context for OpenGL
        GL.createCapabilities();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Creating the framebuffer for the Viewport
        this.framebuffer = new Framebuffer(1920, 1080);
        // Creating the framebuffer for
        this.pickingTexture = new PickingTexture(1920, 1080);
        // Creating the render viewport(OpenGL Side)
        glViewport(0, 0, 1920, 1080);

        // ImGui Init
        this.imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        this.imGuiLayer.initImGui();

        // We want to use the Scene called "LevelEditorScene" this is in the scenes package
        Window.changeScene(new LevelEditorSceneInitializer());
    }

    // Render Loop
    public void loop(){
        // Delta Time pre-calculation
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;

        // Get the shader
        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while(!glfwWindowShouldClose(glfwWindow)) {
            // Poll Events
            glfwPollEvents();

            // This part is the responsible for the PickingTexture functionality
            // Getting ready OpenGL to render the PickingTexture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();
            glViewport(0, 0, 1920, 1080);
            glClearColor(0.f, 0.f, 0.f, 0.f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Render for the PickingTexture functionality
            Renderer.bindShader(pickingShader);
            currentScene.render();

            // Clear everything before the regular renderer
            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            // This part is the responsible for the regular rendering
            // Not the game it's just the LineRenderer
            DebugDraw.beginFrame();

            // Render the game
            this.framebuffer.bind();
            glClearColor(1.f, 1.f, 1.f, 1.f);
            glClear(GL_COLOR_BUFFER_BIT);

            if(dt >= 0) {
                Renderer.bindShader(defaultShader);
                if(runtimePlaying) {
                    currentScene.update(dt);
                } else {
                    currentScene.editorUpdate(dt);
                }
                currentScene.render();
                DebugDraw.draw();
            }

            this.framebuffer.unbind();
            // After the game get rendered we render the UI
            this.imGuiLayer.update(dt, currentScene);
            KeyListener.endFrame();
            MouseListener.endFrame();

            glfwSwapBuffers(glfwWindow);

            // We have to do this for the EditorCamera because we call adjustProjection function in the scene. To do this, we have to call the endFrame function because we need to reset everything
            MouseListener.endFrame();

            // Calculate delta time
            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    // GETTERS AND SETTERS
    public static Scene getScene() {
        return currentScene;
    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    public static Framebuffer getFrameBuffer() {
        return get().framebuffer;
    }

    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    public static ImGuiLayer getImGuiLayer() {
        return get().imGuiLayer;
    }

    public static Physics2D getPhysics() {
        return currentScene.getPhysics();
    }
}
