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

import de.coreengine.util.Configuration;
import de.coreengine.util.Toolbox;

/**Class that represent a lens flare effect to render
 *
 * @author Darius Dinger
 */
public class LensFlare {
    private static final float DEFAULT_SIZE = 
            Configuration.getValuef("LENS_FLARE_DEFAULT_SIZE");
    
    //Lens flare textures to place onto the lens flare vector
    private int[] textures = new int[0];
    
    //Size of the lens flare textures
    private float size = DEFAULT_SIZE;
    
    /**Setting lens flare textures to place onto the lens flare vector
     * 
     * @param textures New lens flare textures
     */
    public void setTextures(int[] textures) {
        this.textures = textures;
    }
    
    /**@return Lens flare textures to place onto the lens flare vector
     */
    public int[] getTextures() {
        return textures;
    }
    
    /**@return Size of the lens flare textures
     */
    public float getSize() {
        return size;
    }
    
    /**@param size New size of the lens flare textures
     */
    public void setSize(float size) {
        this.size = size;
    }
    
    /**Adding texture to the lens flare effect
     * 
     * @param tex Texture to add
     */
    public void addTexture(int tex){
        textures = Toolbox.addElement(textures, tex);
    }
}
