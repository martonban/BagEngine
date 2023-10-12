package physics.primitives;

import org.joml.Vector2f;
import physics.rigidbody.RigidBody2D;

public class Circle {
    private float radius = 1.0f;
    private RigidBody2D body = null;

    public float getRadius() {
        return radius;
    }

    public Vector2f getCenter() {
        return body.getPosition();
    }

}
