/*
 * Copyright (c) 2019, Darius Dinger
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.coreengine.rendering.programs.pp;

import javax.vecmath.Vector2f;

/**Shader for the radial blur effect
 *
 * @author Darius Dinger
 */
public class RadialBlurPPShader extends PPShader{
    
    private int intensityLoc, brightnessLoc, originLoc, sizeLoc, qualityLoc;
    
    @Override
    protected String getPPFragShaderFile() {
        return "radialBlur.frag";
    }
    
    @Override
    protected void setUniformLocations() {
        intensityLoc = getUniformLocation("intensity");
        brightnessLoc = getUniformLocation("brightness");
        originLoc = getUniformLocation("origin");
        sizeLoc = getUniformLocation("size");
        qualityLoc = getUniformLocation("quality");
    }
    
    /**Setting the size of the next to blur image texel.<br>
     * vec2(1.0f / image.width, 1.0f / image.height)
     * 
     * @param size Size of a texel of the next to blur image
     */
    public void setSize(Vector2f size){
        setUniform(sizeLoc, size.x, size.y);
    }
    
    /**Preparing the shader for the next blur
     * 
     * @param intensity Intensity of the blur
     * @param brightness Brightnessof the blur
     * @param origin Origin of the blur
     * @param quality Quality of the blur
     */
    public void prepareBlur(float intensity, float brightness, Vector2f origin, 
            int quality){
        setUniform(intensityLoc, intensity);
        setUniform(brightnessLoc, brightness);
        setUniform(originLoc, origin.x, origin.y);
        setUniform(qualityLoc, quality);
    }
}
