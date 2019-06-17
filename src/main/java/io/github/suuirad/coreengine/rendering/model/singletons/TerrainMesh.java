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
package io.github.suuirad.coreengine.rendering.model.singletons;

import io.github.suuirad.coreengine.rendering.renderable.terrain.TerrainMeshLoader;
import io.github.suuirad.coreengine.util.gl.IndexBuffer;
import io.github.suuirad.coreengine.util.gl.VertexArrayObject;

/**Class that represent one terrain grid and its morphing variants into lower lod
 *
 * @author Darius Dinger
 */
public class TerrainMesh {
    private static TerrainMesh instance = null;
    
    //Vao that contains all terrain vertices
    private final VertexArrayObject vao;
    
    //Indices for the full terrain mesh and all morphing variants into lower lod
    private final IndexBuffer fullMesh, morphT, morphB, morphR, morphL, morphTL, 
            morphTR, morphBL, morphBR;
    
    /**Create new terrain mesh with its morphing variants into lower lod
     * 
     * @param vao VertexArrayObject of the mesh with all vertices
     * @param fullMesh Indices for full terrain mesh without morphing
     * @param morphT Indices for terrain mesh with top morphing
     * @param morphB Indices for terrain mesh with bottom morphing
     * @param morphR Indices for terrain mesh with right morphing
     * @param morphL Indices for terrain mesh with left morphing
     * @param morphTL Indices for terrain mesh with top left morphing
     * @param morphTR Indices for terrain mesh with top right morphing
     * @param morphBL Indices for terrain mesh with bottom left morphing
     * @param morphBR  Indices for terrain mesh with bottom right morphing
     */
    public TerrainMesh(VertexArrayObject vao, IndexBuffer fullMesh, IndexBuffer morphT, IndexBuffer morphB, IndexBuffer morphR, IndexBuffer morphL, IndexBuffer morphTL, IndexBuffer morphTR, IndexBuffer morphBL, IndexBuffer morphBR) {
        this.vao = vao;
        this.fullMesh = fullMesh;
        this.morphT = morphT;
        this.morphB = morphB;
        this.morphR = morphR;
        this.morphL = morphL;
        this.morphTL = morphTL;
        this.morphTR = morphTR;
        this.morphBL = morphBL;
        this.morphBR = morphBR;
    }
    
    /**@return VertexArrayObject of the mesh with all vertices
     */
    public VertexArrayObject getVao() {
        return vao;
    }
    
    /**@return Indices for full terrain mesh without morphing
     */
    public IndexBuffer getFullMesh() {
        return fullMesh;
    }
    
    /**@return Indices for terrain mesh with top morphing
     */
    public IndexBuffer getMorphT() {
        return morphT;
    }
    
    /**@return Indices for terrain mesh with bottom morphing
     */
    public IndexBuffer getMorphB() {
        return morphB;
    }
    
    /**@return Indices for terrain mesh with right morphing
     */
    public IndexBuffer getMorphR() {
        return morphR;
    }
    
    /**@return Indices for terrain mesh with left morphing
     */
    public IndexBuffer getMorphL() {
        return morphL;
    }
    
    /**@return Indices for terrain mesh with top left morphing
     */
    public IndexBuffer getMorphTL() {
        return morphTL;
    }
    
    /**@return Indices for terrain mesh with top right morphing
     */
    public IndexBuffer getMorphTR() {
        return morphTR;
    }
    
    /**@return Indices for terrain mesh with bottom left morphing
     */
    public IndexBuffer getMorphBL() {
        return morphBL;
    }
    
    /**@return Indices for terrain mesh with bottom right morphing
     */
    public IndexBuffer getMorphBR() {
        return morphBR;
    }
    
    /**@return Get terrain mesh singleton instance
     */
    public static TerrainMesh getInstance(){
        if(instance == null) instance = TerrainMeshLoader.loadTerrainMesh();
        return instance;
    }
}
