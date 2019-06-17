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
package io.github.suuirad.coreengine.rendering.renderable.terrain;

import io.github.suuirad.coreengine.rendering.model.Material;

/**Class that represents a texture pack for a terrain
 *
 * @author Darius Dinger
 */
public class TerrainTexturePack {
    
    //Materials for the terrain 
    //main material and red, green, blue material from blend map
    private final Material material = new Material();
    private final Material rMaterial = new Material();
    private final Material gMaterial = new Material();
    private final Material bMaterial = new Material();
    
    /**@return Main material of the terrain.
     * (no r,g, b in blend map or no blend map)
     */
    public Material getMaterial() {
        return material;
    }
    
    /**@return Red material of the terrain (red in blend map)
     */
    public Material getRedMaterial() {
        return rMaterial;
    }
    
    /**@return Green material of the terrain (green in blend map)
     */
    public Material getGreenMaterial() {
        return gMaterial;
    }
    
    /**@return Blue material of the terrain (blue in blend map)
     */
    public Material getBlueMaterial() {
        return bMaterial;
    }
}
