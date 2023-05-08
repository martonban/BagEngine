package engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    // Alap adatok megadása
    private int width, height;
    private String title;
    public float r, g, b, a;
    private boolean fadeToBalck = false;

    private static Window window = null;

    // Ez egy memória cím lesz
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;


    private static Scene currentScene;


    // A konstruktor azért private, mert azt szeretnénk, hogy Singelton legyen
    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "2D Texture Loader";
        r = 1;
        g = 1;
        b = 1;
        a = 1;

    }

    public static void changeScene(int newScene){
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "1";
                break;
        }
    }

    // Singelton miatt amikor meghívjuk akkor egyeszerre létre is hozzuk
    public static Window get() {
        if (Window.window == null){
            Window.window = new Window();
        }

        return Window.window;
    }

    public static Scene getScene() {
        return get().currentScene;
    }

    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        //Miután bezárjuk fel kell sazabadítani az erőforrást
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init(){
        // Ha hiba lép fel le kell tudnunk kezelni.
        GLFWErrorCallback.createPrint(System.err).set();

        // Inicializáljuk a GLFW-t
        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Konfiguráljuk a GLFW-t
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);


        // Létrehozzuk az abalakot
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        if(glfwWindow == NULL){
            throw new IllegalStateException("Failed to load GLFW !");
        }


        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Létrhozzuk az OpenGL kontextust
        glfwMakeContextCurrent(glfwWindow);

        // Engedélyezzük a 'v-sync'-et
        //glfwSwapInterval(1);

        // Láthatóvá tesszük az abalkot
        glfwShowWindow(glfwWindow);

        // FONTOS!!!!
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        this.imGuiLayer = new ImGuiLayer(glfwWindow);
        this.imGuiLayer.initImGui();

        Window.changeScene(0);

    }


    public void loop(){
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;

        while(!glfwWindowShouldClose(glfwWindow)) {
            // Poll Events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if(dt >= 0) {
                currentScene.update(dt);
            }

            this.imGuiLayer.update(dt);
            glfwSwapBuffers(glfwWindow);

            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
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

}
