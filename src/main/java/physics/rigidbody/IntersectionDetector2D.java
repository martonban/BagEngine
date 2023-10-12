package physics.rigidbody;

import org.joml.Vector2f;
import physics.primitives.AABB;
import physics.primitives.Box2D;
import physics.primitives.Circle;
import renderer.Line2D;


public class IntersectionDetector2D {

    public static boolean pointOnLine(Vector2f point, Line2D line) {
        float dx = line.getEnd().x - line.getStart().x;
        float dy = line.getEnd().y - line.getStart().y;
        float m = dy / dx;
        float b = line.getEnd().y - (m * line.getEnd().x);

        return point.y == m * point.x + b;
    }

    public static boolean pointInCircle(Vector2f point, Circle circle) {
        Vector2f circleCenter = circle.getCenter();
        Vector2f centerToPoint = new Vector2f(point).sub(circleCenter);

        return centerToPoint.lengthSquared() < circle.getRadius() * circle.getRadius();
    }

    public static boolean pointInAABB (Vector2f point, AABB box) {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return point.x <= max.x && min.x <= point.x && point.y <= max.y && min.y <= point.y;
    }

    //public static boolean pointInBox2D(Vector2f point, Box2D box) {}

}
