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
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class ParticleShader extends Shader {

    private final int colorTextureUnit = 0;

    private int vpMatLoc, fMatLoc, scaleLoc, posLoc;

    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "particle.vert", true),
                GL20.GL_VERTEX_SHADER, "Particle Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "particle.geo", true),
                GL32.GL_GEOMETRY_SHADER, "Particle Geometry Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "particle.frag", true),
                GL20.GL_FRAGMENT_SHADER, "Particle Fragment Shader");
    }

    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
    }

    @Override
    protected void loadUniforms() {
        fMatLoc = getUniformLocation("fMat");
        vpMatLoc = getUniformLocation("vpMat");
        scaleLoc = getUniformLocation("scale");
        posLoc = getUniformLocation("pos");

        bindTextureUnit("colorTexture", colorTextureUnit);
    }

    /**Prepare camera to render next particles from
     *
     * @param cam Camera to render from
     */
    public void prepareCam(Camera cam){
        setUniform(vpMatLoc, Toolbox.matrixToFloatArray(cam.getViewProjectionMatrix()));
        setUniform(fMatLoc, Toolbox.matrixToFloatArray(cam.getFacingMatrix()));
    }

    /**Preparing stuff for next particles
     *
     * @param texture Texture of next particles
     */
    public void prepareParticles(int texture){
        bindTexture(texture, colorTextureUnit, GL11.GL_TEXTURE_2D);
    }

    /**Set transformation for next particle
     *
     * @param size Size of the next particle
     * @param pos Position of the next particle
     */
    public void setNextTransform(Vector2f size, Vector3f pos){
        setUniform(scaleLoc, size.x, size.y);
        setUniform(posLoc, pos.x, pos.y, pos.z);
    }
}
