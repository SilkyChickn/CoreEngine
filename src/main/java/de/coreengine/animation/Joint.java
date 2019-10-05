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

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class Joint {

    //Index of the joint in the skeleton
    private int index;

    //Children joints of this joint
    private List<Joint> children = new ArrayList<>();

    //Current transformation of the joint
    private Matrix4f animatedTransform = new Matrix4f();

    //Current local pose of this joint
    private Matrix4f localPose = new Matrix4f();

    //Inverse matrix of joints default position
    private Matrix4f inverseBindMatrix;

    /**Creating new joint
     *
     * @param index Index of the joint in the skeleton
     * @param inverseBindMatrix Inverse matrix of joints default position
     */
    public Joint(int index, Matrix4f inverseBindMatrix){
        this.index = index;
        this.inverseBindMatrix = inverseBindMatrix;
        this.localPose.invert(inverseBindMatrix);
        calcAnimatedTransform();
    }

    /**Creating new joint as copy from the passed joint
     *
     * @param other Joint to copy
     */
    public Joint(Joint other){

        //Copy data
        this.index = other.index;
        this.inverseBindMatrix = new Matrix4f(other.inverseBindMatrix);
        this.localPose.invert(inverseBindMatrix);

        //Recursive for children joints
        for(Joint child: other.children){
            children.add(new Joint(child));
        }

        //Recalc animation transform
        calcAnimatedTransform();
    }

    /**Calculate the animated transformation by multiplying the local pose (transformation matrix of the joint in
     * modelspace) with the inverse bind matrix (inverted default pose).
     */
    public void calcAnimatedTransform() {
        this.animatedTransform.set(localPose);
        this.animatedTransform.mul(inverseBindMatrix);

        //Recursive for children joints
        for(Joint child: children) child.calcAnimatedTransform();
    }

    /**Setting the current local pose matrix of this joint in model space
     *
     * @param localPose Current local pose of this joint
     */
    public void setLocalPose(Matrix4f localPose) {
        this.localPose = localPose;
    }

    /**Adding new child to the joint
     *
     * @param child Child to add
     */
    public void addChild(Joint child){
        children.add(child);
    }

    /**@return Children joint of this joint
     */
    public List<Joint> getChildren() {
        return children;
    }

    /**@return Index if this joint in the skeleton
     */
    public int getIndex() {
        return index;
    }

    /**@return Transformation to put joint into animated pose
     */
    public Matrix4f getAnimatedTransform() {
        return animatedTransform;
    }
}
