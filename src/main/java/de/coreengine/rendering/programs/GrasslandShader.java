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
import de.coreengine.rendering.renderable.terrain.Terrain;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**Shader for a grassland renderer
 *
 * @author Darius Dinger
 */
public class GrasslandShader extends Shader{
    
    private final int bladesTextureUnit = 0, heightMapUnit = 1, lightMapUnit = 2, 
            densityMapUnit = 3, windMapUnit = 4;
    
    //Uniform locations
    private int amplitudeLoc, mMatTerrLoc, vpMat, scaleLoc, camPosLoc, tuftCount,
            tuftDistanceLoc, bladesColorLoc, areaLoc, windOffsetLoc, windMapTiling, 
            windIntensityLoc;
    
    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "grassland.vert", true),
                GL20.GL_VERTEX_SHADER, "Grassland Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "grassland.frag", true), 
                GL20.GL_FRAGMENT_SHADER, "Grassland Fragment Shader");
    }
    
    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
        bindAttribute(1, "texCoord");
    }
    
    @Override
    protected void loadUniforms() {
        amplitudeLoc = getUniformLocation("amplitude");
        mMatTerrLoc = getUniformLocation("mMatTerr");
        vpMat = getUniformLocation("vpMat");
        scaleLoc = getUniformLocation("scale");
        camPosLoc = getUniformLocation("camPos");
        tuftDistanceLoc = getUniformLocation("tuftDistance");
        bladesColorLoc = getUniformLocation("bladesColor");
        areaLoc = getUniformLocation("area");
        windOffsetLoc = getUniformLocation("windOffset");
        windMapTiling = getUniformLocation("windMapTiling");
        windIntensityLoc = getUniformLocation("windIntensity");
        tuftCount = getUniformLocation("tuftCount");
        
        bindTextureUnit("bladesTexture", bladesTextureUnit);
        bindTextureUnit("heightMap", heightMapUnit);
        bindTextureUnit("lightMap", lightMapUnit);
        bindTextureUnit("densityMap", densityMapUnit);
        bindTextureUnit("windMap", windMapUnit);
    }
    
    /**Setting camera for next terrain
     * 
     * @param cam Next camera
     */
    public void setcamera(Camera cam){
        setUniform(vpMat, Toolbox.matrixToFloatArray(cam.getViewProjectionMatrix()));
        setUniform(camPosLoc, cam.getPosition().x, cam.getPosition().y,
                cam.getPosition().z);
    }
    
    /**Preparing shader for next terrain
     * 
     * @param terrain Next terrain
     */
    public void prepareTerrain(Terrain terrain){
        bindTexture(terrain.getGrassland().getMesh().getMaterial().diffuseMap, 
                bladesTextureUnit, GL11.GL_TEXTURE_2D);
        bindTexture(terrain.getGrassland().getDensityMap(), 
                densityMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(terrain.getGrassland().getWindMap(), 
                windMapUnit, GL11.GL_TEXTURE_2D);
        
        setUniform(bladesColorLoc, terrain.getGrassland().getMesh().
                getMaterial().diffuseColor);
        setUniform(tuftDistanceLoc, terrain.getGrassland().getDistance());
        setUniform(areaLoc, terrain.getGrassland().getArea().x,
                terrain.getGrassland().getArea().y);
        setUniform(windOffsetLoc, terrain.getGrassland().getWindOffset());
        setUniform(windIntensityLoc, terrain.getGrassland().getWindIntensitivity());
        setUniform(windMapTiling, terrain.getGrassland().getWindMapTiling());
        setUniform(scaleLoc, terrain.getGrassland().getTuftScale());
        setUniform(tuftCount, terrain.getGrassland().getDensity());
        
        bindTexture(terrain.getConfig().getHeightMap().getGlTexture(), 
                heightMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(terrain.getConfig().getLightMap(), 
                lightMapUnit, GL11.GL_TEXTURE_2D);
        
        setUniform(amplitudeLoc, terrain.getConfig().getAmplitude());
        setUniform(mMatTerrLoc, Toolbox.matrixToFloatArray(terrain.getTransMat()));
    }
}
