package engine;


import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    private Spritesheet sprites;


    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f());

        sprites = AssetPool.getSpritesheet("assets/textures/spritesheet.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(100,100),
                new Vector2f(256, 256)) , -1);
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(obj1);
        this.activeGameObject = obj1;





        GameObject obj3 = new GameObject("Object 1", new Transform(new Vector2f(400,100),
                new Vector2f(256, 256)) , -2);
        obj3.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(obj3);


        GameObject obj4 = new GameObject("Object 2", new Transform(new Vector2f(400,100),
                new Vector2f(256, 256)), -1);
        obj4.addComponent(new SpriteRenderer(sprites.getSprite(20)));
        this.addGameObjectToScene(obj4);


    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/textures/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/textures/spritesheet.png"),
                        16, 16, 26, 0));
    }



    private int spriteIndex = 0;
    private float spriteFlipTime = 0.2f;
    private float spriteFlipTimeLeft = 0.0f;
    @Override
    public void update(float dt) {

        spriteFlipTimeLeft -= dt;
        if(spriteFlipTimeLeft <= 0) {
            spriteFlipTimeLeft = spriteFlipTime;
            spriteIndex++;
            if(spriteIndex > 4) {
                spriteIndex = 0;
            }
            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
        }



        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Test Window");
        ImGui.text("Some random shit");
        ImGui.end();
    }


}
