package components;

import engine.Camera;
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
    public void editorUpdate (float dt) {
        Camera camera = Window.getScene().camera();
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        float firstX = ((int) ((cameraPos.x / Settings.GRID_WIDTH) - 1 ) * Settings.GRID_HEIGHT);
        float firstY = ((int) ((cameraPos.y / Settings.GRID_HEIGHT) - 1 ) * Settings.GRID_HEIGHT);

        int numVerticalLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
        int numHorizontalLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

        int height = (int)(projectionSize.y * camera.getZoom()) * 2;
        int width = (int)(projectionSize.x * camera.getZoom()) * 2;

        int maxLines = Math.max(numVerticalLines, numHorizontalLines);
        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);
        for(int i = 0; i < maxLines; i++) {
            float x = firstX + (Settings.GRID_WIDTH * i);
            float y = firstY + (Settings.GRID_HEIGHT * i);

            if(i < numVerticalLines) {
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if(i < numHorizontalLines) {
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    }
}
