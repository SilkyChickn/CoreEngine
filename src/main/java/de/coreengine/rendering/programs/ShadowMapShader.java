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
import de.coreengine.rendering.renderable.Entity;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Matrix4f;

public class ShadowMapShader extends Shader {

    private int transMatLoc, vpMatLoc;

    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "shadowMap.vert", true), GL20.GL_VERTEX_SHADER,
                "Shadow Map Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "shadowMap.frag", true), GL20.GL_FRAGMENT_SHADER,
                "Shadow Map Fragment Shader");
    }

    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
    }

    @Override
    protected void loadUniforms() {
        transMatLoc = getUniformLocation("transMat");
        vpMatLoc = getUniformLocation("vpMat");
    }

    /**
     * Prepare shader for next entity to render
     *
     * @param entity Next entity to render
     */
    public void prepareEntity(Entity entity) {
        setUniform(transMatLoc, entity.getTransform().getTransMatArr());
    }

    /**
     * Setting the vpMat variable of the shader
     *
     * @param mat Matrix to set as vpMat
     */
    public void setVPMat(Matrix4f mat) {
        setUniform(vpMatLoc, Toolbox.matrixToFloatArray(mat));
    }
}
