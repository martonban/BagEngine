package components;

import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.Settings;

/*
*   This code is responsible for drawing the grid lines to the scene
*   If we want to use we need a scene where we have a gamObject with this component. If this game-object exists, it will have an effect automatically.
*   Dependencies:    - util.Settings.java
*/

public class GridLines extends Component{

    @Override
    public void update (float dt) {
        Vector2f cameraPos = Window.getScene().camera().position;
        Vector2f projectionSize = Window.getScene().camera().getProjectionSize();

        int firstX = ((int) ((cameraPos.x / Settings.GRID_WIDTH) - 1 ) * Settings.GRID_HEIGHT);
        int firstY = ((int) ((cameraPos.y / Settings.GRID_HEIGHT) - 1 ) * Settings.GRID_HEIGHT);

        int numVerticalLines = (int)(projectionSize.x / Settings.GRID_WIDTH) + 2;
        int numHorizontalLines = (int)(projectionSize.y / Settings.GRID_HEIGHT) + 2;

        int height = (int)projectionSize.y * 2;
        int width = (int)projectionSize.x * 2;

        int maxLines = Math.max(numVerticalLines, numHorizontalLines);
        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);
        for(int i = 0; i < maxLines; i++) {
            int x = firstX + (Settings.GRID_WIDTH * i);
            int y = firstY + (Settings.GRID_HEIGHT * i);

            if(i < numVerticalLines) {
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if(i < numHorizontalLines) {
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    }
}
