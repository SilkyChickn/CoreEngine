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
package de.coreengine.asset;

import de.coreengine.rendering.model.Material;
import de.coreengine.system.Game;
import de.coreengine.util.BufferUtils;
import de.coreengine.util.Logger;
import de.coreengine.util.gl.MemoryDumper;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**Class for loading Textures from drive<br>
 *<br>
 * Supported Formats (From STB):<br>
 * <br>
 * JPEG baseline and progressive (12 bpc/arithmetic not supported, same as stock IJG lib<br>
 * PNG 1/2/4/8/16-bit-per-channel<br>
 * TGA (not sure what subset, if a subset)<br>
 * BMP non-1bpp, non-RLE<br>
 * PSD (composited view only, no extra channels, 8/16 bit-per-channel)<br>
 * GIF (*desired_channels always reports as 4-channel)<br>
 * HDR (radiance rgbE format)<br>
 * PIC (Softimage PIC)<br>
 * PNM (PPM and PGM binary only)<br>
 *<br>
 * @author Darius Dinger
 */
public class TextureLoader {

    /**Load TextureData file and store into TextureData object
     * 
     * @param textureFile Path to TextureData relative to application
     * @param mipmap Uses this TextureData mipmapping/anisotropic filtering (if supported)
     * @param filtering Wich filtering mathod (GL_NEARES, GL_LINEAR, ...)
     * @param asResource Loading TextureData from resources
     * @return TextureData object
     */
    public static TextureData loadTextureFileMeta(String textureFile, boolean mipmap,
                                                  int filtering, boolean asResource) {
        
        //Define window icon variables
        ByteBuffer textureData = null;
        int textureWidth = 0, textureHeight = 0;
        
        //Try to load icon
        try (MemoryStack stack = MemoryStack.stackPush()){
            
            //Allocate memory
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            
            if(asResource){
                
                //Load resource
                ByteBuffer buffer = BufferUtils.
                        ioResourceToByteBuffer(textureFile, 8 * 1024);
                
                //Load TextureData and throw exception at error
                textureData = STBImage.stbi_load_from_memory(buffer, w, h, comp, 4);
            }else{
                
                //Load TextureData and throw exception at error
                textureData = STBImage.stbi_load(textureFile, w, h, comp, 4);
            }
            
            if(textureData == null){
                Logger.warn("Error by loading TextureData", "The TextureData file " + textureFile +
                        " could not be loaded! Returning null!");
                return null;
            }
            
            //Set width and height from buffer
            textureWidth = w.get();
            textureHeight = h.get();
        }catch(IOException e){
            Logger.err("Error by loading TextureData", "An IO Error occurs while "
                    + "loading TextureData " + textureFile + "!");
            Game.exit(1);
        }
        
        //Create texture data
        TextureData texture = new TextureData();
        texture.data = textureData;
        texture.width = textureWidth;
        texture.height = textureHeight;
        
        return texture;
    }
    
    /**Loading a TextureData file into an opengl texture and storing into asset database
     * 
     * @param textureFile Path to TextureData relative to application
     * @param mipmap Uses this TextureData mipmapping/anisotropic filtering (if supported)
     * @param filtering Wich filtering mathod (GL_NEARES, GL_LINEAR, ...)
     * @param asResource Loading TextureData from resources
     */
    public static void loadTextureFile(String textureFile, boolean mipmap, int filtering, boolean asResource){
        if(AssetDatabase.textures.containsKey(textureFile)) return;
        TextureData textureData = loadTextureFileMeta(textureFile, mipmap, filtering, asResource);
        if(textureData != null)textureData.generateKey(textureFile, mipmap, filtering);
        else AssetDatabase.textures.put(textureFile, AssetDatabase.getTexture(Material.TEXTURE_WHITE));
    }
    
    /**Loading cube map texture from abstract path and extension and storing into asset database. The cube map
     * file names will be generated by:<br>
     * path + ['_lf', '_rt', '_up', '_dn', '_ft', '_bk'] + '.' + ext
     * 
     * @param path Abstract path to the cubemap
     * @param ext Extension of the cube map Textures
     * @param asResource Loading cube map Textures from resources
     */
    public static void loadCubeMap(String path, String ext, boolean asResource){
        loadCubeMap(path, path + "_lf." + ext, path + "_rt." + ext,
                path + "_up." + ext, path + "_dn." + ext, path + "_ft." + ext, 
                path + "_bk." + ext, asResource);
    }
    
    /**Loadig cubemap texture from 6 texture files and storing into asset database
     *
     * @param key Key in the asset database
     * @param left Left sube map texture
     * @param right Right sube map texture
     * @param top Top sube map texture
     * @param bottom Bottom sube map texture
     * @param front Front sube map texture
     * @param back Back sube map texture
     * @param asResource Loading cube map Textures from resources
     */
    public static void loadCubeMap(String key, String left, String right, String top,
            String bottom, String front, String back, boolean asResource){
        if(AssetDatabase.textures.containsKey(key)) return;

        //Loading cube map Textures
        TextureData imgL = loadTextureFileMeta(left, false, GL11.GL_LINEAR, asResource);
        TextureData imgR = loadTextureFileMeta(right, false, GL11.GL_LINEAR, asResource);
        TextureData imgT = loadTextureFileMeta(top, false, GL11.GL_LINEAR, asResource);
        TextureData imgB = loadTextureFileMeta(bottom, false, GL11.GL_LINEAR, asResource);
        TextureData imgF = loadTextureFileMeta(front, false, GL11.GL_LINEAR, asResource);
        TextureData imgBa = loadTextureFileMeta(back, false, GL11.GL_LINEAR, asResource);

        //Gen and bind cube map texture
        int tex = GL11.glGenTextures();
        MemoryDumper.addTexture(tex);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, tex);
        GL11.glEnable(GL13.GL_TEXTURE_CUBE_MAP);
        
        //Apply textures to TextureData
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0,
                GL11.GL_RGBA, imgL.width, imgL.height, 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgL.data);
        
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0,
                GL11.GL_RGBA, imgR.width, imgR.height, 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgR.data);
        
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0,
                GL11.GL_RGBA, imgT.width, imgT.height, 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgT.data);
        
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0,
                GL11.GL_RGBA, imgB.width, imgB.height, 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgB.data);
        
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0,
                GL11.GL_RGBA, imgF.width, imgF.height, 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgF.data);
        
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0,
                GL11.GL_RGBA, imgBa.width, imgBa.height, 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgBa.data);
        
        //Adding filtering
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        
        //Set edge wrap
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);

        AssetDatabase.textures.put(key, tex);
    }
}
