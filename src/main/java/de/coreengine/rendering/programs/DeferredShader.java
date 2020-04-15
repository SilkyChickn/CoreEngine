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
import de.coreengine.rendering.GBuffer;
import de.coreengine.rendering.renderable.light.*;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Vector3f;
import java.util.List;

/**
 * Representing the shader for the deferred light rendering with a gbuffer
 *
 * @author Darius Dinger
 */
public class DeferredShader extends Shader {
    private static final int MAX_LIGHTS = 25;

    private final int colorBufferUnit = 0, positionBufferUnit = 1, normalBufferUnit = 2, variable0BufferUnit = 3,
            variable1BufferUnit = 4, shadowMapUnit = 5;

    private int camPosLoc, alColorsLoc, alIntensitiesLoc, dlColorsLoc, dlIntensitiesLoc, dlDirectionsLoc, plColorsLoc,
            plIntensitiesLoc, plPositionsLoc, plAttenuationsLoc, slColorsLoc, slIntensitiesLoc, slPositionsLoc,
            slAttenuationsLoc, slDirectionsLoc, slLightConesLoc, alCountLoc, slCountLoc, dlCountLoc, plCountLoc,
            toShadowMapSpaceLoc, enableShadowsLoc;

    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "light.vert", true), GL20.GL_VERTEX_SHADER,
                "Light Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "light.frag", true), GL20.GL_FRAGMENT_SHADER,
                "Light Fragment Shader");
    }

    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
    }

    @Override
    protected void loadUniforms() {

        bindTextureUnit("colorBuffer", colorBufferUnit);
        bindTextureUnit("positionBuffer", positionBufferUnit);
        bindTextureUnit("normalBuffer", normalBufferUnit);
        bindTextureUnit("variable0Buffer", variable0BufferUnit);
        bindTextureUnit("variable1Buffer", variable1BufferUnit);
        bindTextureUnit("shadowMap", shadowMapUnit);

        toShadowMapSpaceLoc = getUniformLocation("toShadowMapSpace");
        enableShadowsLoc = getUniformLocation("enableShadows");

        camPosLoc = getUniformLocation("camPos");

        alColorsLoc = getUniformLocation("alColors");
        alIntensitiesLoc = getUniformLocation("alIntensities");
        alCountLoc = getUniformLocation("alCount");

        dlColorsLoc = getUniformLocation("dlColors");
        dlIntensitiesLoc = getUniformLocation("dlIntensities");
        dlDirectionsLoc = getUniformLocation("dlDirections");
        dlCountLoc = getUniformLocation("dlCount");

        plColorsLoc = getUniformLocation("plColors");
        plIntensitiesLoc = getUniformLocation("plIntensities");
        plPositionsLoc = getUniformLocation("plPositions");
        plAttenuationsLoc = getUniformLocation("plAttenuations");
        plCountLoc = getUniformLocation("plCount");

        slColorsLoc = getUniformLocation("slColors");
        slIntensitiesLoc = getUniformLocation("slIntensities");
        slPositionsLoc = getUniformLocation("slPositions");
        slAttenuationsLoc = getUniformLocation("slAttenuations");
        slDirectionsLoc = getUniformLocation("slDirections");
        slLightConesLoc = getUniformLocation("slLightCones");
        slCountLoc = getUniformLocation("slCount");
    }

    /**
     * Setting the position, the specular light calculation is using for calculating
     * the reflections
     * 
     * @param camPos Position of the camera/player
     */
    public void setCameraPosition(Vector3f camPos) {
        setUniform(camPosLoc, camPos.x, camPos.y, camPos.z);
    }

    /**
     * Setting gBuffer maps to render lights into in the next frame
     * 
     * @param gBuffer GBuffer to get maps from
     */
    public void setGBuffer(GBuffer gBuffer) {
        bindTexture(gBuffer.getColorBuffer(), colorBufferUnit, GL11.GL_TEXTURE_2D);
        bindTexture(gBuffer.getPositionBuffer(), positionBufferUnit, GL11.GL_TEXTURE_2D);
        bindTexture(gBuffer.getNormalBuffer(), normalBufferUnit, GL11.GL_TEXTURE_2D);
        bindTexture(gBuffer.getVariable0Buffer(), variable0BufferUnit, GL11.GL_TEXTURE_2D);
        bindTexture(gBuffer.getVariable1Buffer(), variable1BufferUnit, GL11.GL_TEXTURE_2D);
    }

    /**
     * Set shadow light to render shadows from in next frame
     *
     * @param light Next shadow light
     */
    public void setShadowLight(ShadowLight light) {

        if (light != null) {
            bindTexture(light.getShadowMap().getDepthAttachment(), shadowMapUnit, GL11.GL_TEXTURE_2D);
            setUniform(toShadowMapSpaceLoc, Toolbox.matrixToFloatArray(light.getVpMat()));
        }

        setUniform(enableShadowsLoc, light != null);
    }

    /**
     * Setting light sources for the next frame
     * 
     * @param pointLights       To render point lights
     * @param spotLights        To render spot lights
     * @param ambientLights     To render ambient lights
     * @param directionalLights To render directional lights
     */
    public void setLightSources(List<PointLight> pointLights, List<SpotLight> spotLights,
            List<AmbientLight> ambientLights, List<DirectionalLight> directionalLights) {

        setAmbientLights(ambientLights);
        setDirectionalLights(directionalLights);
        setPointLights(pointLights);
        setSpotLights(spotLights);
    }

    /**
     * Setting ambient light sources for the next frame
     * 
     * @param ambientLights To render ambient lights
     */
    private void setAmbientLights(List<AmbientLight> ambientLights) {
        int alCount = Integer.min(ambientLights.size(), MAX_LIGHTS);
        setUniform(alCountLoc, alCount);
        if (alCount == 0)
            return;

        float[] colors = new float[alCount * 3];
        float[] intensities = new float[alCount];

        for (int i = 0; i < alCount; i++) {
            colors[i * 3] = ambientLights.get(i).getColor().getRed();
            colors[i * 3 + 1] = ambientLights.get(i).getColor().getGreen();
            colors[i * 3 + 2] = ambientLights.get(i).getColor().getBlue();

            intensities[i] = ambientLights.get(i).getIntensity();
        }

        setUniformArray3f(alColorsLoc, colors);
        setUniformArray1f(alIntensitiesLoc, intensities);
    }

    /**
     * Setting directional light sources for the next frame
     * 
     * @param directionalLights To render directional lights
     */
    private void setDirectionalLights(List<DirectionalLight> directionalLights) {
        int dlCount = Integer.min(directionalLights.size(), MAX_LIGHTS);
        setUniform(dlCountLoc, dlCount);
        if (dlCount == 0)
            return;

        float[] colors = new float[dlCount * 3];
        float[] intensities = new float[dlCount];
        float[] directions = new float[dlCount * 3];

        for (int i = 0; i < dlCount; i++) {
            colors[i * 3] = directionalLights.get(i).getColor().getRed();
            colors[i * 3 + 1] = directionalLights.get(i).getColor().getGreen();
            colors[i * 3 + 2] = directionalLights.get(i).getColor().getBlue();

            intensities[i] = directionalLights.get(i).getIntensity();

            directions[i * 3] = directionalLights.get(i).getDirection().x;
            directions[i * 3 + 1] = directionalLights.get(i).getDirection().y;
            directions[i * 3 + 2] = directionalLights.get(i).getDirection().z;
        }

        setUniformArray3f(dlColorsLoc, colors);
        setUniformArray1f(dlIntensitiesLoc, intensities);
        setUniformArray3f(dlDirectionsLoc, directions);
    }

    /**
     * Setting point light sources for the next frame
     * 
     * @param pointLights To render point lights
     */
    private void setPointLights(List<PointLight> pointLights) {
        int plCount = Integer.min(pointLights.size(), MAX_LIGHTS);
        setUniform(plCountLoc, plCount);
        if (plCount == 0)
            return;

        float[] colors = new float[plCount * 3];
        float[] intensities = new float[plCount];
        float[] positions = new float[plCount * 3];
        float[] attenuations = new float[plCount * 2];

        for (int i = 0; i < plCount; i++) {
            colors[i * 3] = pointLights.get(i).getColor().getRed();
            colors[i * 3 + 1] = pointLights.get(i).getColor().getGreen();
            colors[i * 3 + 2] = pointLights.get(i).getColor().getBlue();

            intensities[i] = pointLights.get(i).getIntensity();

            positions[i * 3] = pointLights.get(i).getPosition().x;
            positions[i * 3 + 1] = pointLights.get(i).getPosition().y;
            positions[i * 3 + 2] = pointLights.get(i).getPosition().z;

            attenuations[i * 2] = pointLights.get(i).getAttenuation().x;
            attenuations[i * 2 + 1] = pointLights.get(i).getAttenuation().y;
        }

        setUniformArray3f(plColorsLoc, colors);
        setUniformArray1f(plIntensitiesLoc, intensities);
        setUniformArray3f(plPositionsLoc, positions);
        setUniformArray2f(plAttenuationsLoc, attenuations);
    }

    /**
     * Setting spot light sources for the next frame
     * 
     * @param spotLights To render spot lights
     */
    private void setSpotLights(List<SpotLight> spotLights) {
        int slCount = Integer.min(spotLights.size(), MAX_LIGHTS);
        setUniform(slCountLoc, slCount);
        if (slCount == 0)
            return;

        float[] colors = new float[slCount * 3];
        float[] intensities = new float[slCount];
        float[] positions = new float[slCount * 3];
        float[] attenuations = new float[slCount * 2];
        float[] directions = new float[slCount * 3];
        float[] lightCones = new float[slCount * 2];

        for (int i = 0; i < slCount; i++) {
            colors[i * 3] = spotLights.get(i).getColor().getRed();
            colors[i * 3 + 1] = spotLights.get(i).getColor().getGreen();
            colors[i * 3 + 2] = spotLights.get(i).getColor().getBlue();

            intensities[i] = spotLights.get(i).getIntensity();

            positions[i * 3] = spotLights.get(i).getPosition().x;
            positions[i * 3 + 1] = spotLights.get(i).getPosition().y;
            positions[i * 3 + 2] = spotLights.get(i).getPosition().z;

            attenuations[i * 2] = spotLights.get(i).getAttenuation().x;
            attenuations[i * 2 + 1] = spotLights.get(i).getAttenuation().y;

            directions[i * 3] = spotLights.get(i).getDirection().x;
            directions[i * 3 + 1] = spotLights.get(i).getDirection().y;
            directions[i * 3 + 2] = spotLights.get(i).getDirection().z;

            lightCones[i * 2] = spotLights.get(i).getLightCone().x;
            lightCones[i * 2 + 1] = spotLights.get(i).getLightCone().y;
        }

        setUniformArray3f(slColorsLoc, colors);
        setUniformArray1f(slIntensitiesLoc, intensities);
        setUniformArray3f(slPositionsLoc, positions);
        setUniformArray2f(slAttenuationsLoc, attenuations);
        setUniformArray3f(slDirectionsLoc, directions);
        setUniformArray2f(slLightConesLoc, lightCones);
    }
}
