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

package de.coreengine.animation;

import de.coreengine.util.ByteArrayUtils;
import de.coreengine.util.Pair;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Joint {

    // Index of the joint in the skeleton
    private int index;

    // Name of this joint
    private String name;

    // Parent joint
    private Joint parent = null;

    // Children joints of this joint
    private List<Joint> children = new ArrayList<>();

    // Current transformation of the joint
    private Matrix4f animatedTransform = new Matrix4f();

    // Current local pose of this joint
    private Matrix4f localPose = new Matrix4f();

    // Local bind pose of the joint
    private Matrix4f bindLocalPose = new Matrix4f();

    // Inverse matrix of joints default position
    private Matrix4f inverseBindMatrix = new Matrix4f();

    // Debug: Bind and current pose
    private Matrix4f bindPose = new Matrix4f();
    private Matrix4f pose = new Matrix4f();

    /**
     * Creating new joint
     *
     * @param index             Index of the joint in the skeleton
     * @param name              Name of the joint
     * @param inverseBindMatrix Inverse matrix of joints default position
     * @param bindLocalPose     Matrix of joints binding local pose
     */
    public Joint(int index, String name, Matrix4f inverseBindMatrix, Matrix4f bindLocalPose) {
        this.index = index;
        this.name = name;
        this.inverseBindMatrix.set(inverseBindMatrix);
        this.localPose.set(bindLocalPose);
        this.bindLocalPose.set(bindLocalPose);
        this.animatedTransform.setIdentity();
    }

    /**
     * Creating new joint as copy from the passed joint
     *
     * @param other Joint to copy
     */
    public Joint(Joint other) {

        // Copy data
        this.index = other.index;
        this.name = other.name;
        this.inverseBindMatrix.set(other.inverseBindMatrix);
        this.bindLocalPose.set(other.bindLocalPose);
        this.localPose.set(other.localPose);
        this.animatedTransform.set(other.animatedTransform);
        this.bindPose = new Matrix4f(other.bindPose);
        this.pose = new Matrix4f(other.pose);

        // Recursive for children joints
        for (Joint child : other.children) {
            addChild(new Joint(child));
        }
    }

    /**
     * Calculate the bind pose transformation by multiplying the local bind pose
     * (bind transformation matrix of the joint in bone space) with all parents
     * local bind poses.
     *
     * @param parentPose Parents local bind pose or null, if root
     */
    public void calcBindPose(Matrix4f parentPose) {
        if (parentPose == null)
            this.bindPose.setIdentity();
        else
            this.bindPose.set(parentPose);
        this.bindPose.mul(bindLocalPose);
        for (Joint child : children)
            child.calcBindPose(this.bindPose);
    }

    /**
     * Calculate the animated and pose transformation by multiplying the local pose
     * (transformation matrix of the joint in bone space) with all parents local
     * poses and the inverse bind matrix (inverted pose in model space).
     *
     * @param parentLocalPose Parents local pose or null, if root
     */
    public void calcAnimatedTransformAndPose(Matrix4f parentLocalPose) {

        // Calc pose for this joint
        if (parentLocalPose == null)
            this.animatedTransform.setIdentity();
        else
            this.animatedTransform.set(parentLocalPose);
        this.animatedTransform.mul(localPose);

        // Save current pose
        this.pose.set(this.animatedTransform);

        // Calc animated transform for children
        for (Joint child : children)
            child.calcAnimatedTransformAndPose(this.animatedTransform);

        // Calc animation transform
        this.animatedTransform.mul(inverseBindMatrix);
    }

    /**
     * @return Current local pose of the joint
     */
    public Matrix4f getLocalPose() {
        return localPose;
    }

    /**
     * Getting first found joint in this hierarchy with this name. If joint couldn't
     * be found returning null.
     *
     * @param name Name to find
     * @return Found joint or null
     */
    public Joint getByName(String name) {

        // Get node with this name
        if (name.equals(this.name))
            return this;
        else {
            for (Joint child : children) {
                Joint result = child.getByName(name);
                if (result != null)
                    return result;
            }
        }

        // Joint with this name not found in this hierarchy
        return null;
    }

    /**
     * Setting the current local pose matrix of this joint in model space
     *
     * @param localPose Current local pose of this joint
     */
    public void setLocalPose(Matrix4f localPose) {
        this.localPose = localPose;
    }

    /**
     * Adding new child to the joint
     *
     * @param child Child to add
     */
    public void addChild(Joint child) {
        child.parent = this;
        children.add(child);
    }

    /**
     * @return Children joint of this joint
     */
    public List<Joint> getChildren() {
        return children;
    }

    /**
     * @return Index if this joint in the skeleton
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return Transformation to put a vertex into the animated pose
     */
    public Matrix4f getAnimatedTransform() {
        return animatedTransform;
    }

    /**
     * @return Name of the joint
     */
    public String getName() {
        return name;
    }

    /**
     * @return Bind transformation matrix of the joint in model space
     */
    public Matrix4f getBindPose() {
        return bindPose;
    }

    /**
     * @return Current transformation matrix of the joint in model space
     */
    public Matrix4f getPose() {
        return pose;
    }

    /**
     * Converting recursively this joint and all children into a byte array.<br>
     * <br>
     * Format:<br>
     * First Sector [MetaData]:<br>
     * JointCount (int) | Joint0NameSize (int) | Joint1NameSize (int) | ...<br>
     * <br>
     * Second Sector [JointData]:<br>
     * Joint0Index (int) | Joint0ParentIndex (int) | Joint0Name (String) |
     * Joint0InverseBindMatrix (float[]) | Joint0BindLocalPoseMatrix (float[]) |
     * Joint1Index (int) | ...<br>
     *
     * @return Converted byte array
     */
    public byte[] toBytes() {

        // Get all joints from the hierarchy
        List<Joint> allJoints = new ArrayList<>();
        getAllJoints(allJoints);

        // Get joint data
        byte[][] jointDataA = new byte[allJoints.size()][];
        int[] jointNameSizesI = new int[allJoints.size()];
        for (int i = 0; i < allJoints.size(); i++) {
            Joint joint = allJoints.get(i);

            byte[] indexes = ByteArrayUtils
                    .toBytes(new int[] { joint.index, joint.parent == null ? -1 : joint.parent.index });
            byte[] name = joint.name.getBytes();
            byte[] matrices = ByteArrayUtils.toBytes(new Matrix4f[] { joint.inverseBindMatrix, joint.bindLocalPose });

            jointDataA[i] = ByteArrayUtils.combine(indexes, name, matrices);
            jointNameSizesI[i] = joint.name.length();
        }
        byte[] jointData = ByteArrayUtils.combine(jointDataA);

        // Create meta data
        byte[] jointNameSizes = ByteArrayUtils.toBytes(jointNameSizesI);
        byte[] jointCount = ByteArrayUtils.toBytes(new int[] { allJoints.size() });

        // Combine and return
        return ByteArrayUtils.combine(jointCount, jointNameSizes, jointData);
    }

    /**
     * Constructing this joint hierarchy from bytes.<br>
     * <br>
     * Format:<br>
     * First Sector [MetaData]:<br>
     * JointCount (int) | Joint0NameSize (int) | Joint1NameSize (int) | ...<br>
     * <br>
     * Second Sector [JointData]:<br>
     * Joint0Index (int) | Joint0ParentIndex (int) | Joint0Name (String) |
     * Joint0InverseBindMatrix (float[]) | Joint0BindLocalPoseMatrix (float[]) |
     * Joint1Index (int) | ...<br>
     *
     * @param data Bytes to construct hierarchy from
     */
    public void fromBytes(byte[] data) {

        // Get meta data
        int counter = 0;
        int jointCount = ByteArrayUtils.fromBytesi(Arrays.copyOfRange(data, counter, counter += 4))[0];
        int[] jointNameSizes = ByteArrayUtils
                .fromBytesi(Arrays.copyOfRange(data, counter, counter += (4 * jointCount)));

        // Get joint data
        HashMap<Integer, Pair<Joint, Integer>> allJoints = new HashMap<>();
        for (int i = 0; i < jointCount; i++) {

            int[] jointIndexes = ByteArrayUtils.fromBytesi(Arrays.copyOfRange(data, counter, counter += 8));
            String name = new String(Arrays.copyOfRange(data, counter, counter += jointNameSizes[i]));
            Matrix4f[] matrices = ByteArrayUtils.fromBytesm4(Arrays.copyOfRange(data, counter, counter += 128));

            // Check if read joint is root (parent = -1)
            if (jointIndexes[1] == -1) {

                // If its root, setup this joint with data
                this.index = jointIndexes[0];
                this.name = name;
                this.parent = null;
                this.children.clear();
                this.inverseBindMatrix.set(matrices[0]);
                this.localPose.set(matrices[1]);
                this.bindLocalPose.set(matrices[1]);
                this.animatedTransform.setIdentity();
            } else {

                // No root, add to list
                Joint joint = new Joint(jointIndexes[0], name, matrices[0], matrices[1]);
                allJoints.put(jointIndexes[0], new Pair<>(joint, jointIndexes[1]));
            }
        }

        // Construct hierarchy
        for (Integer jointId : allJoints.keySet()) {
            Pair<Joint, Integer> curJoint = allJoints.get(jointId);
            if (curJoint.getValue() == this.index)
                addChild(curJoint.getKey());
            else
                allJoints.get(curJoint.getValue()).getKey().addChild(curJoint.getKey());
        }
    }

    /**
     * Adding this and all children joints recursively into a list
     *
     * @param listToAdd List to add joints
     */
    private void getAllJoints(List<Joint> listToAdd) {
        listToAdd.add(this);
        for (Joint child : children)
            child.getAllJoints(listToAdd);
    }
}
