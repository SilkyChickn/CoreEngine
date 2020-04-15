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

package de.coreengine.asset.modelLoader;

import de.coreengine.asset.dataStructures.MaterialData;
import de.coreengine.asset.dataStructures.MeshData;
import javafx.util.Pair;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MeshParser {

    // Input
    private final AIMesh aiMesh;
    private final MaterialData[] materials;

    // Output
    private MeshData meshData = null;

    /**
     * Creating new mesh data that can parse ai meshes into meshes and
     * dataStructures meshes
     *
     * @param aiMesh    AIMesh to parse
     * @param materials Materials of the AIScene
     */
    public MeshParser(AIMesh aiMesh, MaterialData[] materials) {
        this.aiMesh = aiMesh;
        this.materials = materials;
    }

    /**
     * Parse ai meshes into meshes and dataStructures meshes
     *
     * @param bones          Bone list to add bones or null to dont load bones
     * @param collisionShape Collision shape to use
     */
    public void parse(String collisionShape, List<BoneParser> bones) {

        // Get material, load empty material if id not exist
        MaterialData material;
        if (aiMesh.mMaterialIndex() >= 0 && aiMesh.mMaterialIndex() < materials.length) {
            material = materials[aiMesh.mMaterialIndex()];
        } else {
            material = new MaterialData();
        }

        // Get data from mesh
        float[] vertices = bufferToArray(aiMesh.mVertices(), true);
        float[] texCoords = bufferToArray(Objects.requireNonNull(aiMesh.mTextureCoords(0)), false);
        float[] normals = bufferToArray(Objects.requireNonNull(aiMesh.mNormals()), true);
        float[] tangents = bufferToArray(Objects.requireNonNull(aiMesh.mTangents()), true);
        int[] indices = getIndices();

        // Load bones if requested
        int[] jointIds = new int[aiMesh.mNumVertices() * 4];
        float[] weights = new float[aiMesh.mNumVertices() * 4];
        if (bones != null) {

            // Load and parse bones
            int boneCount = aiMesh.mNumBones();
            for (int i = 0; i < boneCount; i++) {
                AIBone aiBone = AIBone.create(aiMesh.mBones().get(i));
                BoneParser bone = new BoneParser(aiBone);
                bone.parse();
                bones.add(bone);
            }

            // Fill up arrays
            // Iterate through all vertices
            for (int vertexId = 0; vertexId < aiMesh.mNumVertices(); vertexId++) {

                // List to store all joints that effect this vertex
                List<Pair<Integer, Float>> joints = new ArrayList<>();

                // Iterate through bones
                for (int jointId = 0; jointId < bones.size(); jointId++) {
                    BoneParser bone = bones.get(jointId);

                    // Iterate through bones effected vertices, to see if this vertex is effected by
                    // this bone
                    for (Pair<Integer, Float> effectedVertex : bone.getEffectedVertices()) {

                        // If this vertex gets effected by this bone -> add to effected bones/joints
                        if (effectedVertex.getKey() == vertexId) {
                            joints.add(new Pair<>(jointId, effectedVertex.getValue()));
                        }
                    }
                }

                // Add joints to joint ids and weights
                for (int i = 0; i < 4; i++) {
                    int id = 0;
                    float weight = 0;
                    if (i < joints.size()) {
                        id = joints.get(i).getKey();
                        weight = joints.get(i).getValue();
                    }
                    jointIds[vertexId * 4 + i] = id;
                    weights[vertexId * 4 + i] = weight;
                }
            }
        }

        // Construct dataStructures mesh
        meshData = new MeshData();
        meshData.vertices = vertices;
        meshData.texCoords = texCoords;
        meshData.normals = normals;
        meshData.tangents = tangents;
        meshData.indices = indices;
        meshData.material = material;
        meshData.shape = collisionShape;
        if (bones != null)
            meshData.jointIds = jointIds;
        if (bones != null)
            meshData.weights = weights;
    }

    /**
     * Extract indices from ai scene
     *
     * @return Indices array
     */
    private int[] getIndices() {
        int faceCount = aiMesh.mNumFaces();
        int[] indices = new int[faceCount * 3];
        for (int i = 0; i < faceCount; i++) {
            for (int j = 0; j < 3; j++) {
                indices[i * 3 + j] = aiMesh.mFaces().get(i).mIndices().get(j);
            }
        }
        return indices;
    }

    /**
     * Transfer float from vec3d buffer to a float array
     *
     * @param buffer  Buffer to convert
     * @param loadAll If true the whole vector will be loaded, else only x and y
     * @return Float from vec3d buffer as (x0, y0, (z0), x1, y1, (z1), ...)
     */
    private float[] bufferToArray(AIVector3D.Buffer buffer, boolean loadAll) {
        int c = 0, s = (loadAll ? 3 : 2);
        float[] out = new float[buffer.remaining() * s];
        while (buffer.remaining() > 0) {
            AIVector3D vector = buffer.get();
            out[c * s] = vector.x();
            out[c * s + 1] = vector.y();
            if (loadAll)
                out[c * s + 2] = vector.z();
            c++;
        }
        return out;
    }

    /**
     * @return Parsed dataStructures mesh
     */
    public MeshData getMeshData() {
        return meshData;
    }
}
