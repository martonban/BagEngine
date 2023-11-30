package physics.primitives;


import org.joml.Vector2f;
import physics.rigidbody.BagRigidBody2D;

public class BagAABB {
    private Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private BagRigidBody2D rigidBody = null;

    public BagAABB() {
        this.halfSize = new Vector2f(size).div(2);
    }

    // Min = Botton Left Corner
    // Max = Top Right Corner
    public BagAABB(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size).div(2);
    }

    public Vector2f getMin() {
        return new Vector2f(this.rigidBody.getPosition()).sub(this.halfSize);
    }

    public Vector2f getMax() {
        return new Vector2f(this.rigidBody.getPosition()).add(this.halfSize);
    }
}
