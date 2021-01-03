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

import de.coreengine.animation.Joint;
import de.coreengine.asset.AssetDatabase;
import de.coreengine.asset.FileLoader;
import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.renderable.AnimatedEntity;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Shader for the animated entity renderer
 *
 * @author Darius Dinger
 */
public class AnimatedEntityShader extends Shader {
    private static final int MAX_JOINTS = 50;

    private final int diffuseMapUnit = 0, normalMapUnit = 1, specularMapUnit = 2, displacementMapUnit = 3,
            aoMapUnit = 4, glowMapUnit = 5;

    private int vpMatLoc, transMatLoc, tilingLoc, camPosLoc, displacementFactorLoc, reflectivityLoc, shineDamperLoc,
            diffuseColorLoc, pickingColorLoc, glowColorLoc, clipPlaneLoc, jointMatLoc;

    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "animatedEntity.vert", true), GL20.GL_VERTEX_SHADER,
                "AnimatedEntity Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "animatedEntity.frag", true),
                GL20.GL_FRAGMENT_SHADER, "AnimatedEntity Fragment Shader");
    }

    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
        bindAttribute(1, "texCoord");
        bindAttribute(2, "normal");
        bindAttribute(3, "tangent");
        bindAttribute(4, "joints");
        bindAttribute(5, "weights");
    }

    @Override
    protected void loadUniforms() {
        vpMatLoc = getUniformLocation("vpMat");
        transMatLoc = getUniformLocation("transMat");
        tilingLoc = getUniformLocation("tiling");
        camPosLoc = getUniformLocation("camPos");
        displacementFactorLoc = getUniformLocation("displacementFactor");
        reflectivityLoc = getUniformLocation("shininess");
        shineDamperLoc = getUniformLocation("shineDamper");
        diffuseColorLoc = getUniformLocation("diffuseColor");
        pickingColorLoc = getUniformLocation("pickingColor");
        glowColorLoc = getUniformLocation("glowColor");
        clipPlaneLoc = getUniformLocation("clipPlane");
        jointMatLoc = getUniformLocation("jointMat");

        bindTextureUnit("diffuseMap", diffuseMapUnit);
        bindTextureUnit("normalMap", normalMapUnit);
        bindTextureUnit("specularMap", specularMapUnit);
        bindTextureUnit("displacementMap", displacementMapUnit);
        bindTextureUnit("aoMap", aoMapUnit);
        bindTextureUnit("glowMap", glowMapUnit);
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
     * @param cam camera to render next models from
     */
    public void setCamera(Camera cam) {
        setUniform(vpMatLoc, Toolbox.matrixToFloatArray(cam.getViewProjectionMatrix()));
        setUniform(camPosLoc, cam.getPosition().x, cam.getPosition().y, cam.getPosition().z);
    }

    /**
     * Preparing an animated entity
     *
     * @param entity Animated entity to prepare
     */
    public void prepareEntity(AnimatedEntity entity) {
        setUniform(transMatLoc, entity.getTransform().getTransMatArr());
        prepareSkeleton(entity.getSkeleton());
        setUniform(pickingColorLoc, entity.getPickColor());
    }

    /**
     * Preparing skeleton of an animated entity
     *
     * @param skeleton Skeleton to prepare
     */
    private void prepareSkeleton(Joint skeleton) {
        float[] matrices = new float[MAX_JOINTS * 16];
        addJointMatrices(skeleton, matrices);
        setUniform(jointMatLoc, matrices);
    }

    /**
     * Adding this joints matrices and all children joint matrices to the matrices
     * float array at their specific position
     *
     * @param joint    Joint to add matrices from
     * @param matrices Array to fill in matrices
     */
    private void addJointMatrices(Joint joint, float[] matrices) {

        // Check if joint fits into array
        if (joint.getIndex() >= 0 || joint.getIndex() < MAX_JOINTS) {

            // Fill up matrix
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    float val = joint.getAnimatedTransform().getElement(c, r);
                    matrices[(joint.getIndex() * 16) + (r * 4) + c] = val;
                }
            }
        }

        // Fill up children matrices
        for (Joint child : joint.getChildren()) {
            addJointMatrices(child, matrices);
        }
    }

    /**
     * Preparing shader for next material
     *
     * @param mat Material to prepare
     */
    public void prepareMaterial(Material mat) {
        setUniform(tilingLoc, mat.tiling);
        setUniform(diffuseColorLoc, mat.diffuseColor);
        setUniform(displacementFactorLoc, mat.displacementFactor);
        setUniform(reflectivityLoc, mat.shininess);
        setUniform(shineDamperLoc, mat.shineDamping);
        setUniform(glowColorLoc, mat.glowColor);

        bindTexture(AssetDatabase.getTexture(mat.diffuseMap), diffuseMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(AssetDatabase.getTexture(mat.normalMap), normalMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(AssetDatabase.getTexture(mat.specularMap), specularMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(AssetDatabase.getTexture(mat.ambientOcclusionMap), aoMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(AssetDatabase.getTexture(mat.displacementMap), displacementMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(AssetDatabase.getTexture(mat.glowMap), glowMapUnit, GL11.GL_TEXTURE_2D);
    }
}
