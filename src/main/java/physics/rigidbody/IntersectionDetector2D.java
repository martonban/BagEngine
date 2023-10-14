package physics.rigidbody;

import org.joml.Vector2f;
import physics.primitives.AABB;
import physics.primitives.Box2D;
import physics.primitives.Circle;
import renderer.Line2D;
import util.JMath;


public class IntersectionDetector2D {

    public static boolean pointOnLine(Vector2f point, Line2D line) {
        float dx = line.getEnd().x - line.getStart().x;
        float dy = line.getEnd().y - line.getStart().y;
        if(dx == 0) {
            return JMath.compare(point.x, line.getStart().x);
        }
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


    public static boolean pointInBox2D(Vector2f point, Box2D box) {
        Vector2f pointLocalBoxSpace = new Vector2f(point);
        JMath.rotate(pointLocalBoxSpace, box.getRigidBody().getRotation(), box.getRigidBody().getPosition());

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return pointLocalBoxSpace.x <= max.x && min.x <= pointLocalBoxSpace.x &&
                pointLocalBoxSpace.y <= max.y && min.y <= pointLocalBoxSpace.y;
    }

    public static boolean lineAndCircle(Line2D line, Circle circle) {
        if (pointInCircle(line.getStart(), circle) || pointInCircle(line.getEnd(), circle)) {
            return true;
        }
        Vector2f ab = new Vector2f(line.getEnd()).sub(line.getStart());

        // Project point
        Vector2f circleCenter = circle.getCenter();
        Vector2f centerToLineStart = new Vector2f(circleCenter).sub(line.getStart());
        float t = centerToLineStart.dot(ab) / ab.dot(ab);

        if(t < 0.0f || t > 1.0f) {
            return false;
        }

        // Closet point to line segment
        Vector2f closestPoint = new Vector2f(line.getStart()).add(ab.mul(t));

        return pointInCircle(closestPoint, circle);
    }

}
