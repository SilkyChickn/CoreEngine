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
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderable.Entity;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Shader for the object renderer
 * 
 * Default simple entity shader. Supports diffuse map and diffusecolor, tiling,
 * reflectivity and shine damping, picking and simple glow color.
 *
 * @author Darius Dinger
 */
public class EntityShader extends Shader {

    protected int diffuseMapUnit = 0;

    protected int vpMatLoc, transMatLoc, tilingLoc, camPosLoc, reflectivityLoc, shineDamperLoc,
            diffuseColorLoc, pickingColorLoc, clipPlaneLoc, glowColorLoc;

    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "entity/entity.vert", true), GL20.GL_VERTEX_SHADER,
                "Entity Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "entity/entity.frag", true), GL20.GL_FRAGMENT_SHADER,
                "Entity Fragment Shader");
    }

    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
        bindAttribute(1, "texCoord");
        bindAttribute(2, "normal");
    }

    @Override
    protected void loadUniforms() {
        vpMatLoc = getUniformLocation("vpMat");
        transMatLoc = getUniformLocation("transMat");
        tilingLoc = getUniformLocation("tiling");
        camPosLoc = getUniformLocation("camPos");
        reflectivityLoc = getUniformLocation("shininess");
        shineDamperLoc = getUniformLocation("shineDamper");
        diffuseColorLoc = getUniformLocation("diffuseColor");
        pickingColorLoc = getUniformLocation("pickingColor");
        clipPlaneLoc = getUniformLocation("clipPlane");
        glowColorLoc = getUniformLocation("glowColor");

        bindTextureUnit("diffuseMap", diffuseMapUnit);
    }

    /**
     * Setting clip plane for next entity
     * 
     * @param x X value of the plane normal
     * @param y Y value of the plane normal
     * @param z Z value of the plane normal
     * @param w Distance of the plane normal
     */
    public void setClipPlane(float x, float y, float z, float w) {
        setUniform(clipPlaneLoc, x, y, z, w);
    }

    /**
     * @param cam           Camera to render next models from
     * @param rotateWithCam Should the entity rotate and move with the camera
     */
    public void setCamera(Camera cam, boolean rotateWithCam) {
        if (rotateWithCam)
            setUniform(vpMatLoc, Toolbox.matrixToFloatArray(cam.getProjectionMatrix()));
        else
            setUniform(vpMatLoc, Toolbox.matrixToFloatArray(cam.getViewProjectionMatrix()));
        setUniform(camPosLoc, cam.getPosition().x, cam.getPosition().y, cam.getPosition().z);
    }

    public void prepareEntity(Entity entity) {
        setUniform(transMatLoc, entity.getTransform().getTransMatArr());
        setUniform(pickingColorLoc, entity.getPickColor());
    }

    /**
     * Preparing shader for next material
     * 
     * @param mat Material to prepare
     */
    public void prepareMaterial(Material mat) {
        setUniform(tilingLoc, mat.tiling);
        setUniform(diffuseColorLoc, mat.diffuseColor);
        setUniform(reflectivityLoc, mat.shininess);
        setUniform(shineDamperLoc, mat.shineDamping);
        setUniform(glowColorLoc, mat.glowColor);

        bindTexture(AssetDatabase.getTexture(mat.diffuseMap), diffuseMapUnit, GL11.GL_TEXTURE_2D);
    }
}
