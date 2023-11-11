package scenes;


import components.*;
import engine.Camera;
import engine.GameObject;
import engine.Prefabs;
import engine.Transform;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private Spritesheet sprites;

    // This game object is responsible for the DebugGrid and the SnapToGrid
    GameObject developerToolGameObject = new GameObject("Level Editor", new Transform(new Vector2f()), 0);

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        developerToolGameObject.addComponent(new MouseControls());
        developerToolGameObject.addComponent(new GridLines());

        loadResources();
        this.camera = new Camera(new Vector2f());

        sprites = AssetPool.getSpritesheet("assets/spritesheets/decorationsAndBlocks.png");
        if (levelLoaded) {
            if(gameObjects.size() > 0){
                this.activeGameObject = gameObjects.get(0);
            }
            return;
        }

    }


    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        AssetPool.getTexture("assets/textures/blendImage2.png");

        for (GameObject g : gameObjects) {
            if(g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
                }
            }
        }

    }


    float x = 0;
    float y = 0;
    @Override
    public void update(float dt) {
        developerToolGameObject.update(dt);
        DebugDraw.addCircle(new Vector2f(x,y), 64, new Vector3f(0, 0, 1), 1);
        x += 50f * dt;
        y += 50f * dt;

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Test Window");
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowsX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 3;
            float spriteHeight = sprite.getHeight() * 3;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)){
                GameObject object = Prefabs.generateSpriteObject(sprite, 32, 32);
                developerToolGameObject.getComponent(MouseControls.class).pickupObject(object);

            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            if(i + 1 < sprites.size() && nextButtonX2 < windowsX2) {
                 ImGui.sameLine();
            }
        }

        ImGui.end();
    }


}
