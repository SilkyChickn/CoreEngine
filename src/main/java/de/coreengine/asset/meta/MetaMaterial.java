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
package de.coreengine.asset.meta;

import de.coreengine.asset.TextureLoader;
import de.coreengine.rendering.model.Color;
import de.coreengine.rendering.model.Material;
import org.lwjgl.opengl.GL11;

/**A Material that can be stored into a file<br>
 * A value of null means the default value of a material
 *
 * @author Darius Dinger
 */
public class MetaMaterial {
    
    //Data
    public Color diffuseColor = null, glowColor = null;
    public String diffuseMap = null, normalMap = null, specularMap = null, 
            displacementMap = null, ambientOcclusionMap = null, alphaMap = null, 
            reflectionMap = null, glowMap = null;
    public Float displacementFactor = null, tiling = null, shininess = null,
            shineDamping = null;

    /**Getting a new instance of the meta material
     *
     * @return New material instance
     */
    public Material getInstance(String texPath, boolean asResource){
        Material instance = new Material();

        //Copy colors
        if(diffuseColor != null) instance.diffuseColor.set(diffuseColor);
        if(glowColor != null) instance.glowColor.set(glowColor);

        //Loading textures
        if(diffuseMap != null) instance.diffuseMap = loadTexture(diffuseMap, texPath, asResource);
        if(normalMap != null) instance.normalMap = loadTexture(normalMap, texPath, asResource);
        if(specularMap != null) instance.specularMap = loadTexture(specularMap, texPath, asResource);
        if(displacementMap != null) instance.displacementMap = loadTexture(displacementMap, texPath, asResource);
        if(ambientOcclusionMap != null) instance.ambientOcclusionMap = loadTexture(ambientOcclusionMap, texPath, asResource);
        if(alphaMap != null) instance.alphaMap = loadTexture(alphaMap, texPath, asResource);
        if(reflectionMap != null) instance.reflectionMap = loadTexture(reflectionMap, texPath, asResource);
        if(glowMap != null) instance.glowMap = loadTexture(glowMap, texPath, asResource);

        //Copy floats
        if(displacementFactor != null) instance.displacementFactor = displacementFactor;
        if(tiling != null) instance.tiling = tiling;
        if(shininess != null) instance.shininess = shininess;
        if(shineDamping != null) instance.shineDamping = shineDamping;

        return instance;
    }

    /**Loading texture from material
     *
     * @param file File to load
     * @param texPath Location of the materials textures
     * @param asResource Load textures from resource
     * @return Loaded texture key
     */
    private String loadTexture(String file, String texPath, boolean asResource){
        TextureLoader.loadTextureFile(texPath +file, true, GL11.GL_LINEAR, asResource);
        return texPath +file;
    }
}
