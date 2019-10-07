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

import com.bulletphysics.collision.shapes.CollisionShape;
import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.model.Mesh;
import de.coreengine.util.gl.IndexBuffer;
import de.coreengine.util.gl.VertexArrayObject;

public class MetaMesh {

    //Data
    public float[] vertices = null, texCoords = null, normals = null, tangents = null, weights = null;
    public int[] indices = null, jointIds = null;
    public MetaMaterial material = null;
    public CollisionShape shape = null;

    /**Creating new mesh instance of the meta model
     *
     * @param texPath Path to get mesh textures from
     * @param asResource Load mesh textures from resources
     * @return New mesh instance
     */
    public Mesh getInstance(String texPath, boolean asResource){

        //Create vao
        VertexArrayObject vao = new VertexArrayObject();
        vao.addVertexBuffer(vertices, 3, 0);
        vao.addVertexBuffer(texCoords, 2, 1);
        vao.addVertexBuffer(normals, 3, 2);
        vao.addVertexBuffer(tangents, 3, 3);
        vao.addVertexBuffer(jointIds, 4, 4);
        vao.addVertexBuffer(weights, 4, 5);

        //Create index buffer
        IndexBuffer indexBuffer = vao.addIndexBuffer(indices);

        //Create material
        Material material = this.material.getInstance(texPath, asResource);

        //Create and return instance
        return new Mesh(vao, indexBuffer, material, shape);
    }
}
