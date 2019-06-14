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

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Matrix4f;

/**Transformation class to store position, rotation and
 * scale and calc the trasnformation matrix
 *
 * @author Darius Dinger
 */
public class Transformation {
    
    //Has rotation or scale changed since last update
    private boolean recalcRotScale = false;
    
    //Has translation changed since last update
    private boolean recalcTrans = false;
    
    //Rotation variables of the trans mat
    private float rotx, roty, rotz;
    
    //Matrix that containsthe rotation
    private Matrix4f rotMat = new Matrix4f();
    
    //Matrix that contains the translation
    private Matrix4f posMat = new Matrix4f();
    
    //Matrix that contains the scale
    private Matrix4f scaleMat = new Matrix4f();
    
    //Matrix that contains the position and rotation
    private Matrix4f rotPosMat = new Matrix4f();
    
    //Transformation matrix for the transformation with
    //Position, rotation and scale
    private Matrix4f transMat = new Matrix4f();
    
    //Matrices for the rotations
    private Matrix4f rotxMat = new Matrix4f(), 
            rotyMat = new Matrix4f(), rotzMat = new Matrix4f();
    
    //Transformation matrix for the transformation with
    //Position, rotation and scale as array
    private float[] transMatArr = new float[16];
    
    //Transform from jbullet
    private Transform bulletTransform = new Transform();
    
    /**Creating new transformation and init matrices
     */
    public Transformation() {
        posMat.setIdentity();
        scaleMat.setIdentity();
        transMat.setIdentity();
        rotPosMat.setIdentity();
    }
    
    /**@return Actual transformation matrix as array
     */
    public float[] getTransMatArr(){
        
        //Check if matrix changed
        if(recalcRotScale) recalcTransMat();
        else if(recalcTrans) transferTranslation();
        
        return transMatArr;
    }
    
    /**@return Matrix that contains transformations position and rotation
     */
    public Matrix4f getRotPosMat() {
        return rotPosMat;
    }
    
    /**@return Actual transformation matrix for this transformation
     */
    public Matrix4f getTransMat(){
        
        //Check if matrix changed
        if(recalcRotScale) recalcTransMat();
        else if(recalcTrans) transferTranslation();
        
        return transMat;
    }
    
    /**Setting transformation matrix to rigid body transformation matrix
     * 
     * @param rb Rigid body to get transformation matrix from
     */
    public void setFromRigidBody(RigidBody rb){
        rb.getMotionState().getWorldTransform(bulletTransform);
        
        bulletTransform.getMatrix(transMat);
        transMat.mul(scaleMat);
        
        bulletTransform.set(transMat);
        bulletTransform.getOpenGLMatrix(transMatArr);
    }
    
    /**@return X Translation of the transformation
     */
    public float getPosX(){
        return this.posMat.m03;
    }
    
    /**@return Y Translation of the transformation
     */
    public float getPosY(){
        return this.posMat.m13;
    }
    
    /**@return Z Translation of the transformation
     */
    public float getPosZ(){
        return this.posMat.m23;
    }
    
    /**@return X Scale of the transformation
     */
    public float getScaleX(){
        return this.scaleMat.m00;
    }
    
    /**@return Y Scale of the transformation
     */
    public float getScaleY(){
        return this.scaleMat.m11;
    }
    
    /**@return Z Scale of the transformation
     */
    public float getScaleZ(){
        return this.scaleMat.m22;
    }
    
    /**@return X Rotation of the transformation
     */
    public float getRotX(){
        return this.rotx;
    }
    
    /**@return Y Rotation of the transformation
     */
    public float getRotY(){
        return this.roty;
    }
    
    /**@return Z Rotation of the transformation
     */
    public float getRotZ(){
        return this.rotz;
    }
    
    /**@param val new X Translation of the transformation
     */
    public void setPosX(float val){
        this.posMat.m03 = val;
        this.recalcTrans = true;
    }
    
    /**@param val new Y Translation of the transformation
     */
    public void setPosY(float val){
        this.posMat.m13 = val;
        this.recalcTrans = true;
    }
    
    /**@param val new Z Translation of the transformation
     */
    public void setPosZ(float val){
        this.posMat.m23 = val;
        this.recalcTrans = true;
    }
    
    /**@param val new X Scale of the transformation
     */
    public void setScaleX(float val){
        this.scaleMat.m00 = val;
        this.recalcRotScale = true;
    }
    
    /**@param val new Y Scale of the transformation
     */
    public void setScaleY(float val){
        this.scaleMat.m11 = val;
        this.recalcRotScale = true;
    }
    
    /**@param val new Z Scale of the transformation
     */
    public void setScaleZ(float val){
        this.scaleMat.m22 = val;
        this.recalcRotScale = true;
    }
    
    /**@param val new X Rotation of the transformation
     */
    public void setRotX(float val){
        this.rotx = val;
        this.recalcRotScale = true;
    }
    
    /**@param val new Y Rotation of the transformation
     */
    public void setRotY(float val){
        this.roty = val;
        this.recalcRotScale = true;
    }
    
    /**@param val new Z Rotation of the transformation
     */
    public void setRotZ(float val){
        this.rotz = val;
        this.recalcRotScale = true;
    }
    
    /**@param val Value to add to the X Translation of the transformation
     */
    public void addPosX(float val){
        this.posMat.m03 += val;
        this.recalcTrans = true;
    }
    
    /**@param val Value to add to the Y Translation of the transformation
     */
    public void addPosY(float val){
        this.posMat.m13 += val;
        this.recalcTrans = true;
    }
    
    /**@param val Value to add to the Z Translation of the transformation
     */
    public void addPosZ(float val){
        this.posMat.m23 += val;
        this.recalcTrans = true;
    }
    
    /**@param val Value to add to the X Scale of the transformation
     */
    public void addScaleX(float val){
        this.scaleMat.m00 += val;
        this.recalcRotScale = true;
    }
    
    /**@param val Value to add to the Y Scale of the transformation
     */
    public void addScaleY(float val){
        this.scaleMat.m11 += val;
        this.recalcRotScale = true;
    }
    
    /**@param val Value to add to the Z Scale of the transformation
     */
    public void addScaleZ(float val){
        this.scaleMat.m22 += val;
        this.recalcRotScale = true;
    }
    
    /**@param val Value to add to the X Rotation of the transformation
     */
    public void addRotX(float val){
        this.rotx += val;
        this.recalcRotScale = true;
    }
    
    /**@param val Value to add to the Y Rotation of the transformation
     */
    public void addRotY(float val){
        this.roty += val;
        this.recalcRotScale = true;
    }
    
    /**@param val Value to add to the Z Rotation of the transformation
     */
    public void addRotZ(float val){
        this.rotz += val;
        this.recalcRotScale = true;
    }
    
    /**Recalculate the rotation part of the trans mat with the rotx, roty, rotz
     * variables
     */
    private void recalcTransMat(){
        
        //Reclalc rotation matrices
        rotxMat.rotX((float) Math.toRadians(rotx));
        rotyMat.rotY((float) Math.toRadians(roty));
        rotzMat.rotZ((float) Math.toRadians(rotz));
        
        //Recalc rotation matrix
        rotMat.set(rotxMat);
        rotMat.mul(rotyMat);
        rotMat.mul(rotzMat);
        
        //translate and rotate trans mat
        transMat.set(posMat);
        transMat.mul(rotMat);
        
        //get currentstate into rot pos mat
        rotPosMat.set(transMat);
        
        //Scale transmat
        transMat.mul(scaleMat);
        
        //Reset vars
        this.recalcRotScale = false;
        this.recalcTrans = false;
        
        //Transfer to transmat array
        bulletTransform.set(transMat);
        bulletTransform.getOpenGLMatrix(transMatArr);
    }
    
    /**Transfer translation from posScaleMat into transMat
     */
    private void transferTranslation(){
        transMat.m03 = (posMat.m03);
        transMat.m13 = (posMat.m13);
        transMat.m23 = (posMat.m23);
        
        transMatArr[12] = posMat.m03;
        transMatArr[13] = posMat.m13;
        transMatArr[14] = posMat.m23;
        
        rotPosMat.m03 = (posMat.m03);
        rotPosMat.m13 = (posMat.m13);
        rotPosMat.m23 = (posMat.m23);
    }
}
