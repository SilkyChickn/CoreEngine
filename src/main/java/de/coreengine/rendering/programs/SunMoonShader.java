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
package de.coreengine.rendering.programs;

import de.coreengine.asset.AssetDatabase;
import de.coreengine.asset.FileLoader;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderable.Moon;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**Shader for the sun renderer
 *
 * @author Darius Dinger
 */
public class SunMoonShader extends Shader{
    
    private final int colorTextureUnit = 0;
    
    private int vpMatLoc, scaleLoc;
    
    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(SHADERS_LOCATION + "sunMoon.vert", true),
                    GL20.GL_VERTEX_SHADER, "Sun/Moon Vertex Shader");
        addShader(FileLoader.getResource(SHADERS_LOCATION + "sunMoon.frag", true),
                    GL20.GL_FRAGMENT_SHADER, "Sun/Moon Fragment Shader");
    }
    
    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
    }
    
    @Override
    protected void loadUniforms() {
        vpMatLoc = getUniformLocation("vpMat");
        scaleLoc = getUniformLocation("scale");
        
        bindTextureUnit("colorTexture", colorTextureUnit);
    }
    
    /**Preparing shader for the next sun or moon
     * 
     * @param moon Moon or sun to prepare
     * @param cam Camera to render from
     */
    public void prepareMoon(Moon moon, Camera cam){
        setUniform(scaleLoc, moon.getSize());
        
        setUniform(vpMatLoc, Toolbox.matrixToFloatArray(cam.getFacingMVPMatrix(
                moon.getPosition().x, moon.getPosition().y, moon.getPosition().z)));
        
        bindTexture(AssetDatabase.getTexture(moon.getTexture()), colorTextureUnit, GL11.GL_TEXTURE_2D);
    }
}
