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

import de.coreengine.asset.FileLoader;
import de.coreengine.rendering.programs.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Shader for a post processing effect
 *
 * @author Darius Dinger
 */
public abstract class PPShader extends Shader {
    private static final String PP_SHADER_LOCATION = Shader.SHADERS_LOCATION + "pp/";

    private final int colorTextureUnit = 0, depthTextureUnit = 1;

    protected abstract String getPPFragShaderFile();

    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(PPShader.PP_SHADER_LOCATION + "pp.vert", true), GL20.GL_VERTEX_SHADER,
                "PostProcessing Vertex Shader");
        addShader(FileLoader.getResource(PPShader.PP_SHADER_LOCATION + getPPFragShaderFile(), true),
                GL20.GL_FRAGMENT_SHADER, getPPFragShaderFile());
    }

    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
    }

    @Override
    protected void loadUniforms() {
        bindTextureUnit("colorTexture", colorTextureUnit);
        bindTextureUnit("depthTexture", depthTextureUnit);

        setUniformLocations();
    }

    /**
     * Setting the effect shader relevant uniforms.
     */
    protected abstract void setUniformLocations();

    /**
     * Setting base post processing effect textures
     * 
     * @param color Input color texture
     * @param depth Input depth buffer texture
     */
    public void setBaseTexture(int color, int depth) {
        bindTexture(color, colorTextureUnit, GL11.GL_TEXTURE_2D);
        bindTexture(depth, depthTextureUnit, GL11.GL_TEXTURE_2D);
    }
}
