/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2019, Suuirad
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.coreengine.asset;

import de.coreengine.util.BufferUtils;
import de.coreengine.util.Logger;
import de.coreengine.util.gl.MemoryDumper;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Class that can load a sound file
 *
 * @author Darius Dinger
 */
public class OggLoader {

    /**
     * Loading ogg sound file and storing into asset database
     *
     * @param file         Ogg sound file
     * @param fromResouces Load sound from resources
     */
    public static void loadSound(String file, boolean fromResouces) {
        if (AssetDatabase.getSound(file) != 0)
            return;

        ShortBuffer audioData = null;
        int channels, sampleRate;

        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer channelsBuffer = stack.mallocInt(1);
            IntBuffer sampleRateBuffer = stack.mallocInt(1);

            // Load sound file into buffers
            if (fromResouces) {
                ByteBuffer buffer = BufferUtils.ioResourceToByteBuffer(file, 8 * 1024);
                if (buffer == null) {
                    Logger.warn("Error by loading sound", "The OGG file " + file + " could not be found!");
                    return;
                }
                audioData = STBVorbis.stb_vorbis_decode_memory(buffer, channelsBuffer, sampleRateBuffer);
            } else {
                audioData = STBVorbis.stb_vorbis_decode_filename(file, channelsBuffer, sampleRateBuffer);
            }

            if (audioData == null) {
                Logger.warn("Error by loading audio", "The audio file " + file + " could not be loaded!");
            }

            // Get data from buffers
            channels = channelsBuffer.get();
            sampleRate = sampleRateBuffer.get();

        } catch (IOException e) {
            Logger.warn("Error by loading audio", "The audio file " + file + " could not be loaded!");
            return;
        }

        // Getting format (Mono/Stereo)
        int format = AL10.AL_FORMAT_MONO16;
        switch (channels) {
            case 1:
                format = AL10.AL_FORMAT_MONO16;
                break;
            case 2:
                format = AL10.AL_FORMAT_STEREO16;
                break;
            default:
                Logger.warn("Error by extracting audio format",
                        "The audio format for " + file + " couldn't be extracted!");
                break;
        }

        // Create audio buffer
        int sound = AL10.alGenBuffers();
        MemoryDumper.addAudioBuffer(sound);

        // Load audio into buffer
        assert audioData != null;
        AL10.alBufferData(sound, format, audioData, sampleRate);

        AssetDatabase.addSound(file, sound);
    }
}
