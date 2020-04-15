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

import de.coreengine.rendering.model.Color;
import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.renderable.light.PointLight;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.util.Configuration;

import javax.vecmath.Vector3f;

/**
 * Class that reprsents a moon for post processing and lighting calculation
 *
 * @author Darius Dinger
 */
public class Moon {
    private static final float DEFAULT_SIZE = Configuration.getValuef("MOON_DEFAULT_SIZE");

    // Moon size
    private float size = DEFAULT_SIZE;

    // Moon texture
    private String texture = Material.TEXTURE_BLANK;

    // Moon light sources
    private PointLight pointLight = new PointLight();

    /**
     * Creating new white moon and setting its attenuation to infinity
     */
    public Moon() {
        pointLight.getAttenuation().set(0.0f, 0.0f);
    }

    /**
     * @return Size of the moon
     */
    public final float getSize() {
        return size;
    }

    /**
     * @param size New sizeof the moon
     */
    public final void setSize(float size) {
        this.size = size;
    }

    /**
     * @return TextureData of the moon
     */
    public final String getTexture() {
        return texture;
    }

    /**
     * @param texture New texture of the moon
     */
    public final void setTexture(String texture) {
        this.texture = texture;
    }

    /**
     * Adding lights of the moon to the masterrenderer
     */
    public final void addLights() {
        MasterRenderer.renderPointLight(pointLight);
    }

    /**
     * @return Color of the moon
     */
    public final Color getColor() {
        return pointLight.getColor();
    }

    /**
     * @return Worldposition of the moon
     */
    public final Vector3f getPosition() {
        return pointLight.getPosition();
    }

    /**
     * Setting light intensity
     * 
     * @param intensity New intensity of the light
     */
    public final void setIntensity(float intensity) {
        pointLight.setIntensity(intensity);
    }
}
