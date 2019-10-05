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

package de.coreengine.rendering.renderable;

import de.coreengine.rendering.model.Material;
import de.coreengine.util.Configuration;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class Particle {
    private static final float[] DEFAULT_SIZE = Configuration.getValuefa("PARTICLE_DEFAULT_SIZE");

    //Particle transformation
    private Vector2f size = new Vector2f(DEFAULT_SIZE[0], DEFAULT_SIZE[1]);
    private Vector3f position = new Vector3f();

    //Particles current texture
    private String texture = Material.TEXTURE_WHITE;

    /**@return Particles current texture
     */
    public String getTexture() {
        return texture;
    }

    /**Setting texture of the particle
     *
     * @param texture New texture of the particle
     */
    public void setTexture(String texture) {
        this.texture = texture;
    }

    /**@return Read/Writeable 2d size of the particle
     */
    public Vector2f getSize() {
        return size;
    }

    /**@return Read/Writeable 3d position of the particle
     */
    public Vector3f getPosition() {
        return position;
    }
}
