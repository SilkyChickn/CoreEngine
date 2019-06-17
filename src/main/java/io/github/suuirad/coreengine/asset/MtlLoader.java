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
package io.github.suuirad.coreengine.asset;

import io.github.suuirad.coreengine.asset.meta.MetaMaterial;
import io.github.suuirad.coreengine.rendering.model.Color;
import io.github.suuirad.coreengine.rendering.model.Material;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

/**Class that can load a mtl (Material) file
 *
 * @author Darius Dinger
 */
public class MtlLoader {
    
    /**Loading materials from string array in mtl format<br>
     * <br>
     * Mtl format:<br>
     * The material starts with 'newmtl [MtlName]'. Then the following parameters
     * can be set. If one isnt set it will be set to default.
     * <br>
     * c        Material color<br>
     * g        Material glowing color<br>
     * df       Material displacement factor<br>
     * r        Material reflectivity<br>
     * sd       Material shine damper<br>
     * t        Material tiling for the maps<br>
     * map_c    Material color map<br>
     * map_n    Material normal map<br>
     * map_ao   Material ambient occlusion map<br>
     * map_a    Material alpha map<br>
     * map_g    Material glowing map<br>
     * map_d    Material displacement map<br>
     * map_s    Material specular map<br>
     * map_r    Material relfection map (as png cube map)<br>
     * 
     * @param mtlFile String array of materials
     * @param texLocation Location of the materials textures
     * @param asResource Load material file from resources
     * @param metaMaterials Hashmap to store meta materials in or null to throw them 
     * @return Materials as hashmap
     */
    static HashMap<String, Material> loadMaterials(String[] mtlFile,
                                                   String texLocation, boolean asResource, HashMap<String,
            MetaMaterial> metaMaterials) {
        HashMap<String, Material> materials = new HashMap<>();
        if(mtlFile == null) return materials;
        
        //Cur materials
        Material curMat = null;
        MetaMaterial curMetaMat = null;
        
        //Iterate through mtl lines
        for(String line: mtlFile){
            String[] args = line.split(" ");
            
            //Prepare new material
            if(args[0].equalsIgnoreCase("newmtl")){
                curMat = new Material();
                materials.put(args[1], curMat);
                curMetaMat = new MetaMaterial();
                if(metaMaterials != null)metaMaterials.put(args[1], curMetaMat);
                continue;
            }
            
            //If no material set, continue loop
            if(curMat == null) continue;
            
            //Setting material values
            if(args[0].equalsIgnoreCase("c")){
                curMat.diffuseColor.setRed(Float.parseFloat(args[1]));
                curMat.diffuseColor.setGreen(Float.parseFloat(args[2]));
                curMat.diffuseColor.setBlue(Float.parseFloat(args[3]));

                curMetaMat.diffuseColor = new Color(Float.parseFloat(args[1]),
                        Float.parseFloat(args[2]), Float.parseFloat(args[3]));
            }else if(args[0].equalsIgnoreCase("g")){
                curMat.glowColor.setRed(Float.parseFloat(args[1]));
                curMat.glowColor.setGreen(Float.parseFloat(args[2]));
                curMat.glowColor.setBlue(Float.parseFloat(args[3]));
                
                curMetaMat.glowColor = new Color(Float.parseFloat(args[1]), 
                        Float.parseFloat(args[2]), Float.parseFloat(args[3]));
            }else if(args[0].equalsIgnoreCase("df")){
                curMat.displacementFactor = Float.parseFloat(args[1]);
                curMetaMat.displacementFactor = Float.parseFloat(args[1]);
            }else if(args[0].equalsIgnoreCase("r")){
                curMat.reflectivity = Float.parseFloat(args[1]);
                curMetaMat.reflectivity = Float.parseFloat(args[1]);
            }else if(args[0].equalsIgnoreCase("sd")){
                curMat.shineDamping = Float.parseFloat(args[1]);
                curMetaMat.shineDamping = Float.parseFloat(args[1]);
            }else if(args[0].equalsIgnoreCase("t")){
                curMat.tiling = Float.parseFloat(args[1]);
                curMetaMat.tiling = Float.parseFloat(args[1]);
            }else if(args[0].equalsIgnoreCase("map_c")){
                curMat.diffuseMap = ImageLoader.loadImageFileGl(
                        texLocation +args[1], true, GL11.GL_LINEAR, asResource);
                curMetaMat.diffuseMap = texLocation +args[1];
            }else if(args[0].equalsIgnoreCase("map_n")){
                curMat.normalMap = ImageLoader.loadImageFileGl(
                        texLocation +args[1], true, GL11.GL_LINEAR, asResource);
                curMetaMat.normalMap = texLocation +args[1];
            }else if(args[0].equalsIgnoreCase("map_ao")){
                curMat.ambientOcclusionMap = ImageLoader.loadImageFileGl(
                        texLocation +args[1], true, GL11.GL_LINEAR, asResource);
                curMetaMat.ambientOcclusionMap = texLocation +args[1];
            }else if(args[0].equalsIgnoreCase("map_a")){
                curMat.alphaMap = ImageLoader.loadImageFileGl(
                        texLocation +args[1], true, GL11.GL_LINEAR, asResource);
                curMetaMat.alphaMap = texLocation +args[1];
            }else if(args[0].equalsIgnoreCase("map_g")){
                curMat.glowMap = ImageLoader.loadImageFileGl(
                        texLocation +args[1], true, GL11.GL_LINEAR, asResource);
                curMetaMat.glowMap = texLocation +args[1];
            }else if(args[0].equalsIgnoreCase("map_d")){
                curMat.displacementMap = ImageLoader.loadImageFileGl(
                        texLocation +args[1], true, GL11.GL_LINEAR, asResource);
                curMetaMat.displacementMap = texLocation +args[1];
            }else if(args[0].equalsIgnoreCase("map_s")){
                curMat.specularMap = ImageLoader.loadImageFileGl(
                        texLocation +args[1], true, GL11.GL_LINEAR, asResource);
                curMetaMat.specularMap = texLocation +args[1];
            }else if(args[0].equalsIgnoreCase("map_r")){
                curMat.reflectionMap = ImageLoader.loadCubeMap(
                        texLocation +args[1], "png", asResource);
                curMetaMat.reflectionMap = texLocation +args[1];
            }
        }
        
        return materials;
    }
}
