package physics.primitives;

import org.joml.Vector2f;

public class AABB {
    private Vector2f center = new Vector2f();
    private Vector2f size = new Vector2f();


    // Min = Botton Left Corner
    // Max = Top Right Corner
    public AABB(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.center = new Vector2f(min).add(new Vector2f(size).div(0.5f));
    }
}
