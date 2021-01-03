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

import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIVertexWeight;

import de.coreengine.util.Pair;

import javax.vecmath.Matrix4f;

public class BoneParser {

    // Input
    private final AIBone aiBone;

    // Output
    private String name;
    private Matrix4f offsetMatrix;
    private Pair<Integer, Float>[] effectedVertices;

    /**
     * Creating new bone data that can parse data from an ai bone
     *
     * @param aiBone AIBone to parse
     */
    public BoneParser(AIBone aiBone) {
        this.aiBone = aiBone;
    }

    /**
     * Parsing ai bone
     */
    public void parse() {

        // Get bone name
        name = aiBone.mName().dataString();

        // Get matrix
        offsetMatrix = aiMatToMat(aiBone.mOffsetMatrix());

        // Get effected vertices
        int vertexCount = aiBone.mNumWeights();
        effectedVertices = new Pair[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            AIVertexWeight aiVertexWeight = aiBone.mWeights().get(i);
            effectedVertices[i] = new Pair<>(aiVertexWeight.mVertexId(), aiVertexWeight.mWeight());
        }
    }

    /**
     * Converting AIMatrix4x4 into a Matrix4f
     *
     * @param aiMat AIMatrix4x4 input
     * @return Matrix4f output
     */
    private Matrix4f aiMatToMat(AIMatrix4x4 aiMat) {
        return new Matrix4f(aiMat.a1(), aiMat.a2(), aiMat.a3(), aiMat.a4(), aiMat.b1(), aiMat.b2(), aiMat.b3(),
                aiMat.b4(), aiMat.c1(), aiMat.c2(), aiMat.c3(), aiMat.c4(), aiMat.d1(), aiMat.d2(), aiMat.d3(),
                aiMat.d4());
    }

    /**
     * @return Parsed effected vertices with its weight value
     */
    public Pair<Integer, Float>[] getEffectedVertices() {
        return effectedVertices;
    }

    /**
     * @return Parsed offset matrix
     */
    public Matrix4f getOffsetMatrix() {
        return offsetMatrix;
    }

    /**
     * @return Parsed bones name
     */
    public String getName() {
        return name;
    }
}
