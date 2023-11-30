package physics.primitives;

import org.joml.Vector2f;
import physics.rigidbody.BagRigidBody2D;

public class BagCircle {
    private float radius = 1.0f;
    private BagRigidBody2D body = null;

    public float getRadius() {
        return radius;
    }

    public Vector2f getCenter() {
        return body.getPosition();
    }
}
