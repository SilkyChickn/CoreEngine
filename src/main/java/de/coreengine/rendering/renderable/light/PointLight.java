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
package de.coreengine.rendering.renderable.light;

import de.coreengine.util.Configuration;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**
 * Represent a point light in the scene with specific range
 *
 * @author Darius Dinger
 */
public class PointLight extends AmbientLight {
    private static float DEFAULT_LINEAR_DROP = Configuration.getValuef("LIGHT_DEFAULT_LINEAR_DROP");
    private static float DEFAULT_SQUARED_DROP = Configuration.getValuef("LIGHT_DEFAULT_SQUARED_DROP");

    // Lights attenuation (x = linear drop, y = squared drop)
    private Vector2f attenuation = new Vector2f(DEFAULT_LINEAR_DROP, DEFAULT_SQUARED_DROP);

    // Position of the point light in the 3d world
    private Vector3f position = new Vector3f();

    /**
     * @return Lights current attenuation (x = linear drop, y = squared drop)
     */
    public Vector2f getAttenuation() {
        return attenuation;
    }

    /**
     * @return Lights position in the 3d world
     */
    public Vector3f getPosition() {
        return position;
    }
}
