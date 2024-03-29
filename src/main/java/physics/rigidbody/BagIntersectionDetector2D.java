package physics.rigidbody;

import org.joml.Vector2f;
import physics.primitives.BagAABB;
import physics.primitives.BagBox2D;
import physics.primitives.BagCircle;
import renderer.Line2D;
import util.JMath;

public class BagIntersectionDetector2D {

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

    public static boolean pointInCircle(Vector2f point, BagCircle circle) {
        Vector2f circleCenter = circle.getCenter();
        Vector2f centerToPoint = new Vector2f(point).sub(circleCenter);

        return centerToPoint.lengthSquared() < circle.getRadius() * circle.getRadius();
    }

    public static boolean pointInAABB (Vector2f point, BagAABB box) {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return point.x <= max.x && min.x <= point.x && point.y <= max.y && min.y <= point.y;
    }

    public static boolean pointInBox2D(Vector2f point, BagBox2D box) {
        Vector2f pointLocalBoxSpace = new Vector2f(point);
        JMath.rotate(pointLocalBoxSpace, box.getRigidBody().getRotation(), box.getRigidBody().getPosition());

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return pointLocalBoxSpace.x <= max.x && min.x <= pointLocalBoxSpace.x &&
                pointLocalBoxSpace.y <= max.y && min.y <= pointLocalBoxSpace.y;
    }

    public static boolean lineAndCircle(Line2D line, BagCircle circle) {
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

    public static boolean lineAndAABB(Line2D line, BagAABB box) {
        if(pointInAABB(line.getStart(), box) || pointInAABB(line.getEnd(), box)) {
            return true;
        }
        Vector2f unitVector = new Vector2f(line.getEnd()).sub(line.getStart());
        unitVector.normalize();
        unitVector.x = (unitVector.x != 0) ? 1.0f / unitVector.x : 0;
        unitVector.y = (unitVector.y != 0) ? 1.0f / unitVector.y : 0;

        Vector2f min = box.getMin();
        min.sub(line.getStart()).min(unitVector);
        Vector2f max = box.getMax();
        max.sub(line.getStart()).mul(unitVector);

        float tmin = Math.max(Math.min(min.x, max.x), Math.min(min.y, max.y));
        float tmax = Math.min(Math.max(min.x, max.x), Math.max(min.y, max.y));
        if(tmax < 0 || tmin > tmax) {
            return false;
        }
        float t = (tmin < 0f) ? tmax : tmin;
        return t > 0f && t * t < line.lengthSquared();
    }

    public static boolean lineAndBox2D(Line2D line, BagBox2D box) {
        float theta = box.getRigidBody().getRotation();
        Vector2f center = box.getRigidBody().getPosition();
        Vector2f localStart = new Vector2f(line.getStart());
        Vector2f localEnd = new Vector2f(line.getEnd());
        JMath.rotate(localStart, theta, center);
        JMath.rotate(localEnd, theta, center);

        Line2D localLine = new Line2D(localStart, localEnd);
        BagAABB aabb = new BagAABB(box.getMin(), box.getMax());

        return lineAndAABB(localLine, aabb);
    }
}
