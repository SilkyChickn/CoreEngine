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
package io.github.suuirad.coreengine.rendering.programs;

import io.github.suuirad.coreengine.asset.FileLoader;
import io.github.suuirad.coreengine.rendering.renderable.terrain.TerrainConfig;
import io.github.suuirad.coreengine.rendering.renderable.terrain.TerrainTexturePack;
import io.github.suuirad.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**Class that represent a shader programm for the terrain shader pipeline
 *
 * @author Darius Dinger
 */
public class TerrainShader extends Shader{
    private final int blendMapUnit = 1, heightMapUnit = 2, lightMapUnit = 3, 
            diffuseUnit = 4, diffuseRUnit = 5, diffuseGUnit = 6, diffuseBUnit = 7,
            normalUnit = 8, normalRUnit = 9, normalGUnit = 10, normalBUnit = 11,
            aoUnit = 12, aoRUnit = 13, aoGUnit = 14, aoBUnit = 15,
            specularUnit = 16, specularRUnit = 17, specularGUnit = 18, specularBUnit = 19,
            displacementUnit = 20, displacementRUnit = 21, displacementGUnit = 22, displacementBUnit = 23;
    
    //Uniform locations
    private int chunkSizeLoc, chunkOffsetLoc, vpMatLoc, mMatLoc, camPosLoc,
            amplitudeLoc, tessAreaLoc, clipPlaneLoc,
            diffuseLoc, diffuseRLoc, diffuseGLoc, diffuseBLoc,
            tilingLoc, tilingRLoc, tilingGLoc, tilingBLoc,
            specularLoc, specularRLoc, specularGLoc, specularBLoc,
            displacementLoc, displacementRLoc, displacementGLoc, displacementBLoc;
    
    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "terrain.vert", true),
                GL20.GL_VERTEX_SHADER, "Terrain Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "terrain.tcs", true), 
                GL40.GL_TESS_CONTROL_SHADER, "Terrain Tesselation Control Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "terrain.tes", true), 
                GL40.GL_TESS_EVALUATION_SHADER, "Terrain Tesselation Evaluation Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "terrain.geo", true), 
                GL40.GL_GEOMETRY_SHADER, "Terrain Geometry Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "terrain.frag", true), 
                GL20.GL_FRAGMENT_SHADER, "Terrain Fragment Shader");
    }
    
    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
    }
    
    @Override
    protected void loadUniforms() {
        chunkSizeLoc = getUniformLocation("chunkSize");
        chunkOffsetLoc = getUniformLocation("chunkOffset");
        
        vpMatLoc = getUniformLocation("vpMat");
        mMatLoc = getUniformLocation("mMat");
        
        camPosLoc = getUniformLocation("camPos");
        
        amplitudeLoc = getUniformLocation("amplitude");
        
        tessAreaLoc = getUniformLocation("tessArea");
        
        clipPlaneLoc = getUniformLocation("clipPlane");
        
        tilingLoc = getUniformLocation("tiling");
        tilingRLoc = getUniformLocation("tilingR");
        tilingGLoc = getUniformLocation("tilingG");
        tilingBLoc = getUniformLocation("tilingB");
        
        diffuseLoc = getUniformLocation("diffuse");
        diffuseRLoc = getUniformLocation("diffuseR");
        diffuseGLoc = getUniformLocation("diffuseG");
        diffuseBLoc = getUniformLocation("diffuseB");
        
        specularLoc = getUniformLocation("specular");
        specularRLoc = getUniformLocation("specularR");
        specularGLoc = getUniformLocation("specularG");
        specularBLoc = getUniformLocation("specularB");
        
        displacementLoc = getUniformLocation("displacement");
        displacementRLoc = getUniformLocation("displacementR");
        displacementGLoc = getUniformLocation("displacementG");
        displacementBLoc = getUniformLocation("displacementB");
        
        bindTextureUnit("blendMap", blendMapUnit);
        bindTextureUnit("heightMap", heightMapUnit);
        bindTextureUnit("lightMap", lightMapUnit);
        
        bindTextureUnit("diffuseTexture", diffuseUnit);
        bindTextureUnit("diffuseRTexture", diffuseRUnit);
        bindTextureUnit("diffuseGTexture", diffuseGUnit);
        bindTextureUnit("diffuseBTexture", diffuseBUnit);
        
        bindTextureUnit("aoTexture", aoUnit);
        bindTextureUnit("aoRTexture", aoRUnit);
        bindTextureUnit("aoGTexture", aoGUnit);
        bindTextureUnit("aoBTexture", aoBUnit);
        
        bindTextureUnit("specularTexture", specularUnit);
        bindTextureUnit("specularRTexture", specularRUnit);
        bindTextureUnit("specularGTexture", specularGUnit);
        bindTextureUnit("specularBTexture", specularBUnit);
        
        bindTextureUnit("displacementTexture", displacementUnit);
        bindTextureUnit("displacementRTexture", displacementRUnit);
        bindTextureUnit("displacementGTexture", displacementGUnit);
        bindTextureUnit("displacementBTexture", displacementBUnit);
        
        bindTextureUnit("normalTexture", normalUnit);
        bindTextureUnit("normalRTexture", normalRUnit);
        bindTextureUnit("normalGTexture", normalGUnit);
        bindTextureUnit("normalBTexture", normalBUnit);
    }
    
    /**Loading a whole terrain configuration into the shader
     * 
     * @param config Config for the next terrain
     */
    public void setTerrainConfig(TerrainConfig config){
        bindTexture(config.getBlendMap(), blendMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(config.getHeightMap().getGlTexture(), heightMapUnit, GL11.GL_TEXTURE_2D);
        bindTexture(config.getLightMap(), lightMapUnit, GL11.GL_TEXTURE_2D);
        
        setUniform(amplitudeLoc, config.getAmplitude());
        setUniform(tessAreaLoc, config.getTesselationArea().x, 
                config.getTesselationArea().y, config.getTesselationArea().z);
        
        setTexturePack(config.getTexturePack());
    }
    
    /**Setting textures for next terrain
     * 
     * @param pack Terrain texture pack to load
     */
    private void setTexturePack(TerrainTexturePack pack){
        setUniform(tilingLoc, pack.getMaterial().tiling);
        setUniform(tilingRLoc, pack.getRedMaterial().tiling);
        setUniform(tilingGLoc, pack.getGreenMaterial().tiling);
        setUniform(tilingBLoc, pack.getBlueMaterial().tiling);
        
        setUniform(diffuseLoc, pack.getMaterial().diffuseColor);
        setUniform(diffuseRLoc, pack.getRedMaterial().diffuseColor);
        setUniform(diffuseGLoc, pack.getGreenMaterial().diffuseColor);
        setUniform(diffuseBLoc, pack.getBlueMaterial().diffuseColor);
        
        setUniform(displacementLoc, pack.getMaterial().displacementFactor);
        setUniform(displacementRLoc, pack.getRedMaterial().displacementFactor);
        setUniform(displacementGLoc, pack.getGreenMaterial().displacementFactor);
        setUniform(displacementBLoc, pack.getBlueMaterial().displacementFactor);
        
        setUniform(specularLoc, pack.getMaterial().reflectivity, pack.getMaterial().shineDamping);
        setUniform(specularRLoc, pack.getRedMaterial().reflectivity, pack.getRedMaterial().shineDamping);
        setUniform(specularGLoc, pack.getGreenMaterial().reflectivity, pack.getGreenMaterial().shineDamping);
        setUniform(specularBLoc, pack.getBlueMaterial().reflectivity, pack.getBlueMaterial().shineDamping);
        
        bindTexture(pack.getMaterial().diffuseMap, diffuseUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getRedMaterial().diffuseMap, diffuseRUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getGreenMaterial().diffuseMap, diffuseGUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getBlueMaterial().diffuseMap, diffuseBUnit, GL11.GL_TEXTURE_2D);
        
        bindTexture(pack.getMaterial().specularMap, specularUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getRedMaterial().specularMap, specularRUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getGreenMaterial().specularMap, specularGUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getBlueMaterial().specularMap, specularBUnit, GL11.GL_TEXTURE_2D);
        
        bindTexture(pack.getMaterial().ambientOcclusionMap, aoUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getRedMaterial().ambientOcclusionMap, aoRUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getGreenMaterial().ambientOcclusionMap, aoGUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getBlueMaterial().ambientOcclusionMap, aoBUnit, GL11.GL_TEXTURE_2D);
        
        bindTexture(pack.getMaterial().displacementMap, displacementUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getRedMaterial().displacementMap, displacementRUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getGreenMaterial().displacementMap, displacementGUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getBlueMaterial().displacementMap, displacementBUnit, GL11.GL_TEXTURE_2D);
        
        bindTexture(pack.getMaterial().normalMap, normalUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getRedMaterial().normalMap, normalRUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getGreenMaterial().normalMap, normalGUnit, GL11.GL_TEXTURE_2D);
        bindTexture(pack.getBlueMaterial().normalMap, normalBUnit, GL11.GL_TEXTURE_2D);
    }
    
    /**Setting clip plane for next terrain
     * 
     * @param x X value of the plane normal
     * @param y Y value of the plane normal
     * @param z Z value of the plane normal
     * @param w Distance of the plane normal
     */
    public void setClipPlane(float x, float y, float z, float w){
        setUniform(clipPlaneLoc, x, y, z, w);
    }
    
    /**Loading the transformation matrix of the terrain into the shader
     * 
     * @param transMat Terrains transformation matrix
     */
    public void setTerrainTransform(Matrix4f transMat){
        setUniform(mMatLoc, Toolbox.matrixToFloatArray(transMat));
    }
    
    /**Loading the position/offset and size of the next chunk into the shader
     * 
     * @param offset Offset for the next chunk
     * @param size Size of the next chunks
     */
    public void setChunkData(Vector2f offset, float size){
        setUniform(chunkOffsetLoc, offset.x, offset.y);
        setUniform(chunkSizeLoc, size);
    }
    
    /**Setting view projection matrix to use
     * 
     * @param vpMat New view projection matrix
     */
    public void setViewProjectionMatrix(Matrix4f vpMat){
        setUniform(vpMatLoc, Toolbox.matrixToFloatArray(vpMat));
    }
    
    /**Setting camera position used for tesselation
     * 
     * @param camPos Camera position to use
     */
    public void setCameraPosition(Vector3f camPos){
        setUniform(camPosLoc, camPos.x, camPos.y, camPos.z);
    }
}
