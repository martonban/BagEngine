package physics.primitives;


import org.joml.Vector2f;
import physics.rigidbody.RigidBody2D;

public class AABB {
    private Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();
    private RigidBody2D rigidBody = null;

    public AABB() {
        this.halfSize = new Vector2f(size).div(2);
    }

    // Min = Botton Left Corner
    // Max = Top Right Corner
    public AABB(Vector2f min, Vector2f max) {
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
