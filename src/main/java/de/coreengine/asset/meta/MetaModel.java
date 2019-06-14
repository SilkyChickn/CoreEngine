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
package de.coreengine.asset.meta;

import com.bulletphysics.collision.shapes.CollisionShape;

/**Meta model file that can be saved in a file
 *
 * @author Darius Dinger
 */
public class MetaModel{
    
    //Data
    private int[][] indices;
    private float[] vertices, texCoords, normals, tangents;
    private MetaMaterial[] materials;
    private CollisionShape shape;
    
    /**@param indices New meta models indices
     */
    public void setIndices(int[][] indices) {
        this.indices = indices;
    }
    
    /**@param vertices New meta models vertices
     */
    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }
    
    /**@param texCoords New meta models texture coordinates
     */
    public void setTexCoords(float[] texCoords) {
        this.texCoords = texCoords;
    }
    
    /**@param normals New meta models normals
     */
    public void setNormals(float[] normals) {
        this.normals = normals;
    }
    
    /**@param tangents New meta models tangents
     */
    public void setTangents(float[] tangents) {
        this.tangents = tangents;
    }
    
    /**@param materials  New meta models materials
     */
    public void setMaterials(MetaMaterial[] materials) {
        this.materials = materials;
    }
    
    /**@param shape New meta models collision shape
     */
    public void setShape(CollisionShape shape) {
        this.shape = shape;
    }
    
    /**@return Models collision shape
     */
    public CollisionShape getShape() {
        return shape;
    }
    
    /**@return Models materials
     */
    public MetaMaterial[] getMaterials() {
        return materials;
    }
    
    /**@return Models indices
     */
    public int[][] getIndices() {
        return indices;
    }
    
    /**@return Models tangents
     */
    public float[] getTangents() {
        return tangents;
    }
    
    /**@return models texture coordinates
     */
    public float[] getTexCoords() {
        return texCoords;
    }
    
    /**@return models vertices
     */
    public float[] getVertices() {
        return vertices;
    }
    
    /**@return models normals
     */
    public float[] getNormals() {
        return normals;
    }
}
