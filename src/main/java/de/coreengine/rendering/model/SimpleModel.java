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
package de.coreengine.rendering.model;

import com.bulletphysics.collision.shapes.CollisionShape;
import de.coreengine.util.gl.IndexBuffer;
import de.coreengine.util.gl.VertexArrayObject;

/**Class that represent a simple model, wich has only one material and consists
 * of only one mesh
 *
 * @author Darius Dinger
 */
public class SimpleModel {
    
    //Vao of the model, where all vertices are stored
    private final VertexArrayObject vao;
    
    //Indexbuffer of the model, where the indices are stored, 
    //that connecting the vertices
    private final IndexBuffer indexBuffer;
    
    //Models material
    private final Material material;
    
    //Collision shape of the model
    private final CollisionShape shape;
    
    /**Creating new model and set the material to default material
     * 
     * @param vao Models VertexArrayObject with the vertices
     * @param indexBuffer Models IndeBuffer with the indices, that connecting the vertices
     * @param shape Collision shape of the model
     */
    public SimpleModel(VertexArrayObject vao, IndexBuffer indexBuffer, 
            CollisionShape shape) {
        this.vao = vao;
        this.indexBuffer = indexBuffer;
        this.material = new Material();
        this.shape = shape;
    }
    
    /**Creating new model with all parameters
     * 
     * @param vao Models VertexArrayObject with the vertices
     * @param indexBuffer Models IndeBuffer with the indices, that connecting the vertices
     * @param material Models material
     * @param shape Collision shape of the model
     */
    public SimpleModel(VertexArrayObject vao, IndexBuffer indexBuffer, 
            Material material, CollisionShape shape) {
        this.vao = vao;
        this.indexBuffer = indexBuffer;
        this.material = material;
        this.shape = shape;
    }
    
    /**@return Models IndexBuffer
     */
    public IndexBuffer getIndexBuffer() {
        return indexBuffer;
    }
    
    /**@return Models material
     */
    public Material getMaterial() {
        return material;
    }
    
    /**@return Models VertexArrayObject
     */
    public VertexArrayObject getVao() {
        return vao;
    }
    
    /**@return Collision shape of the model
     */
    public CollisionShape getShape() {
        return shape;
    }
}
