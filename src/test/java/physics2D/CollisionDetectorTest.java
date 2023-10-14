package physics2D;

import org.joml.Vector2f;
import org.junit.Test;
import physics.rigidbody.IntersectionDetector2D;
import renderer.Line2D;

import static junit.framework.TestCase.assertTrue;

public class CollisionDetectorTest {
    @Test
    public void pointOnLine2DShouldReturnTrueTest() {
        Line2D line = new Line2D(new Vector2f(0,0), new Vector2f(12, 4));
        Vector2f point = new Vector2f(0,0);

        assertTrue(IntersectionDetector2D.pointOnLine(point, line));
    }

    @Test
    public void pointOnLine2DShouldReturnTrueTestTwo() {
        Line2D line = new Line2D(new Vector2f(0,0), new Vector2f(12, 4));
        Vector2f point = new Vector2f(12,4);

        assertTrue(IntersectionDetector2D.pointOnLine(point, line));
    }

    @Test
    public void pointOnLine2DShouldReturnTrueTestThree() {
        Line2D line = new Line2D(new Vector2f(0,0), new Vector2f(0, 10));
        Vector2f point = new Vector2f(0,5);

        assertTrue(IntersectionDetector2D.pointOnLine(point, line));
    }

}
