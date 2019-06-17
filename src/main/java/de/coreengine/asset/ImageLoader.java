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

import de.coreengine.asset.meta.Image;
import de.coreengine.system.Game;
import de.coreengine.util.BufferUtils;
import de.coreengine.util.Configuration;
import de.coreengine.util.Logger;
import de.coreengine.util.gl.MemoryDumper;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**Class for loading images from drive
 *
 * @author Darius Dinger
 */
public class ImageLoader {
    private static final float MIPMAP_LEVEL = Configuration.getValuef("MIPMAP_LEVEL");
    
    /**Load image file from ressources and store into Image object
     * 
     * @param imageFile Path to image relative to application
     * @param mipmap Uses this image mipmapping/anisotropic filtering (if supported)
     * @param filtering Wich filtering mathod (GL_NEARES, GL_LINEAR, ...)
     * @param asResource Loading image from resources
     * @return Image object
     */
    public static Image loadImageFile(String imageFile, boolean mipmap,
                                      int filtering, boolean asResource) {
        
        //Define window icon variables
        ByteBuffer imageData = null;
        int imageWidth = 0, imageHeight = 0;
        
        //Try to load icon
        try (MemoryStack stack = MemoryStack.stackPush()){
            
            //Allocate memory
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            
            if(asResource){
                
                //Load resource
                ByteBuffer buffer = BufferUtils.
                        ioResourceToByteBuffer(imageFile, 8 * 1024);
                
                //Load image and throw exception at error
                imageData = STBImage.stbi_load_from_memory(buffer, w, h, comp, 4);
            }else{
                
                //Load image and throw exception at error
                imageData = STBImage.stbi_load(imageFile, w, h, comp, 4);
            }
            
            if(imageData == null){
                Logger.err("Error by loading image", "The image file " + imageFile +
                        " could not be loaded!");
                Game.exit(1);
            }
            
            //Set width and height from buffer
            imageWidth = w.get();
            imageHeight = h.get();
        }catch(IOException e){
            Logger.err("Error by loading image", "An IO Error occurs while "
                    + "loading image " + imageFile + "!");
            Game.exit(1);
        }
        
        int tex = GL11.glGenTextures();
        MemoryDumper.addTexture(tex);
        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
        
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, imageWidth, imageHeight, 
                0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
        
        if(mipmap){
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, MIPMAP_LEVEL);
            
            if(GL.getCapabilities().GL_EXT_texture_filter_anisotropic){
                float[] aniso = new float[1];
                GL11.glGetFloatv(GL46.GL_MAX_TEXTURE_MAX_ANISOTROPY, aniso);
                float amount = Float.min(4.0f, aniso[0]);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL46.GL_TEXTURE_MAX_ANISOTROPY, amount);
            }
        }else{
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filtering);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filtering);
        }
        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        
        return new Image(imageData, imageWidth, imageHeight, tex);
    }
    
    /**Loading a image file into an opengl texture. Returning 0, if image
     * could not be load
     * 
     * @param imageFile Path to image relative to application
     * @param mipmap Uses this image mipmapping/anisotropic filtering (if supported)
     * @param filtering Wich filtering mathod (GL_NEARES, GL_LINEAR, ...)
     * @param asResource Loading image from resources
     * @return Opengl texture with image data
     */
    public static int loadImageFileGl(String imageFile, boolean mipmap, 
            int filtering, boolean asResource){
        Image img = loadImageFile(imageFile, mipmap, filtering, asResource);
        return img.getGlTexture();
    }
    
    /**Loading cube map texture from abstract path and extension. The cube map
     * file names will be generated by:<br>
     * path + ['_lf', '_rt', '_up', '_dn', '_ft', '_bk'] + '.' + ext
     * 
     * @param path Abstract path to the cubemap
     * @param ext Extension of the cube map images
     * @param asResource Loading cube map images from resources
     * @return Cube map texture id
     */
    public static int loadCubeMap(String path, String ext, boolean asResource){
        return loadCubeMap(path + "_lf." + ext, path + "_rt." + ext, 
                path + "_up." + ext, path + "_dn." + ext, path + "_ft." + ext, 
                path + "_bk." + ext, asResource);
    }
    
    /**Loadig cubemap texture from 6 texture files
     * 
     * @param left Left sube map texture
     * @param right Right sube map texture
     * @param top Top sube map texture
     * @param bottom Bottom sube map texture
     * @param front Front sube map texture
     * @param back Back sube map texture
     * @param asResource Loading cube map images from resources
     * @return Loaded cube map texture id
     */
    public static int loadCubeMap(String left, String right, String top, 
            String bottom, String front, String back, boolean asResource){
        
        //Loading cube map images
        Image imgL = loadImageFile(left, false, GL11.GL_LINEAR, asResource);
        Image imgR = loadImageFile(right, false, GL11.GL_LINEAR, asResource);
        Image imgT = loadImageFile(top, false, GL11.GL_LINEAR, asResource);
        Image imgB = loadImageFile(bottom, false, GL11.GL_LINEAR, asResource);
        Image imgF = loadImageFile(front, false, GL11.GL_LINEAR, asResource);
        Image imgBa = loadImageFile(back, false, GL11.GL_LINEAR, asResource);
        
        //Gen and bind cube map texture
        int tex = GL11.glGenTextures();
        MemoryDumper.addTexture(tex);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, tex);
        GL11.glEnable(GL13.GL_TEXTURE_CUBE_MAP);
        
        //Apply textures to image
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, 
                GL11.GL_RGBA, imgL.getWidth(), imgL.getHeight(), 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgL.getData());
        
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, 
                GL11.GL_RGBA, imgR.getWidth(), imgR.getHeight(), 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgR.getData());
        
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, 
                GL11.GL_RGBA, imgT.getWidth(), imgT.getHeight(), 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgT.getData());
        
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, 
                GL11.GL_RGBA, imgB.getWidth(), imgB.getHeight(), 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgB.getData());
        
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, 
                GL11.GL_RGBA, imgF.getWidth(), imgF.getHeight(), 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgF.getData());
        
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, 
                GL11.GL_RGBA, imgBa.getWidth(), imgBa.getHeight(), 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imgBa.getData());
        
        //Adding filtering
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        
        //Set edge wrap
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
        
        return tex;
    }
}
