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
package de.coreengine.rendering.programs.pp;

import de.coreengine.rendering.renderer.MasterRenderer;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

/**
 * Shader for the light scattering effect
 *
 * @author Darius Dinger
 */
public class LightScatteringPPShader extends PPShader {

    private final int sunTextureUnit = 2;

    // Uniform locations
    private int sizeLoc, originLoc, intensityLoc, brightnessLoc, qualityLoc, colorLoc;

    // Sun position buffer
    private Vector4f sunPos = new Vector4f();

    @Override
    protected String getPPFragShaderFile() {
        return "lightScattering.frag";
    }

    @Override
    protected void setUniformLocations() {
        bindTextureUnit("sunTexture", sunTextureUnit);
        sizeLoc = getUniformLocation("size");
        originLoc = getUniformLocation("origin");
        intensityLoc = getUniformLocation("intensity");
        brightnessLoc = getUniformLocation("brightness");
        qualityLoc = getUniformLocation("quality");
        colorLoc = getUniformLocation("color");
    }

    /**
     * @param texture TextureData id of the sun buffer
     */
    public void setSunTexture(int texture) {
        bindTexture(texture, sunTextureUnit, GL11.GL_TEXTURE_2D);
    }

    /**
     * Preparing shader for the next sun
     * 
     * @param intensity  Intensity of the light scatters
     * @param brightness Brightness of the light scatters
     * @param quality    Quality of the light scatters
     */
    public void prepareEffect(float intensity, float brightness, int quality) {
        setUniform(intensityLoc, intensity);
        setUniform(brightnessLoc, brightness);
        setUniform(qualityLoc, quality);
    }

    /**
     * Realoading sun origin into shader
     */
    public void reloadSun() {
        Matrix4f vpMat = MasterRenderer.getCamera().getViewProjectionMatrix();

        sunPos.set(MasterRenderer.getSun().getPosition().x, MasterRenderer.getSun().getPosition().y,
                MasterRenderer.getSun().getPosition().z, 1.0f);
        vpMat.transform(sunPos);

        sunPos.x /= sunPos.w;
        sunPos.y /= sunPos.w;

        sunPos.x *= 0.5f;
        sunPos.x += 0.5f;

        sunPos.y *= 0.5f;
        sunPos.y += 0.5f;

        setUniform(originLoc, sunPos.x, sunPos.y);
        setUniform(colorLoc, MasterRenderer.getSun().getColor());
    }

    /**
     * Setting the size of the next to blur image texel.<br>
     * vec2(1.0f / image.width, 1.0f / image.height)
     * 
     * @param size Size of a texel of the next to blur image
     */
    public void setSize(Vector2f size) {
        setUniform(sizeLoc, size.x, size.y);
    }
}
