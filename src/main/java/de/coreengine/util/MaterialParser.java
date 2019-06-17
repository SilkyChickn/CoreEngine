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
package de.coreengine.util;

import de.coreengine.asset.ImageLoader;
import de.coreengine.asset.meta.MetaMaterial;
import de.coreengine.rendering.model.Material;
import org.lwjgl.opengl.GL11;

/**Parse and unparsing materials/metamaterials from/into strings
 *
 * @author Darius Dinger
 */
public class MaterialParser {
    
    /**Parsing material from string.
     * Returning null for impossible parse
     * 
     * @param line String to parse
     * @param asResource Should the textures from the material be loaded from resources
     * @return Parsed material or null
     */
    public static Material toMaterial(String line, boolean asResource){
        Material result = new Material();
        String[] args = line.split(" ");
        
        for(int i = 0; i < args.length; i+=2){
            switch (args[i]) {
                case "c":
                    {
                        String[] col = args[i+1].split("-");
                        result.diffuseColor.set(Float.parseFloat(col[0]),
                                Float.parseFloat(col[1]), Float.parseFloat(col[2]));
                        break;
                    }
                case "g":
                    {
                        String[] col = args[i+1].split("-");
                        result.glowColor.set(Float.parseFloat(col[0]),
                                Float.parseFloat(col[1]), Float.parseFloat(col[2]));
                        break;
                    }
                case "c_map":
                    result.diffuseMap = ImageLoader.loadImageFileGl(args[i + 1],
                            true, GL11.GL_LINEAR, asResource);
                    break;
                case "n_map":
                    result.normalMap = ImageLoader.loadImageFileGl(args[i+1], 
                            true, GL11.GL_LINEAR, asResource);
                    break;
                case "ao_map":
                    result.ambientOcclusionMap = ImageLoader.loadImageFileGl(args[i+1], 
                            true, GL11.GL_LINEAR, asResource);
                    break;
                case "a_map":
                    result.alphaMap = ImageLoader.loadImageFileGl(args[i+1], 
                            true, GL11.GL_LINEAR, asResource);
                    break;
                case "g_map":
                    result.glowMap = ImageLoader.loadImageFileGl(args[i+1], 
                            true, GL11.GL_LINEAR, asResource);
                    break;
                case "d_map":
                    result.displacementMap = ImageLoader.loadImageFileGl(args[i+1], 
                            true, GL11.GL_LINEAR, asResource);
                    break;
                case "s_map":
                    result.specularMap = ImageLoader.loadImageFileGl(args[i+1], 
                            true, GL11.GL_LINEAR, asResource);
                    break;
                case "r_map":
                    result.reflectionMap = ImageLoader.loadCubeMap(args[i+1], 
                            "png", asResource);
                    break;
                case "df":
                    result.displacementFactor = Float.parseFloat(args[i+1]);
                    break;
                case "t":
                    result.tiling = Float.parseFloat(args[i+1]);
                    break;
                case "r":
                    result.reflectivity = Float.parseFloat(args[i+1]);
                    break;
                case "sd":
                    result.shineDamping = Float.parseFloat(args[i+1]);
                    break;
                default:
                    break;
            }
        }
        
        return result;
    }
    
    /**Convert a meta material into a string.
     * 
     * @param mat Material to convert
     * @return Converted material as string
     */
    public static String toString(MetaMaterial mat){
        String result = "";
        
        if(mat.diffuseColor != null) result += " c " + mat.diffuseColor.getRed() + 
                "-" + mat.diffuseColor.getGreen() + "-" + mat.diffuseColor.getBlue();
        if(mat.glowColor != null) result += " g " + mat.glowColor.getRed() + 
                "-" + mat.glowColor.getGreen() + "-" + mat.glowColor.getBlue();
        if(mat.diffuseMap != null) result += " c_map " + mat.diffuseMap;
        if(mat.normalMap != null) result += " n_map " + mat.normalMap;
        if(mat.specularMap != null) result += " s_map " + mat.specularMap;
        if(mat.displacementMap != null) result += " d_map " + mat.displacementMap;
        if(mat.ambientOcclusionMap != null) result += " ao_map " + mat.ambientOcclusionMap;
        if(mat.alphaMap != null) result += " a_map " + mat.alphaMap;
        if(mat.reflectionMap != null) result += " r_map " + mat.reflectionMap;
        if(mat.glowMap != null) result += " g_map " + mat.glowMap;
        if(mat.displacementFactor != null) result += " df " + mat.displacementFactor;
        if(mat.tiling != null) result += " t " + mat.tiling;
        if(mat.reflectivity != null) result += " r " + mat.reflectivity;
        if(mat.shineDamping != null) result += " sd " + mat.shineDamping;
        
        return result.replaceFirst(" ", "");
    }
}
