package engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import renderer.*;
import scenes.LevelEditorScene;
import scenes.LevelScene;
import scenes.Scene;
import util.AssetPool;


import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private String title;
    public float r, g, b, a;
    private boolean fadeToBalck = false;

    private static Window window = null;

    // Memory Address for the window
    private long glfwWindow;

    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;

    private static Scene currentScene;

    // The Constructor is private because the Window needs to be Singleton.
    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Bag Engine - Editor";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    // "Scene Manager": we can set the scene which one we need and load/init/start/ is
    public static void changeScene(int newScene){
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "1";
                break;
        }
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    // Because this class need to be Singleton when we call it, we need to create it.
    public static Window get() {
        if (Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
    }

    // Runtime
    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        // Start the whole Engine
        init();
        loop();

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

        // Setting up the mouse and the keyborad callback functions
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
        //glfwSwapInterval(1);

        // Show the Window
        glfwShowWindow(glfwWindow);

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
        Window.changeScene(0);
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
            // Getting ready OpenGL to render for the PickingTexture functionality
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

            // This part is the responsible for the PickingTexture functionality
            // Not the game it's just the Debugger
            DebugDraw.beginFrame();

            // Render the game
            this.framebuffer.bind();
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if(dt >= 0) {
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);
                currentScene.update(dt);
                currentScene.render();
            }

            this.framebuffer.unbind();
            this.imGuiLayer.update(dt, currentScene);
            glfwSwapBuffers(glfwWindow);

            // Calculate delta time
            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
        // Exit with serialization
        currentScene.saveExit();
    }

    // GETTERS AND SETTERS
    public static Scene getScene() {
        return get().currentScene;
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
}
