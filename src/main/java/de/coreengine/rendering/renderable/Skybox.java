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
package de.coreengine.rendering.renderable;

import de.coreengine.util.Toolbox;
import javax.vecmath.Matrix4f;

/**Class that represents a skybox
 *
 * @author Darius Dinger
 */
public class Skybox {
    
    //Skybox cube map textures and blendings
    private int[] cubeMapTextures = new int[0];
    private float[] blendingFactors = new float[0];
    
    //Current rotation ind degrees and rotation matrix
    private float rotation = 0.0f;
    private Matrix4f transMat = new Matrix4f();
    
    /**Creating new skybox, init variables
     */
    public Skybox() {
        transMat.setIdentity();
    }
    
    /**@param rotation New rotation of the skybox in degrees
     */
    public void setRotation(float rotation) {
        if(rotation != this.rotation){
            transMat.rotY((float) Math.toRadians(rotation));
            this.rotation = rotation;
        }
    }
    
    /**Setting cube map textures and its initial blending factors.<br>
     * The factor represents the visibility of the cube map texture. 0.0f is not
     * visible and 1.0f is full visible. The blending factor will be multiplied
     * by the texture color.<br>
     * The cubeMapTextures must have the same length as the blendingfactors, else
     * it would not be changed.
     * 
     * @param cubeMapTextures Cube map textures
     * @param blendingFactors Initial blending factors
     */
    public void setCubeMapTextures(int[] cubeMapTextures, float[] blendingFactors) {
        if(cubeMapTextures.length == blendingFactors.length){
            this.cubeMapTextures = cubeMapTextures;
            this.blendingFactors = blendingFactors;
        }
    }
    
    /**Setting cube map texture at specific id. The id has to be in the range of
     * the units else it wouldnt change!
     * 
     * @param id Id of the unit to set the texture
     * @param tex Texture to set at the id
     */
    public void setCubeMapTexture(int id, int tex){
        if(id >= 0 && id < cubeMapTextures.length){
            cubeMapTextures[id] = tex;
        }
    }
    
    /**Setting blending factor at specific id. The id has to be in the range of
     * the units else it wouldnt change!<br>
     * The factor represents the visibility of the cube map texture. 0.0f is not
     * visible and 1.0f is full visible. The blending factor will be multiplied
     * by the texture color.
     * 
     * @param id Id of the unit to set the blending factor for
     * @param blending Blending factor to set at the id
     */
    public void setBlendingFactor(int id, float blending){
        if(id >= 0 && id < blendingFactors.length){
            blendingFactors[id] = blending;
        }
    }
    
    /**Adding cube map texture and its initial blending factor to the units.<br>
     * The factor represents the visibility of the cube map texture. 0.0f is not
     * visible and 1.0f is full visible. The blending factor will be multiplied
     * by the texture color.
     * 
     * @param tex Cube map texture to add
     * @param blending Initial blending factor
     * @return Unit id of the texture
     */
    public int addCubeMapTexture(int tex, float blending){
        cubeMapTextures = Toolbox.addElement(cubeMapTextures, tex);
        blendingFactors = Toolbox.addElement(blendingFactors, blending);
        return cubeMapTextures.length -1;
    }
    
    /**@return Read/writeable array of the blending factors
     */
    public float[] getBlendingFactors() {
        return blendingFactors;
    }
    
    /**@return Read/writeable array of the cube map textures
     */
    public int[] getCubeMapTextures() {
        return cubeMapTextures;
    }
    
    /**@return Current transformation matrix of the skybox
     */
    public Matrix4f getTransMat() {
        return transMat;
    }
}
