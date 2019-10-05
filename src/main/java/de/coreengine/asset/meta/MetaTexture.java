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

import java.io.Serializable;
import java.nio.ByteBuffer;

/**Class that stores image data and represent and drive image
 *
 * @author Darius Dinger
 */
public class MetaTexture implements Serializable{
    
    //MetaTexture data (pixels)
    private final ByteBuffer data;
    
    //MetaTexture width and height (in px)
    private final int width, height;
    
    //Gl id of the image
    private final String glTexture;
    
    /**Creates new MetaTexture
     * 
     * @param data image data
     * @param width image width
     * @param height image height
     * @param id OpenGL id of the texture
     */
    public MetaTexture(ByteBuffer data, int width, int height, String id) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.glTexture = id;
    }
    
    /**@return image width
     */
    public int getWidth() {
        return width;
    }
    
    /**@return image height
     */
    public int getHeight() {
        return height;
    }
    
    /**@return image data
     */
    public ByteBuffer getData() {
        return data;
    }
    
    /**@return OpenGl id of the texture
     */
    public String getGlTexture() {
        return glTexture;
    }
}
