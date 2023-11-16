package components;

import engine.Camera;
import engine.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class EditorCamera extends Component {

    // This value is going to be annotated how many seconds later the drag(mouse drag) gonna be registered. Drag = middle mouse button is pushed down
    private float dragDebounce = 0.032f;

    private Camera levelEditorCamera;

    // Where I pushed the middle button first
    private Vector2f clickOrigin;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void update(float dt) {
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0) {
            this.clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= dt;
            return;
        }
        else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            Vector2f deltaDistance = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.position.sub(deltaDistance.mul(dt));
            this.clickOrigin.lerp(mousePos, dt);
        }
        System.out.println(clickOrigin.x + "   " + clickOrigin.y);
    }
}
