package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

/*
*   This class is generally responsible for store the "meta-data" of a texture and prepare the GPU.
*   Currently, we have to type of textures.
*   One for the regular renderer and one for the Framebuffer.
*   We're preparing the data to the GPU is different in the case of the framebuffer or the regular Texture.
*/

public class Texture {
    private String filePath;
    private transient int texID;
    private int width, height;

    // An Empty constructor. It'll get data later.
    public Texture() {
        texID = -1;
        width = -1;
        height = -1;
    }

    // Classic Texture
    // It'll upload the image at this file path
    public void init (String filePath) {
        this.filePath = filePath;

        // Texture
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // Textures parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);


        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filePath, width, height, channels, 0);

        if (image != null) {
            this.width = width.get(0);
            this.height = height.get(0);
            if(channels.get(0) == 3){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            }
        } else {
            assert false: "Error(Texture): Could not load image '" + filePath + "'";

        }
        stbi_image_free(image);
    }

    // Texture for framebuffer
    //
    public Texture (int width, int height) {
        this.filePath = "Generated";

        // Generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE, 0);
    }

    // GETTERS
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return texID;
    }

    public String getFilePath() {
        return this.filePath;
    }

    // EQUALS FUNCTION
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Texture)) return false;
        Texture oTex = (Texture)o;
        return oTex.getWidth() == this.width && oTex.getHeight() == this.height &&
                oTex.getId() == this.texID &&
                oTex.getFilePath().equals(this.filePath);
    }
}
