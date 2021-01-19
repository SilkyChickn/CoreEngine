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

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

/**
 * Transformation class to store position, rotation and scale and calc the
 * trasnformation matrix
 *
 * @author Darius Dinger
 */
public class Transformation {

    // Has rotation, translation or scale changed since last update
    private boolean recalc = false;

    // Rotation variables of the trans mat
    private float localRotx, localRoty, localRotz;

    // Matrices for the rotations
    private Matrix4f localRotxMat = new Matrix4f(), localRotyMat = new Matrix4f(), localRotzMat = new Matrix4f();

    // Matrix that containsthe rotation
    private Matrix4f localRotMat = new Matrix4f();

    // Matrix that contains the translation
    private Matrix4f localPosMat = new Matrix4f();

    // Matrix that contains the scale
    private Matrix4f localScaleMat = new Matrix4f();

    // Matrix that contains the position and rotation
    private Matrix4f localRotPosMat = new Matrix4f();

    // Local transformation matrix without parent transform
    private Matrix4f localTransMat = new Matrix4f();

    // Transformation matrix for the transformation with
    // Position, rotation and scale
    private Matrix4f transMat = new Matrix4f();

    // Transformation matrix for the transformation with
    // Position, rotation and scale as array
    private float[] transMatArr = new float[16];

    // Transform from jbullet
    private Transform bulletTransform = new Transform();

    // Transformation tree
    private Transformation parent = null;
    private List<Transformation> children = new ArrayList<>();

    /**
     * Creating new transformation and init matrices
     */
    public Transformation() {
        localPosMat.setIdentity();
        localScaleMat.setIdentity();
        transMat.setIdentity();
        localTransMat.setIdentity();
        localRotPosMat.setIdentity();
        recalcTransMat();
    }

    public void addChild(Transformation child) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        children.add(child);
        child.parent = this;
        child.recalcTransMat();
    }

    public void removeChild(Transformation child) {
        child.parent = null;
        children.remove(child);
        child.recalcTransMat();
    }

    /**
     * @return Actual transformation matrix as array
     */
    public float[] getTransMatArr() {

        // Check if matrix changed
        recalcTransMat();

        return transMatArr;
    }

    /**
     * @return Actual transformation matrix for this transformation
     */
    public Matrix4f getTransMat() {

        // Check if matrix changed
        recalcTransMat();

        return transMat;
    }

    /**
     * Setting transformation matrix to rigid body transformation matrix
     * 
     * @param rb Rigid body to get transformation matrix from
     */
    public void setFromRigidBody(RigidBody rb) {
        rb.getMotionState().getWorldTransform(bulletTransform);

        bulletTransform.getMatrix(transMat);
        transMat.mul(localScaleMat);

        bulletTransform.set(transMat);
        bulletTransform.getOpenGLMatrix(transMatArr);
    }

    /**
     * @return X Translation of the transformation
     */
    public float getPosX() {
        return this.localPosMat.m03;
    }

    /**
     * @return Y Translation of the transformation
     */
    public float getPosY() {
        return this.localPosMat.m13;
    }

    /**
     * @return Z Translation of the transformation
     */
    public float getPosZ() {
        return this.localPosMat.m23;
    }

    /**
     * @return X Scale of the transformation
     */
    public float getScaleX() {
        return this.localScaleMat.m00;
    }

    /**
     * @return Y Scale of the transformation
     */
    public float getScaleY() {
        return this.localScaleMat.m11;
    }

    /**
     * @return Z Scale of the transformation
     */
    public float getScaleZ() {
        return this.localScaleMat.m22;
    }

    /**
     * @return X Rotation of the transformation
     */
    public float getRotX() {
        return this.localRotx;
    }

    /**
     * @return Y Rotation of the transformation
     */
    public float getRotY() {
        return this.localRoty;
    }

    /**
     * @return Z Rotation of the transformation
     */
    public float getRotZ() {
        return this.localRotz;
    }

    /**
     * @param val new X Translation of the transformation
     */
    public void setPosX(float val) {
        this.localPosMat.m03 = val;
        this.recalc = true;
    }

    /**
     * @param val new Y Translation of the transformation
     */
    public void setPosY(float val) {
        this.localPosMat.m13 = val;
        this.recalc = true;
    }

    /**
     * @param val new Z Translation of the transformation
     */
    public void setPosZ(float val) {
        this.localPosMat.m23 = val;
        this.recalc = true;
    }

    /**
     * @param val new X Scale of the transformation
     */
    public void setScaleX(float val) {
        this.localScaleMat.m00 = val;
        this.recalc = true;
    }

    /**
     * @param val new Y Scale of the transformation
     */
    public void setScaleY(float val) {
        this.localScaleMat.m11 = val;
        this.recalc = true;
    }

    /**
     * @param val new Z Scale of the transformation
     */
    public void setScaleZ(float val) {
        this.localScaleMat.m22 = val;
        this.recalc = true;
    }

    /**
     * @param val new X Rotation of the transformation
     */
    public void setRotX(float val) {
        this.localRotx = val;
        this.recalc = true;
    }

    /**
     * @param val new Y Rotation of the transformation
     */
    public void setRotY(float val) {
        this.localRoty = val;
        this.recalc = true;
    }

    /**
     * @param val new Z Rotation of the transformation
     */
    public void setRotZ(float val) {
        this.localRotz = val;
        this.recalc = true;
    }

    /**
     * @param val Value to add to the X Translation of the transformation
     */
    public void addPosX(float val) {
        this.localPosMat.m03 += val;
        this.recalc = true;
    }

    /**
     * @param val Value to add to the Y Translation of the transformation
     */
    public void addPosY(float val) {
        this.localPosMat.m13 += val;
        this.recalc = true;
    }

    /**
     * @param val Value to add to the Z Translation of the transformation
     */
    public void addPosZ(float val) {
        this.localPosMat.m23 += val;
        this.recalc = true;
    }

    /**
     * @param val Value to add to the X Scale of the transformation
     */
    public void addScaleX(float val) {
        this.localScaleMat.m00 += val;
        this.recalc = true;
    }

    /**
     * @param val Value to add to the Y Scale of the transformation
     */
    public void addScaleY(float val) {
        this.localScaleMat.m11 += val;
        this.recalc = true;
    }

    /**
     * @param val Value to add to the Z Scale of the transformation
     */
    public void addScaleZ(float val) {
        this.localScaleMat.m22 += val;
        this.recalc = true;
    }

    /**
     * @param val Value to add to the X Rotation of the transformation
     */
    public void addRotX(float val) {
        this.localRotx += val;
        this.recalc = true;
    }

    /**
     * @param val Value to add to the Y Rotation of the transformation
     */
    public void addRotY(float val) {
        this.localRoty += val;
        this.recalc = true;
    }

    /**
     * @param val Value to add to the Z Rotation of the transformation
     */
    public void addRotZ(float val) {
        this.localRotz += val;
        this.recalc = true;
    }

    /**
     * Recalculate the rotation part of the trans mat with the rotx, roty, rotz
     * variables
     * 
     * @return was matrix recalculated
     */
    private void recalcTransMat() {

        // Recalc parent
        if (parent != null) {

            // If parent changed, recalc this too
            parent.recalcTransMat();
        }

        // Recalculation needed
        if (!recalc)
            return;

        // Tell children to recalc
        for (Transformation t : children)
            t.recalc = true;

        // Reclalc rotation matrices
        localRotxMat.rotX((float) Math.toRadians(localRotx));
        localRotyMat.rotY((float) Math.toRadians(localRoty));
        localRotzMat.rotZ((float) Math.toRadians(localRotz));

        // Recalc rotation matrix
        localRotMat.set(localRotxMat);
        localRotMat.mul(localRotyMat);
        localRotMat.mul(localRotzMat);

        // translate and rotate trans mat
        localTransMat.set(localPosMat);
        localTransMat.mul(localRotMat);

        // get current state into rot pos mat
        localRotPosMat.set(localTransMat);

        // Scale transmat
        localTransMat.mul(localScaleMat);

        // Align transMat with parent transMat
        if (this.parent != null)
            transMat.mul(parent.transMat, localTransMat);
        else
            transMat.set(localTransMat);

        // Reset vars
        this.recalc = false;

        // Transfer to transmat array
        bulletTransform.set(transMat);
        bulletTransform.getOpenGLMatrix(transMatArr);
    }
}
