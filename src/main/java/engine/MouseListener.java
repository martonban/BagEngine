package engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.lang.reflect.Array;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/*
*   The MouseListener class is a Singleton static class, to handle Mouse input through GLFW.
*
*
*
*
*/

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos;
    private boolean mouseButtonPressed[] = new boolean[9];
    private boolean isDragging;

    private int mouseButtonDown = 0;

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    // If something accidentally stays in the memory, when we call the MouseListener it'll reset everything.
    private MouseListener(){
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
    }

    public static void endFrame() {
        get().scrollY = 0.0f;
        get().scrollX = 0.0f;
    }

    public static void clear() {
        get().scrollX = 0.0;
        get().scrollY = 0.0;
        get().xPos = 0.0;
        get().yPos = 0.0;
        get().mouseButtonDown = 0;
        get().isDragging = false;
        Arrays.fill(get().mouseButtonPressed, false);
    }

    // Because of the singleton, we will initialize with this call.
    public static MouseListener get() {
        if(instance == null){
            instance = new MouseListener();
        }
        return instance;
    }

    // Previously I had a Hungarian comment but removed, If I'm commenting properly, this will help
    public static void mousePosCallback(long window, double xpos, double ypos) {
        if(!Window.getImGuiLayer().getGameViewWindow().getWantCaptureMouse()) {
            clear();
        }

        if(get().mouseButtonDown > 0) {
            get().isDragging = true;
        }

        get().xPos = xpos;
        get().yPos = ypos;
    }

    // Previously I had a Hungarian comment but removed, If I'm commenting properly, this will help
    public static void mouseButtonCallback (long window, int button, int action, int mods) {
        if(action == GLFW_PRESS) {
            get().mouseButtonDown++;
            if(button < get().mouseButtonPressed.length){
                get().mouseButtonPressed[button] = true;
            }
        }else if(action == GLFW_RELEASE){
            get().mouseButtonDown--;
            if(button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static float getX(){
        return (float)get().xPos;
    }

    public static float getY(){
        return (float)get().yPos;
    }

    public static float getScrollX(){
        return (float)get().scrollX;
    }

    public static float getScrollY(){
        return (float)get().scrollY;
    }

    public static boolean isDragging(){
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button){
        if(button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        }else{
            return false;
        }
    }

    public static float getWorldX() {
        return getWorld().x;
    }

    public static float getWorldY() {
        return getWorld().y;
    }

    public static Vector2f getWorld() {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * 2.0f - 1.0f;
        float currentY = (getY() - get().gameViewportPos.y);
        currentY = (2.0f * (1.0f - (currentY / get().gameViewportSize.y))) - 1;
        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);

        Camera camera = Window.getScene().camera();
        Matrix4f inverseView = new Matrix4f(camera.getInverseViewMatrix());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjectionMatrix());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    public static Vector2f screenToWorld(Vector2f screenCords) {
        Vector2f normalizedScreenCoords = new Vector2f(
                screenCords.x / Window.getWidth(),
                screenCords.y / Window.getHeight()
        );
        // [-1, 1]
        normalizedScreenCoords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        Camera camera = Window.getScene().camera();
        Vector4f tmp = new Vector4f(normalizedScreenCoords.x, normalizedScreenCoords.y, 0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseViewMatrix());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjectionMatrix());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    public static Vector2f worldToScene(Vector2f worldCoordinates) {
        Camera camera = Window.getScene().camera();
        Vector4f ndcSpacePos = new Vector4f(worldCoordinates.x, worldCoordinates.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePos.mul(projection.mul(view));
        Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(Window.getWidth(), Window.getHeight()));

        return windowSpace;
    }

    public static float getScreenX() {
        return getScreen().x;
    }

    public static float getScreenY() {
        return getScreen().y;
    }

    public static Vector2f getScreen() {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * 1920.0f;

        float currentY = getY() - get().gameViewportPos.y;
        currentY = 1080.0f - ((currentY / get().gameViewportSize.y) * 1080.0f);

        return new Vector2f(currentX, currentY);
    }

    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        get().gameViewportSize.set(gameViewportSize) ;
    }
}
