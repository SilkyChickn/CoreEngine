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
package de.coreengine.rendering.programs.entity;

import de.coreengine.asset.AssetDatabase;
import de.coreengine.asset.FileLoader;
import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.programs.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Shader for the object renderer
 * 
 * More advanced entity shader. Supports normal-, specular-, displacement-,
 * ambientOcclusuion- and glowMapping.
 *
 * @author Darius Dinger
 */
public class EntityShaderAdvanced extends EntityShader {

    private final int normalMapUnit = 1, specularMapUnit = 2, displacementMapUnit = 3,
            aoMapUnit = 4, glowMapUnit = 5;

    private int displacementFactorLoc;

    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "entity/entityAdvanced.vert", true),
                GL20.GL_VERTEX_SHADER,
                "Advanced Entity Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "entity/entityAdvanced.frag", true),
                GL20.GL_FRAGMENT_SHADER,
                "Advanced Entity Fragment Shader");
    }

    @Override
    protected void bindAttribs() {
        super.bindAttribs();
        bindAttribute(3, "tangent");
    }

    @Override
    protected void loadUniforms() {
        super.loadUniforms();

        displacementFactorLoc = getUniformLocation("displacementFactor");

        bindTextureUnit("normalMap", normalMapUnit);
        bindTextureUnit("specularMap", specularMapUnit);
        bindTextureUnit("displacementMap", displacementMapUnit);
        bindTextureUnit("aoMap", aoMapUnit);
        bindTextureUnit("glowMap", glowMapUnit);
    }

    /**
     * Preparing shader for next material
     * 
     * @param mat Material to prepare
     */
    public void prepareMaterial(Material mat) {
        setUniform(displacementFactorLoc, mat.displacementFactor);

        bindTexture(AssetDatabase.getTexture(mat.normalMap), normalMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(AssetDatabase.getTexture(mat.specularMap), specularMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(AssetDatabase.getTexture(mat.ambientOcclusionMap), aoMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(AssetDatabase.getTexture(mat.displacementMap), displacementMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(AssetDatabase.getTexture(mat.glowMap), glowMapUnit, GL11.GL_TEXTURE_2D);
    }
}
