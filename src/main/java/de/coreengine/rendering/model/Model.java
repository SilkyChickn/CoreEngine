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
package de.coreengine.rendering.model;

import com.bulletphysics.collision.shapes.CollisionShape;
import de.coreengine.util.Toolbox;
import de.coreengine.util.gl.IndexBuffer;
import de.coreengine.util.gl.VertexArrayObject;

import java.util.Arrays;

/**Class that represents a complex model with variouse materials and meshes
 *
 * @author Darius Dinger
 */
public class Model {
    
    //Vao of the model, where all vertices are stored
    private final VertexArrayObject vao;
    
    //Indexbuffers of the model, where the indices are stored, 
    //that connecting the vertices
    private final IndexBuffer[] indexBuffers;
    
    //Models materials
    private final Material[] materials;
    
    //Collision shape for the model
    private final CollisionShape shape;
    
    /**Creating new model and set the materials to default material
     * 
     * @param vao Models VertexArrayObject with the vertices
     * @param indexBuffers Models IndeBuffers with the indices, that connecting the vertices
     * @param shape Collison shape of the model
     */
    public Model(VertexArrayObject vao, IndexBuffer[] indexBuffers, CollisionShape shape) {
        this.vao = vao;
        this.indexBuffers = indexBuffers;
        this.materials = new Material[indexBuffers.length];
        this.shape = shape;
        
        //Create empty materials
        for(int i = 0; i < materials.length; i++)
            materials[i] = new Material();
    }
    
    /**Creating new model with all parameters
     * 
     * if indexBuffer count is less the materials count 
     * then superfluous materials will be deleted
     * 
     * if indexBuffer count is greater the materials count 
     * then missing materials will be added
     * 
     * if a material is null
     * then material is set to a default material
     * 
     * @param vao Models VertexArrayObject with the vertices
     * @param indexBuffers Models IndeBuffers with the indices, that connecting the vertices
     * @param materials Models materials
     * @param shape Collision shape of the model
     */
    public Model(VertexArrayObject vao, IndexBuffer[] indexBuffers, 
            Material[] materials, CollisionShape shape) {
        this.vao = vao;
        this.indexBuffers = indexBuffers;
        this.materials = materials;
        this.shape = shape;
        
        //Syncronize materials and indexbuffers
        if(this.materials.length < this.indexBuffers.length){
            
            //Adding missing materials at end
            for(int i = this.materials.length -1; i < this.indexBuffers.length; i++){
                Toolbox.addElement(this.materials, new Material());
            }
        }else if(this.materials.length > this.indexBuffers.length){
            
            //Remove superfluous materials from end
            Arrays.copyOf(this.materials, this.indexBuffers.length);
        }else{
            
            //Fill 'null'-gaps with default materials
            for(int i = 0; i < this.materials.length; i++){
                if(this.materials[i] == null) this.materials[i] = new Material();
            }
        }
    }
    
    /**@return Collision shape of the model
     */
    public CollisionShape getShape() {
        return shape;
    }
    
    /**@return Models VertexArrayObject
     */
    public VertexArrayObject getVao() {
        return vao;
    }
    
    /**Gets a indexbuffer for a specific mesh
     * 
     * @param i Id of the mesh
     * @return Indexbuffer of the mesh
     */
    public IndexBuffer getIndexBufferAt(int i){
        return indexBuffers[i];
    }
    
    /**Gets a material for a specific mesh
     * 
     * @param i Id of the mesh
     * @return Material of the mesh
     */
    public Material getMaterialAt(int i){
        return materials[i];
    }
    
    /**@return Count of vao meshes/faces (materials-length, indexbuffers-length)
     */
    public int getMeshCount(){
        return indexBuffers.length;
    }
}
