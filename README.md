# Bag Engine [Working on the 0.2 version]
👷🏻‍♂️ Warning: This project is not yet production-ready. The source code contains many bugs, but development continues after a one-year break! <br><br>
Bag Engine is a 2D game engine built with Java and OpenGL, utilizing industry-standard libraries such as ImGui, Box2D, and GSON. While the engine is not yet production-ready, you can check the milestones section for a better understanding of its current state.

# Documentation
If you want to understand Bag Engine better please visit the [documentation](https://martonban.notion.site/Bag-Engine-Docs-18a2493db6ef803ca04cc6ab0cf9193d).

# Libraries/Technologies:
- Maven: Build System
- LWJGL: For window handling, graphics and math libraries.
- Box2D: For Physics
- GSON: For serialization/deserialization
- ImGUI: For UI elements

# Functionalities:
## Editor and Runtime 
Bag Engine includes an integrated editor that allows users to create and modify their own levels, with a built-in serializer handling the saving process. By pressing the 'Play' button, users can launch a debug mode, which effectively serves as the engine's runtime. <br><br><br>
![editor2](https://github.com/user-attachments/assets/36513b81-04e7-4583-8ea5-05d21b99fbb3)


## ECS System
The engine features a classic Entity-Component-System (ECS) architecture. This allows each scene to contain multiple game objects, each composed of various components. These components define and modify the behavior of their respective game objects. <br><br><br>
![ecs2](https://github.com/user-attachments/assets/f59f5f8a-82f2-431c-8e27-0a6d4fa45080)

## Physics System
The engine includes a physics system powered by the Box2D library. This enables the creation of dynamic game objects that move based on real-world physics simulations. <br><br><br>
![physics2](https://github.com/user-attachments/assets/3b892c6d-d840-4e36-99af-9a1f3a54272c)

## Docking 
With the help of the industry-recognized and widely used ImGui library, I am able to provide a highly dynamic user interface. As a result, all GUI elements are fully customizable and can be tailored to the user's specific needs. <br><br><br>
![docking](https://github.com/user-attachments/assets/ecffaa47-ccd3-4412-bb4a-a0e23f72fd1f)

## Asset Browser
The engine features an Asset Browser, which allows users to browse through tiles, prefabs, and audio files. <br><br><br>
![assets](https://github.com/user-attachments/assets/ed6c4e4b-da8e-4213-b71a-2a2b83eddb10)


