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

import de.coreengine.animation.Joint;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AINode;

import javax.vecmath.Matrix4f;
import java.util.List;

public class NodeParser {

    // Input
    private final AINode aiNode;

    // Output
    private Joint skeleton;

    /**
     * Creates new node data to parse ai nodes
     *
     * @param aiNode AINode to parse
     */
    public NodeParser(AINode aiNode) {
        this.aiNode = aiNode;
    }

    /**
     * Parsing data from the ai node
     * 
     * @param bones Ai Node bones
     */
    public void parse(List<BoneParser> bones) {
        skeleton = createJoint(aiNode, bones);
        skeleton.calcBindPose(null);
        skeleton.calcAnimatedTransformAndPose(null);
    }

    /**
     * Recursively creating joint hierarchy from aiNode.
     *
     * @param aiNode AINode of the joint to create
     * @param bones  Loaded bones of the mesh
     * @return Created hierarchy
     */
    private Joint createJoint(AINode aiNode, List<BoneParser> bones) {

        // Get bone id
        int nodeId = -1;
        String nodeName = aiNode.mName().dataString();
        for (int b = 0; b < bones.size(); b++) {
            if (bones.get(b).getName().equals(nodeName)) {
                nodeId = b;
                break;
            }
        }

        // If bone to node found
        if (nodeId >= 0) {

            // Create this joint
            Matrix4f localBindPose = aiMatToMat(aiNode.mTransformation());
            Joint joint = new Joint(nodeId, nodeName, bones.get(nodeId).getOffsetMatrix(), localBindPose);

            // Create and parse children recursively children
            int childCount = aiNode.mNumChildren();
            for (int i = 0; i < childCount; i++) {
                AINode aiChild = AINode.create(aiNode.mChildren().get(i));
                Joint child = createJoint(aiChild, bones);
                if (child != null)
                    joint.addChild(child);
            }

            return joint;
        } else {

            // Create and parse children recursively children
            int childCount = aiNode.mNumChildren();
            for (int i = 0; i < childCount; i++) {
                AINode aiChild = AINode.create(aiNode.mChildren().get(i));
                Joint child = createJoint(aiChild, bones);
                if (child != null)
                    return child;
            }
        }

        // No root found, returning null
        return null;
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
     * @return Parsed skeleton
     */
    public Joint getSkeleton() {
        return skeleton;
    }
}
