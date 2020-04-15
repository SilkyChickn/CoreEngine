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

import org.lwjgl.openal.AL10;

import javax.vecmath.Vector3f;

/**
 * Class that represents a listener for the audios
 *
 * @author Darius Dinger
 */
public class AudioListener {

    // Position, direction and range of the listener
    private final Vector3f position = new Vector3f(), velocity = new Vector3f();
    private final float[] orientation = new float[6];

    public AudioListener() {
        orientation[3] = 0;
        orientation[4] = 1;
        orientation[5] = 0;
    }

    /**
     * @return Read/writeable vector of the listeners position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * @return Read/writeable vector of the listeners velocity
     */
    public Vector3f getVelocity() {
        return velocity;
    }

    /**
     * Orientation of the listener as 3d vector
     * 
     * @param x Orinetation to X
     * @param y Orinetation to Y
     * @param z Orinetation to Z
     */
    public void setOrientation(float x, float y, float z) {
        orientation[0] = x;
        orientation[1] = y;
        orientation[2] = z;
    }

    /**
     * Applying the listener to the scene
     */
    public void apply() {
        AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
        AL10.alListener3f(AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
        AL10.alListenerfv(AL10.AL_ORIENTATION, orientation);
    }
}
