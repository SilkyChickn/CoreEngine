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
package de.coreengine.network.syncronized;

import com.bulletphysics.dynamics.RigidBody;
import de.coreengine.network.Syncronized;
import de.coreengine.rendering.model.Transformation;
import de.coreengine.util.Toolbox;

import javax.vecmath.Matrix4f;

/**
 * Transformation that can be syncronized in a network
 *
 * @author Darius Dinger
 */
public class SyncTransformation extends Syncronized {
    private Transformation val = new Transformation();

    /**
     * @param tag Tag of the syncronized float
     */
    public SyncTransformation(String tag) {
        super(tag);
    }

    /**
     * Adding value to the x position
     * 
     * @param v Value to add to x
     */
    public void addPosX(float v) {
        val.addPosX(v);
        change();
    }

    /**
     * Adding value to the y position
     * 
     * @param v Value to add to y
     */
    public void addPosY(float v) {
        val.addPosY(v);
        change();
    }

    /**
     * Adding value to the z position
     * 
     * @param v Value to add to z
     */
    public void addPosZ(float v) {
        val.addPosZ(v);
        change();
    }

    /**
     * Setting value to the x position
     * 
     * @param v Value to set to x
     */
    public void setPosX(float v) {
        val.setPosX(v);
        change();
    }

    /**
     * Setting value to the y position
     * 
     * @param v Value to set to y
     */
    public void setPosY(float v) {
        val.setPosY(v);
        change();
    }

    /**
     * Setting value to the z position
     * 
     * @param v Value to set to z
     */
    public void setPosZ(float v) {
        val.setPosZ(v);
        change();
    }

    /**
     * Adding value to the x scale
     * 
     * @param v Value to add to x
     */
    public void addScaleX(float v) {
        val.addScaleX(v);
        change();
    }

    /**
     * Adding value to the y scale
     * 
     * @param v Value to add to y
     */
    public void addScaleY(float v) {
        val.addScaleY(v);
        change();
    }

    /**
     * Adding value to the z scale
     * 
     * @param v Value to add to z
     */
    public void addScaleZ(float v) {
        val.addScaleZ(v);
        change();
    }

    /**
     * Setting value to the x scale
     * 
     * @param v Value to set to x
     */
    public void setScaleX(float v) {
        val.setScaleX(v);
        change();
    }

    /**
     * Setting value to the y scale
     * 
     * @param v Value to set to y
     */
    public void setScaleY(float v) {
        val.setScaleY(v);
        change();
    }

    /**
     * Setting value to the z scale
     * 
     * @param v Value to set to z
     */
    public void setScaleZ(float v) {
        val.setScaleZ(v);
        change();
    }

    /**
     * Adding value to the x rotation
     * 
     * @param v Value to add to x
     */
    public void addRotX(float v) {
        val.addRotX(v);
        change();
    }

    /**
     * Adding value to the y rotation
     * 
     * @param v Value to add to y
     */
    public void addRotY(float v) {
        val.addRotY(v);
        change();
    }

    /**
     * Adding value to the z rotation
     * 
     * @param v Value to add to z
     */
    public void addRotZ(float v) {
        val.addRotZ(v);
        change();
    }

    /**
     * Setting value to the x rotation
     * 
     * @param v Value to set to x
     */
    public void setRotX(float v) {
        val.setRotX(v);
        change();
    }

    /**
     * Setting value to the y rotation
     * 
     * @param v Value to set to y
     */
    public void setRotY(float v) {
        val.setRotY(v);
        change();
    }

    /**
     * Setting value to the z rotation
     * 
     * @param v Value to set to z
     */
    public void setRotZ(float v) {
        val.setRotZ(v);
        change();
    }

    /**
     * Setting transformation from rigidbody transformation
     * 
     * @param rb Rigidbody to get transformation from
     */
    public void setFromRigidbody(RigidBody rb) {
        val.setFromRigidBody(rb);
        change();
    }

    /**
     * Setting value to the x position
     * 
     * @return x position
     */
    public float getPosX() {
        return val.getPosX();
    }

    /**
     * Setting value to the y position
     * 
     * @return y position
     */
    public float getPosY() {
        return val.getPosY();
    }

    /**
     * Setting value to the z position
     * 
     * @return z position
     */
    public float getPosZ() {
        return val.getPosZ();
    }

    /**
     * Setting value to the x scale
     * 
     * @return x scale
     */
    public float getScaleX() {
        return val.getScaleX();
    }

    /**
     * Setting value to the y scale
     * 
     * @return y scale
     */
    public float getScaleY() {
        return val.getScaleY();
    }

    /**
     * Setting value to the z scale
     * 
     * @return z scale
     */
    public float getScaleZ() {
        return val.getScaleZ();
    }

    /**
     * Setting value to the x rotation
     * 
     * @return x rotation
     */
    public float getRotX() {
        return val.getRotX();
    }

    /**
     * Setting value to the y rotation
     * 
     * @return y rotation
     */
    public float getRotY() {
        return val.getRotY();
    }

    /**
     * Setting value to the z rotation
     * 
     * @return z rotation
     */
    public float getRotZ() {
        return val.getRotZ();
    }

    /**
     * @return Transformation matrix as array
     */
    public float[] getTransMatArr() {
        return val.getTransMatArr();
    }

    /**
     * @return Transformation matrix
     */
    public Matrix4f getTransMat() {
        return val.getTransMat();
    }

    @Override
    protected void sync(String sync) {
        String[] data = sync.split(" ");
        val.setPosX(Float.parseFloat(data[0]));
        val.setPosY(Float.parseFloat(data[1]));
        val.setPosZ(Float.parseFloat(data[2]));

        val.setRotX(Float.parseFloat(data[3]));
        val.setRotY(Float.parseFloat(data[4]));
        val.setRotZ(Float.parseFloat(data[5]));

        val.setScaleX(Float.parseFloat(data[6]));
        val.setScaleY(Float.parseFloat(data[7]));
        val.setScaleZ(Float.parseFloat(data[8]));

        val.getTransMat().set(Toolbox.stringToArrayf(data[9], "-"));
    }

    @Override
    protected String sync() {
        return val.getPosX() + " " + val.getPosY() + " " + val.getPosZ() + " " + val.getRotX() + " " + val.getRotY()
                + " " + val.getRotZ() + " " + val.getScaleX() + " " + val.getScaleY() + " " + val.getScaleZ() + " "
                + Toolbox.arrayToString(val.getTransMatArr(), "-");
    }
}
