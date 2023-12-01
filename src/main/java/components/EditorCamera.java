package components;

import engine.Camera;
import engine.KeyListener;
import engine.MouseListener;
import org.joml.Vector2f;
import util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_HOME;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

// TODO:: If I "un-drag" out of the viewport registered differently.
public class EditorCamera extends Component {
    private Camera levelEditorCamera;

    // This value is going to be annotated how many seconds later the drag(mouse drag) gonna be registered. Drag = middle mouse button is pushed down
    private float dragDebounce = 0.032f;
    private float lerpTime = 0.0f;


    // Where I pushed the middle button first under the dragDebounce time
    private Vector2f clickOrigin;

    private boolean reset = false;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt) {
        // DRAG FUNCTIONS
        // Didn't register as a drag, but we have to decrease the value of the dragBounce
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0.0f) {
            this.clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= dt;
            return;
        }
        // Registered as a drag, and we change the camera position
        else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            Vector2f deltaDistance = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.position.sub(deltaDistance.mul(dt * Settings.DRAG_SPEED));
            this.clickOrigin.lerp(mousePos, dt);
        }

        if (dragDebounce <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
           dragDebounce = 0.1f;
        }

        // ZOOM FUNCTION
        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * Settings.SCROLL_SENSITIVITY),
                    1.0 / levelEditorCamera.getZoom());
            addValue *= Settings.REVERSE_ZOOM * (Math.signum(MouseListener.getScrollY()));
            levelEditorCamera.addZoom(addValue);
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_HOME)) {
            reset = true;
        }

        if (reset) {
           levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
           levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
                   ((1.0f - levelEditorCamera.getZoom() ) * lerpTime));
           this.lerpTime += 0.1f * dt;
           if (Math.abs(levelEditorCamera.position.x) <= 5.0 &&
                   Math.abs(levelEditorCamera.position.y) <= 5.0) {
               this.lerpTime = 0.0f;
               levelEditorCamera.position.set(0f, 0f);
               this.levelEditorCamera.setZoom(1.0f);
               reset = false;
           }
        }

    }
}
