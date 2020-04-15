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
package de.coreengine.sound;

import de.coreengine.util.gl.MemoryDumper;
import org.lwjgl.openal.AL10;

/**
 * Class that represents a audio source to play audio from
 *
 * @author Darius Dinger
 */
public class AudioSource {

    // Id of the audio source
    private final int id;

    /**
     * Create new audio source
     */
    public AudioSource() {
        id = AL10.alGenSources();
        MemoryDumper.addAudioSource(id);
    }

    /**
     * Setting sound for this source and play
     * 
     * @param sound Sound to play from source
     */
    public void play(int sound) {
        setSound(sound);
        play();
    }

    /**
     * @param sound Setting sound to play from this source
     */
    public void setSound(int sound) {
        AL10.alSourcei(id, AL10.AL_BUFFER, sound);
    }

    /**
     * @param vol New volume of the source
     */
    public void setVolume(int vol) {
        AL10.alSourcef(id, AL10.AL_GAIN, vol);
    }

    /**
     * Stop source from playing audio
     */
    public void stop() {
        AL10.alSourceStop(id);
    }

    /**
     * Pause source from playing audio
     */
    public void pause() {
        AL10.alSourcePause(id);
    }

    /**
     * Continue playing after pause
     */
    public void play() {
        AL10.alSourcePlay(id);
    }

    /**
     * @param loop Should source playing in loop?
     */
    public void setLoop(boolean loop) {
        AL10.alSourcei(id, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    /**
     * Setting position of the source in the 3d world
     * 
     * @param x X Position of the source
     * @param y Y Position of the source
     * @param z Z Position of the source
     */
    public void setPosition(float x, float y, float z) {
        AL10.alSource3f(id, AL10.AL_POSITION, x, y, z);
    }

    /**
     * Setting direction and range of the source in the 3d world as 3d vector
     * 
     * @param x X Direction of the source
     * @param y Y Direction of the source
     * @param z Z Direction of the source
     */
    public void setVelocity(float x, float y, float z) {
        AL10.alSource3f(id, AL10.AL_VELOCITY, x, y, z);
    }

    /**
     * Setting rolloff factor for this source in the distance. Zero will b no
     * rolloff.
     * 
     * @param rolloff New rolloff factor
     */
    public void setRolloff(float rolloff) {
        AL10.alSourcef(id, AL10.AL_ROLLOFF_FACTOR, rolloff);
    }

    /**
     * @param rel Is the source relative?
     */
    public void setRelative(boolean rel) {
        AL10.alSourcei(id, AL10.AL_SOURCE_RELATIVE, rel ? AL10.AL_TRUE : AL10.AL_FALSE);
    }
}
