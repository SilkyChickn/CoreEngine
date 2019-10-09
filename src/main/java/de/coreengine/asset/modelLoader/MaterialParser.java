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

package de.coreengine.asset.modelLoader;

import de.coreengine.asset.dataStructures.MaterialData;
import de.coreengine.rendering.model.Color;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIString;

import java.nio.IntBuffer;

import static org.lwjgl.assimp.Assimp.*;

public class MaterialParser {

    //Input
    private final AIMaterial aiMaterial;

    //Output
    private final MaterialData materialData = new MaterialData();

    /**Creating new material data that can parse ai materials into materials and dataStructures materials
     *
     * @param aiMaterial AIMaterial to parse
     */
    public MaterialParser(AIMaterial aiMaterial) {
        this.aiMaterial = aiMaterial;
    }

    /**Parse ai materials into materials and dataStructures materials
     */
    public void parse(){

        //Data buffers
        AIColor4D color = AIColor4D.create();
        AIString path = AIString.create();
        float[] floot = new float[1];

        //Load diffuse texture
        if(getTexturePath(aiTextureType_DIFFUSE, path)){
            materialData.diffuseMap = path.dataString();
        }

        //Load normal texture
        if(getTexturePath(aiTextureType_NORMALS, path)){
            materialData.normalMap = path.dataString();
        }

        //Load specular texture
        if(getTexturePath(aiTextureType_SPECULAR, path)){
            materialData.specularMap = path.dataString();
        }

        //Load ambient texture
        if(getTexturePath(aiTextureType_AMBIENT, path)){
            materialData.ambientOcclusionMap = path.dataString();
        }

        //Load alpha texture
        if(getTexturePath(aiTextureType_OPACITY, path)){
            materialData.alphaMap = path.dataString();
        }

        //Load displacement texture
        if(getTexturePath(aiTextureType_DISPLACEMENT, path)){
            materialData.displacementMap = path.dataString();
        }

        //Load diffuse color
        if(aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color)
                == aiReturn_SUCCESS){
            materialData.diffuseColor = new Color(color.r(), color.g(), color.b());
        }

        //Load shininess
        if(aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS, aiTextureType_NONE, 0,
                floot, new int[] {1}) == aiReturn_SUCCESS){
            materialData.shininess = floot[0];
        }

        //Load bump/displacement factor
        if(aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_BUMPSCALING, aiTextureType_NONE, 0,
                floot, new int[] {1}) == aiReturn_SUCCESS){
            materialData.displacementFactor = floot[0];
        }
    }

    /**Getting texture from ai material
     *
     * @param type Type of texture to get
     * @param path Path to store tex path in
     * @return True if texture exist, else false
     */
    private boolean getTexturePath(int type, AIString path){
        return aiGetMaterialTexture(aiMaterial, type, 0, path, (IntBuffer) null, null, null,
                null, null, null) == aiReturn_SUCCESS;
    }

    /**@return Parsed dataStructures material
     */
    public MaterialData getMaterialData() {
        return materialData;
    }
}
