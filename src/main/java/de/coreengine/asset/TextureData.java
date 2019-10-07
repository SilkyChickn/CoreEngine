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

import de.coreengine.util.Configuration;
import de.coreengine.util.gl.MemoryDumper;
import org.lwjgl.opengl.*;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**Class that stores image data and represent and drive image
 *
 * @author Darius Dinger
 */
public class TextureData implements Serializable{
    private static final float MIPMAP_LEVEL = Configuration.getValuef("MIPMAP_LEVEL");

    //Data
    public ByteBuffer data = null;
    public Integer width = null, height = null;
    public String key = null;

    /**Generating key to acces texture in asset database
     *
     * @param key Name of the key to generate (If key exist, this method returns)
     * @param genMipmap Generate mipmap
     * @param filtering Filtering
     */
    public void generateKey(String key, boolean genMipmap, int filtering){
        if(this.key != null) return;

        //Gen gl texture
        int tex = GL11.glGenTextures();
        MemoryDumper.addTexture(tex);

        //Bind and fill data
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);

        //Generate mip map and anisotropic filtering if enabled
        if(genMipmap){
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

        //Unbind texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        //Load to asset database
        AssetDatabase.textures.put(key, tex);
    }

    /**@return Texture width in pixels
     */
    public Integer getWidth() {
        return width;
    }

    /**@return Texture height in pixels
     */
    public Integer getHeight() {
        return height;
    }

    /**@return Texture data
     */
    public ByteBuffer getData() {
        return data;
    }

    /**@return Texture key in asset database or null, if no loaded
     */
    public String getKey() {
        return key;
    }
}
