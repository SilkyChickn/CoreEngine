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

import org.lwjgl.opengl.GL11;

import de.coreengine.framework.Window;
import de.coreengine.rendering.renderer.MasterRenderer;

/**
 * Shader for dof effect
 *
 * @author Darius Dinger
 */
public class DofPPShader extends PPShader {

    private final int strengthTextureUnit = 2;

    private int directionsLoc, qualityLoc, sizeLoc, resolutionLoc, cameraPlanesLoc, areaLoc;

    @Override
    protected String getPPFragShaderFile() {
        return "dof.frag";
    }

    @Override
    protected void setUniformLocations() {
        directionsLoc = getUniformLocation("directions");
        qualityLoc = getUniformLocation("quality");
        sizeLoc = getUniformLocation("size");
        resolutionLoc = getUniformLocation("resolution");
        cameraPlanesLoc = getUniformLocation("cameraPlanes");
        areaLoc = getUniformLocation("area");
        bindTextureUnit("strengthTexture", strengthTextureUnit);
    }

    /**
     * Setting strength texture, where the g value represent the strength of the dof
     * at this point.
     * 
     * @param tex New strength texture
     */
    public void setStrengthTexture(int tex) {
        bindTexture(tex, strengthTextureUnit, GL11.GL_TEXTURE_2D);
    }

    /**
     * Set dof area
     * 
     * @param density  Dof density
     * @param gradient Dof gradient
     */
    public void setArea(float density, float gradient) {
        setUniform(areaLoc, density, gradient);
    }

    /**
     * Prepare gaussian blur, by setting blur settings for next blur.
     * 
     * @param directions Directions to blur
     * @param quality    Blur quality
     * @param size       Blur size / radius
     */
    public void prepareBlur(float directions, float quality, float size) {
        setUniform(directionsLoc, directions);
        setUniform(qualityLoc, quality);
        setUniform(sizeLoc, size);
        setUniform(resolutionLoc, Window.getWidth(), Window.getHeight());
        setUniform(cameraPlanesLoc, MasterRenderer.getCamera().getNearPlane(),
                MasterRenderer.getCamera().getFarPlane());
    }
}
