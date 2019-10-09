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

package de.coreengine.asset.dataStructures;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.TriangleMeshShape;
import de.coreengine.rendering.model.Mesh;
import de.coreengine.util.ByteArrayUtils;
import de.coreengine.util.Logger;
import de.coreengine.util.bullet.CollisionShapeParser;
import de.coreengine.util.bullet.Physics;
import de.coreengine.util.gl.IndexBuffer;
import de.coreengine.util.gl.VertexArrayObject;

import java.util.Arrays;

public class MeshData {

    //Data
    public float[] vertices = null, texCoords = null, normals = null, tangents = null, weights = null;
    public int[] indices = null, jointIds = null;
    public MaterialData material = null;
    public String shape = null;

    /**Constructing dataStructures mesh from a byte array.<br>
     * <br>
     * Format:<br>
     * First Sector [MetaData]:<br>
     * VerticesSize (int) | TextureCoordinatesSize (int) | NormalsSize (int) | TangentsSize (int) | JointIdsSize (int) |
     * WeightsSize (int) | IndicesSize (int) | MaterialSize (int) | CollisionShapeSize (int)<br>
     * <br>
     * Second Sector [MeshData]:<br>
     * Vertices (float[]) | TextureCoordinates (float[]) | Normals (float[]) | Tangents (float[]) |
     * JointIds (int[]) | Weights (float[]) | Indices (int[])<br>
     * <br>
     * Third Sector [Material]:<br>
     * Material (MaterialData)<br>
     * <br>
     * Fourth Sector [CollisionShape]:<br>
     * CollisionShape (String)<br>
     *
     * @param data Byte array to construct dataStructures mesh from
     */
    public void fromBytes(byte[] data){

        //Get dataStructures data
        byte[] metaDataB = Arrays.copyOfRange(data, 0, 36);
        int[] metaData = ByteArrayUtils.fromBytesi(metaDataB);

        //Get mesh data
        int counter = 36;
        vertices = metaData[0] == 0 ? null :
                ByteArrayUtils.fromBytesf(Arrays.copyOfRange(data, counter, counter += metaData[0]));
        texCoords = metaData[1] == 0 ? null :
                ByteArrayUtils.fromBytesf(Arrays.copyOfRange(data, counter, counter += metaData[1]));
        normals = metaData[2] == 0 ? null :
                ByteArrayUtils.fromBytesf(Arrays.copyOfRange(data, counter, counter += metaData[2]));
        tangents = metaData[3] == 0 ? null :
                ByteArrayUtils.fromBytesf(Arrays.copyOfRange(data, counter, counter += metaData[3]));
        jointIds = metaData[4] == 0 ? null :
                ByteArrayUtils.fromBytesi(Arrays.copyOfRange(data, counter, counter += metaData[4]));
        weights = metaData[5] == 0 ? null :
                ByteArrayUtils.fromBytesf(Arrays.copyOfRange(data, counter, counter += metaData[5]));
        indices = metaData[6] == 0 ? null :
                ByteArrayUtils.fromBytesi(Arrays.copyOfRange(data, counter, counter += metaData[6]));

        //Get material
        material = metaData[7] == 0 ? null : new MaterialData();
        if(material != null) material.fromBytes(Arrays.copyOfRange(data, counter, counter += metaData[7]));

        //Get collision shape
        shape = metaData[8] == 0 ? null : new String(Arrays.copyOfRange(data, counter, counter +metaData[8]));
    }

    /**Converting the dataStructures mesh into a byte array.<br>
     * <br>
     * Format:<br>
     * First Sector [MetaData]:<br>
     * VerticesSize (int) | TextureCoordinatesSize (int) | NormalsSize (int) | TangentsSize (int) | JointIdsSize (int) |
     * WeightsSize (int) | IndicesSize (int) | MaterialSize (int) | CollisionShapeSize (int)<br>
     * <br>
     * Second Sector [MeshParser]:<br>
     * Vertices (float[]) | TextureCoordinates (float[]) | Normals (float[]) | Tangents (float[]) |
     * JointIds (int[]) | Weights (float[]) | Indices (int[])<br>
     * <br>
     * Third Sector [Material]:<br>
     * Material (MaterialData)<br>
     * <br>
     * Fourth Sector [CollisionShape]:<br>
     * CollisionShape (String)<br>
     *
     * @return Converted byte array
     */
    public byte[] toBytes(){

        //Get material and collision shape bytes
        byte[] materialBytes = material == null ? new byte[0] : material.toBytes();
        byte[] shapeBytes = shape == null ? new byte[0] : shape.getBytes();

        //Define dataStructures data
        int[] metaDataI = new int[] {
                vertices == null ? 0 : vertices.length * 4, texCoords == null ? 0 : texCoords.length * 4,
                normals == null ? 0 : normals.length * 4, tangents == null ? 0 : tangents.length * 4,
                jointIds == null ? 0 : jointIds.length * 4, weights == null ? 0 : weights.length * 4,
                indices == null ? 0 : indices.length * 4, material == null ? 0 : materialBytes.length * 4,
                shape == null ? 0 : shapeBytes.length * 4
        };
        byte[] metaData = ByteArrayUtils.toBytes(metaDataI);

        //Define mesh data
        byte[] verticesBytes = vertices == null ? new byte[0] : ByteArrayUtils.toBytes(vertices);
        byte[] texCoordsBytes = texCoords == null ? new byte[0] : ByteArrayUtils.toBytes(texCoords);
        byte[] normalsBytes = normals == null ? new byte[0] : ByteArrayUtils.toBytes(normals);
        byte[] tangentsBytes = tangents == null ? new byte[0] : ByteArrayUtils.toBytes(tangents);
        byte[] jointIdsBytes = jointIds == null ? new byte[0] : ByteArrayUtils.toBytes(jointIds);
        byte[] weightsBytes = weights == null ? new byte[0] : ByteArrayUtils.toBytes(weights);
        byte[] indicesBytes = indices == null ? new byte[0] : ByteArrayUtils.toBytes(indices);

        //Create and return final byte array
        return ByteArrayUtils.combine(metaData, verticesBytes, texCoordsBytes, normalsBytes, tangentsBytes,
                jointIdsBytes, weightsBytes, indicesBytes, materialBytes, shapeBytes);
    }

    /**Creating new mesh instance of the dataStructures model
     *
     * @param texPath Path to get mesh textures from
     * @param asResource Load mesh textures from resources
     * @return New mesh instance
     */
    public Mesh getInstance(String texPath, boolean asResource, boolean animated){

        //Create vao
        VertexArrayObject vao = new VertexArrayObject();
        if(vertices != null) vao.addVertexBuffer(vertices, 3, 0);
        else {
            Logger.warn("Error by creating mesh instance",
                    "The vertices of the dataStructures mesh are null! Returning null!");
            return null;
        }
        if(texCoords != null) vao.addVertexBuffer(texCoords, 2, 1);
        else {
            Logger.warn("Error by creating mesh instance",
                    "The texture coordinates of the dataStructures mesh are null! Returning null!");
            return null;
        }
        if(normals != null) vao.addVertexBuffer(normals, 3, 2);
        else {
            Logger.warn("Error by creating mesh instance",
                    "The normals of the dataStructures mesh are null! Returning null!");
            return null;
        }
        if(tangents != null) vao.addVertexBuffer(tangents, 3, 3);
        else {
            Logger.warn("Error by creating mesh instance",
                    "The tangents of the dataStructures mesh are null! Returning null!");
            return null;
        }
        if(jointIds != null && animated) vao.addVertexBuffer(jointIds, 4, 4);
        else if(jointIds == null){
            Logger.warn("Error by creating mesh instance (animated)",
                    "The joint ids of the dataStructures mesh are null! Returning null!");
            return null;
        }
        if(weights != null && animated) vao.addVertexBuffer(weights, 4, 5);
        else if(weights == null){
            Logger.warn("Error by creating mesh instance (animated)",
                    "The weights of the dataStructures mesh are null! Returning null!");
            return null;
        }

        //Create index buffe
        IndexBuffer indexBuffer = null;
        if(indices != null) indexBuffer = vao.addIndexBuffer(indices);
        else {
            Logger.warn("Error by creating mesh instance",
                    "The indices of the dataStructures mesh are null! Returning null!");
            return null;
        }

        //Create collision shape
        if(shape == null) Logger.warn("Empty collision shape",
                "Collision shape not set, creating convex hull!");
        CollisionShape collisionShape = CollisionShapeParser.toShape(shape);

        if(collisionShape instanceof ConvexHullShape)
            collisionShape = Physics.createConvexHullShape(vertices);
        if(collisionShape instanceof TriangleMeshShape)
            collisionShape = Physics.createTriangleMeshShape(vertices, indices);

        //Finalize
        if(material == null) return new Mesh(vao, indexBuffer, collisionShape);
        else return new Mesh(vao, indexBuffer, this.material.getInstance(texPath, asResource), collisionShape);
    }
}
