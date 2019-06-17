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

import de.coreengine.asset.FileLoader;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Matrix4f;

/**Shader forthe lens flare renderer
 *
 * @author Darius Dinger
 */
public class LensFlareShader extends Shader{
    
    private final int lensFlareTextureUnit = 0;
    
    private int sizeLoc, posLoc, pMatLoc;
    
    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "lensFlare.vert", true), 
                GL20.GL_VERTEX_SHADER, "Lens Flare Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "lensFlare.frag", true), 
                GL20.GL_FRAGMENT_SHADER, "Lens Flare Fragment Shader");
    }
    
    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
    }
    
    @Override
    protected void loadUniforms() {
        bindTextureUnit("lensFlareTexture", lensFlareTextureUnit);
        
        posLoc = getUniformLocation("pos");
        sizeLoc = getUniformLocation("size");
        pMatLoc = getUniformLocation("pMat");
    }
    
    /** * @param tex Next lens flare texture
     * @param size Next lens flare size
     * @param x Next lens flare x position
     * @param y Next lens flare y position
     * @param z Next lens flare z position
     */
    public void prepareLensFlareTile(int tex, float size, float x, float y, float z){
        bindTexture(tex, lensFlareTextureUnit, GL11.GL_TEXTURE_2D);
        setUniform(posLoc, x, y, z);
        setUniform(sizeLoc, size);
    }
    
    /**Reloading ortho projection matrix into shader
     * 
     * @param ortho Ortho matrix to load
     */
    public void reloadOrtho(Matrix4f ortho){
        setUniform(pMatLoc, Toolbox.matrixToFloatArray(ortho));
    }
}
