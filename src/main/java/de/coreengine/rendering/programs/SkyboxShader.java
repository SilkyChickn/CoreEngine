/*
 * Copyright (c) 2019, Darius Dinger
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.coreengine.rendering.programs;

import de.coreengine.asset.FileLoader;
import de.coreengine.rendering.renderable.Skybox;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

/**Shader for the skybox renderer
 *
 * @author Darius Dinger
 */
public class SkyboxShader extends Shader{
    public static final int MAX_CUBE_MAPS = 10;
    
    private int blendingFactorsLoc, vpMatLoc, transMatLoc, sizeLoc, camPosLoc ,
            cubeMapCountLoc, cubeMapTexturesLoc;
    
    @Override
    protected void addShaders() {
            addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "skybox.vert", true), 
                    GL20.GL_VERTEX_SHADER, "Skybox Vertex Shader");
            addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "skybox.frag", true), 
                    GL20.GL_FRAGMENT_SHADER, "Skybox Fragment Shader");
    }
    
    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
    }
    
    @Override
    protected void loadUniforms() {
        sizeLoc = getUniformLocation("size");
        vpMatLoc = getUniformLocation("vpMat");
        transMatLoc = getUniformLocation("transMat");
        camPosLoc = getUniformLocation("camPos");
        blendingFactorsLoc = getUniformLocation("blendingFactors");
        cubeMapCountLoc = getUniformLocation("cubeMapCount");
        cubeMapTexturesLoc = getUniformLocation("cubeMapTextures");
    }
    
    /**Setting thescaling of the skybox
     * 
     * @param size New scaling
     */
    public void setSkyboxSize(float size){
        setUniform(sizeLoc, size);
    }
    
    /**Setting the camera to render next skyboxes from
     * 
     * @param cam Camera to set
     */
    public void setCamera(Camera cam){
        setUniform(vpMatLoc, Toolbox.matrixToFloatArray(cam.getViewProjectionMatrix()));
        setUniform(camPosLoc, cam.getPosition().x, cam.getPosition().y, cam.getPosition().z);
    }
    
    /**Preparing next skybox
     * 
     * @param skybox Next skybox to render
     */
    public void prepareSkybox(Skybox skybox){
        
        //Cap cube map count at limit
        int cubeMapCount = Integer.min(MAX_CUBE_MAPS, skybox.getCubeMapTextures().length);
        
        //Preparing array
        float[] blendFactors = new float[cubeMapCount];
        int[] cubeMapTextureUnits = new int[cubeMapCount];
        
        //Fill array and loading textures
        for(int i = 0; i < cubeMapCount; i++){
            blendFactors[i] = skybox.getBlendingFactors()[i];
            
            //Load texture
            cubeMapTextureUnits[i] = i;
            bindTexture(skybox.getCubeMapTextures()[i], i, GL13.GL_TEXTURE_CUBE_MAP);
        }
        
        //Load arrays and skybox stuff
        setUniform(cubeMapCountLoc, cubeMapCount);
        setUniformArray1i(cubeMapTexturesLoc, cubeMapTextureUnits);
        setUniformArray1f(blendingFactorsLoc, blendFactors);
        setUniform(transMatLoc, Toolbox.matrixToFloatArray(skybox.getTransMat()));
    }
}
