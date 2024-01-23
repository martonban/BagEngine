package scenes;


import components.*;
import engine.*;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import util.AssetPool;
import util.Settings;

import java.io.File;
import java.util.Collection;

/*
*   In the current state of the engine, this is the only Scene what we're using.
*   Right now we don't have a separate editor and core, so this is THE ENGINE!!!!
*/

public class LevelEditorSceneInitializer extends SceneInitializer {
    private Spritesheet sprites;
    // This game object is responsible for the DebugGrid and the SnapToGrid
    private GameObject developerToolGameObject;


    public LevelEditorSceneInitializer() {}

    // Before
    @Override
    public void init(Scene scene) {
        sprites = AssetPool.getSpritesheet("assets/spritesheets/decorationsAndBlocks.png");
        Spritesheet gizmos = AssetPool.getSpritesheet("assets/spritesheets/gizmos.png");

        developerToolGameObject = scene.createGameObject("Level Editor");
        developerToolGameObject.setNoSerialize();
        developerToolGameObject.addComponent(new MouseControls());
        developerToolGameObject.addComponent(new KeyControls());
        developerToolGameObject.addComponent(new GridLines());
        developerToolGameObject.addComponent(new EditorCamera(scene.camera()));
        developerToolGameObject.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(developerToolGameObject);
    }

    @Override
    public void loadResources(Scene scene) {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 82, 0));
        AssetPool.addSpritesheet("assets/spritesheets/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/spritesheets/spritesheet.png"),
                        16, 16, 26, 0));
        AssetPool.addSpritesheet("assets/spritesheets/items.png",
                new Spritesheet(AssetPool.getTexture("assets/spritesheets/items.png"),
                        16, 16, 43, 0));
        AssetPool.addSpritesheet("assets/spritesheets/gizmos.png",
                new Spritesheet(AssetPool.getTexture("assets/spritesheets/gizmos.png"),
                        24, 48, 3, 0));
        AssetPool.getTexture("assets/textures/blendImage2.png");

        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", true);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
        AssetPool.addSound("assets/sounds/invincible.ogg", false);

        /*
        * We changed how
        */
        for (GameObject g : scene.getGameObjects()) {
            if(g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
                }
            }

            if(g.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = g.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }

    }

    @Override
    public void imgui() {
        ImGui.begin("Tiles");

        if(ImGui.beginTabBar("WindowTabBar")){
            if(ImGui.beginTabItem("Tiles")) {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowsX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < sprites.size(); i++) {
                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * Settings.EDITOR_TILE_SIZE_SCALE_XY;
                    float spriteHeight = sprite.getHeight() * Settings.EDITOR_TILE_SIZE_SCALE_XY;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                        developerToolGameObject.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

                    if (i + 1 < sprites.size() && nextButtonX2 < windowsX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }
            if(ImGui.beginTabItem("Prefabs")) {
                Spritesheet playerSprites = AssetPool.getSpritesheet("assets/spritesheets/spritesheet.png");
                Sprite sprite = playerSprites.getSprite(0);
                float spriteWidth = sprite.getWidth() * Settings.EDITOR_TILE_SIZE_SCALE_XY;
                float spriteHeight = sprite.getHeight() * Settings.EDITOR_TILE_SIZE_SCALE_XY;
                int id = sprite.getTexId();
                Vector2f[] texCoords = sprite.getTexCoords();

                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prefabs.generatePlayer();
                    developerToolGameObject.getComponent(MouseControls.class).pickupObject(object);
                }
                ImGui.sameLine();
                ImGui.endTabItem();
            }
            if(ImGui.beginTabItem("Sounds")) {
                Collection<Sound> sounds = AssetPool.getAllSound();
                for (Sound sound : sounds) {
                    File tmp  = new File(sound.getFilePath());
                    if(ImGui.button(tmp.getName())) {
                        if(!sound.isPlaying()) {
                            sound.play();
                        } else {
                            sound.stop();
                        }
                    }
                    if(ImGui.getContentRegionAvailX() > 100) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }
        ImGui.end();
    }
}
