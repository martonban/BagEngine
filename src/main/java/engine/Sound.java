package engine;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound {
    private int bufferID;
    private int sourceID;
    private String filePath;

    private boolean isPalying = false;

    public Sound(String filePath, boolean loops) {
        this.filePath = filePath;

        // Allocate space to store the return information from stb
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filePath, channelsBuffer, sampleRateBuffer);

        if(rawAudioBuffer == null) {
            System.out.println("Not able to load the audio!" + filePath);
            stackPop();
            stackPop();
            return;
        }

        // Retrieve extra information stored in the buffers by stb
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        // Free memory
        stackPop();
        stackPop();

        // Find the correct openAL format
        int format = -1;
        if (channels == 1) {
            format = AL_FORMAT_MONO16;
        } else if (channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        bufferID = alGenBuffers();
        alBufferData(bufferID, format, rawAudioBuffer, sampleRate);

        // Generate Source
        sourceID = alGenSources();

        alSourcei(sourceID, AL_BUFFER, bufferID);
        alSourcei(sourceID, AL_LOOPING, loops ? 1 : 0);
        alSourcei(sourceID, AL_POSITION, 0);
        alSourcef(sourceID, AL_GAIN, 0.3f);

        // Free stb audio raw buffer
        free(rawAudioBuffer);
    }

    // Use this if we change a scene where are different sounds
    public void delete() {
        alDeleteSources(sourceID);
        alDeleteBuffers(bufferID);
    }

    public void play() {
        int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
           isPalying = false;
           alSourcei(sourceID, AL_POSITION, 0);
        }

        if (!isPalying) {
            alSourcePlay(sourceID);
            isPalying = true;
        }
    }
    
    public void stop() {
        if (isPalying) {
           alSourceStop(sourceID);
           isPalying = false;
        }
    }

    public String getFilePath() {
        return this.filePath;
    }

    public boolean isPalying() {
        int state = alGetSourcei(sourceID, AL_SOURCE_STATE);
        if(state == AL_STOPPED) {
            isPalying = false;
        }
        return isPalying;
    }
}
