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
package de.coreengine.rendering.renderable;

import de.coreengine.framework.Window;
import de.coreengine.util.CameraRay;
import de.coreengine.util.Configuration;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

/**
 * Represent a moveable camera in the 3d world
 *
 * @author Darius Dinger
 */
public class Camera {

    // Was camera moved or rotated since the last update
    private boolean moved = false, rotatedX = false, rotatedY = false, rotatedZ = false;

    // Has camera perspective changed
    private boolean perspectiveChanged = false;

    // Position of the camera in the 3d world
    private final Matrix4f trans = new Matrix4f();
    private final Vector3f position = new Vector3f();

    // Inverted position of the camera in the 3d world for faster matrix
    // calculations
    private final Vector3f invertedPosition = new Vector3f();

    // Rotation of the camera around the x axis
    private final Matrix4f rotX = new Matrix4f();
    private float pitch = 0.0f;

    // Rotation of the camera around the y axis
    private final Matrix4f rotY = new Matrix4f();
    private float yaw = 0.0f;

    // Rotation of the camera around the z axis
    private final Matrix4f rotZ = new Matrix4f();
    private float roll = 0.0f;

    // Cameras view matrix and model matrix, taht facing the camera
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f inverseViewMatrix = new Matrix4f();
    private final Matrix4f facingModelMatrix = new Matrix4f();

    // Cameras projection matrix
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f inverseProjectionMatrix = new Matrix4f();

    // Camera view-projection matrices
    private final Matrix4f viewProjectionMatrix = new Matrix4f();

    // Camera prpjection far plane
    private float far_plane = Configuration.getValuef("CAMERA_DEFAULT_FAR_PLANE");

    // Camera projection near plane
    private float near_plane = Configuration.getValuef("CAMERA_DEFAULT_NEAR_PLANE");

    // Camera projection field of view
    private float fov = Configuration.getValuef("CAMERA_DEFAULT_FOV");

    // Direction, the camera is looking to
    private CameraRay ray = new CameraRay();

    /**
     * Creating new camera at (0, 0, 0) with no rotation Reclaculate all matrices
     */
    public Camera() {

        // Adding aspect changed listener
        Window.addWindowListener((x, y, aspect) -> perspectiveChanged = true);

        // Reset matrices
        trans.setIdentity();
        rotX.setIdentity();
        rotY.setIdentity();
        rotZ.setIdentity();
        facingModelMatrix.setIdentity();
        viewMatrix.setIdentity();
        projectionMatrix.setIdentity();
        viewProjectionMatrix.setIdentity();

        // Recalculate matrices
        recalcProjectionMatrix();
        recalcViewMatrix();
        recalcViewProjectionMatrix();
    }

    /**
     * Check if camera was moved. if then and update matrices setting moved and
     * rotated variables to false
     */
    public void updateViewMatrix() {

        if (moved || rotatedX || rotatedY || rotatedZ) {

            recalcViewMatrix();

            if (perspectiveChanged) {
                recalcProjectionMatrix();
                perspectiveChanged = false;
            }

            recalcViewProjectionMatrix();
        } else {
            if (perspectiveChanged) {
                recalcProjectionMatrix();
                recalcViewProjectionMatrix();
                perspectiveChanged = false;
            }
        }

    }

    /**
     * Recalculate view projection matrix from view andprojection matrix from this
     * camera
     */
    public final void recalcViewProjectionMatrix() {
        viewProjectionMatrix.mul(projectionMatrix, viewMatrix);
        ray.recalcRay(inverseViewMatrix, inverseProjectionMatrix);
    }

    /**
     * Recalculate view matrix from position and rotation from this camera
     */
    public final void recalcViewMatrix() {

        if (moved) {
            moved = false;
            trans.setTranslation(invertedPosition);
        }

        if (rotatedX) {
            rotatedX = false;
            rotX.rotX((float) Math.toRadians(pitch));
        }

        if (rotatedY) {
            rotatedY = false;
            rotY.rotY((float) Math.toRadians(yaw));
        }

        if (rotatedZ) {
            rotatedZ = false;
            rotZ.rotZ((float) Math.toRadians(roll));
        }

        viewMatrix.set(rotX);
        viewMatrix.mul(rotY);
        viewMatrix.mul(rotZ);
        viewMatrix.mul(trans);

        facingModelMatrix.m00 = viewMatrix.m00;
        facingModelMatrix.m01 = viewMatrix.m10;
        facingModelMatrix.m02 = viewMatrix.m20;
        facingModelMatrix.m10 = viewMatrix.m01;
        facingModelMatrix.m11 = viewMatrix.m11;
        facingModelMatrix.m12 = viewMatrix.m21;
        facingModelMatrix.m20 = viewMatrix.m02;
        facingModelMatrix.m21 = viewMatrix.m12;
        facingModelMatrix.m22 = viewMatrix.m22;

        inverseViewMatrix.invert(viewMatrix);
    }

    /**
     * Recalculate projection matrix from perspective settings from this camera and
     * the actual aspect from the window
     */
    private void recalcProjectionMatrix() {

        float x_scale = (float) (1f / Math.tan(Math.toRadians(fov / 2f)));
        float y_scale = x_scale * Window.getAspect();
        float frustum_length = far_plane - near_plane;

        projectionMatrix.setIdentity();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((near_plane + far_plane) / frustum_length);
        projectionMatrix.m23 = -((2 * near_plane * far_plane) / frustum_length);
        projectionMatrix.m32 = -1;
        projectionMatrix.m33 = 0;

        inverseProjectionMatrix.invert(projectionMatrix);
    }

    /**
     * Setting new camera pitch and sets camera rotated to true if it has changed
     * 
     * @param pitch New camera pitch
     */
    public void setPitch(float pitch) {
        if (pitch != this.pitch) {
            rotatedX = true;
            this.pitch = pitch % 360.0f;
        }
    }

    /**
     * Setting new camera yaw and sets camera rotated to true if it has changed
     * 
     * @param yaw New camera yaw
     */
    public void setYaw(float yaw) {
        if (yaw != this.yaw) {
            rotatedY = true;
            this.yaw = yaw % 360.0f;
        }
    }

    /**
     * Setting new camera roll and sets camera rotated to true if it has changed
     * 
     * @param roll New camera roll
     */
    public void setRoll(float roll) {
        if (roll != this.roll) {
            rotatedZ = true;
            this.roll = roll % 360.0f;
        }
    }

    /**
     * Setting new camera x position and sets moved to true if it has changed
     * 
     * @param x New camera x position
     */
    public void setX(float x) {
        if (x != this.position.x) {
            moved = true;
            this.position.x = x;
            this.invertedPosition.x = -x;
        }
    }

    /**
     * Setting new camera y position and sets moved to true if it has changed
     * 
     * @param y New camera y position
     */
    public void setY(float y) {
        if (y != this.position.y) {
            moved = true;
            this.position.y = y;
            this.invertedPosition.y = -y;
        }
    }

    /**
     * Setting new camera z position and sets moved to true if it has changed
     * 
     * @param z New camera z position
     */
    public void setZ(float z) {
        if (z != this.position.z) {
            moved = true;
            this.position.z = z;
            this.invertedPosition.z = -z;
        }
    }

    /**
     * Setting new camera far plane
     * 
     * @param farPlane New camera far plane
     */
    public void setFarPlane(float farPlane) {
        if (farPlane != this.far_plane) {
            perspectiveChanged = true;
            this.far_plane = farPlane;
        }
    }

    /**
     * Setting new camera near plane
     * 
     * @param nearPlane New camera near plane
     */
    public void setNearPlane(float nearPlane) {
        if (nearPlane != this.near_plane) {
            perspectiveChanged = true;
            this.near_plane = nearPlane;
        }
    }

    /**
     * Setting new camera field of view
     * 
     * @param fov New camera fov
     */
    public void setFov(float fov) {
        if (fov != this.fov) {
            perspectiveChanged = true;
            this.fov = fov;
        }
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    /**
     * Gets cameras field of view
     *
     * @return New cameras fov
     */
    public float getFov() {
        return fov;
    }

    /**
     * Gets cameras near plane distance
     *
     * @return Cameras near plane distance
     */
    public float getNearPlane() {
        return near_plane;
    }

    /**
     * Gets cameras far plane distance
     *
     * @return Cameras far plane distance
     */
    public float getFarPlane() {
        return far_plane;
    }

    /**
     * Gets cameras current position in world space as 3d vector
     * 
     * @return Current position as 3d vector
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Gets cameras pitch (rotation around the x axis) in degrees
     * 
     * @return Cameras pitch in degrees
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Gets cameras yaw (rotation around the y axis) in degrees
     * 
     * @return Cameras yaw in degrees
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Gets cameras roll (rotation around the z axis) in degrees
     * 
     * @return Cameras roll in degrees
     */
    public float getRoll() {
        return roll;
    }

    /**
     * Getting cameras current view projection matrix
     * 
     * @return View proejction matrix of the camera
     */
    public Matrix4f getViewProjectionMatrix() {
        return viewProjectionMatrix;
    }

    /**
     * Getting cameras current view projection matrix, where the object every time
     * facing the camera
     * 
     * @param x X offset of the matrix
     * @param y Y offset of the matrix
     * @param z Z offset of the matrix
     * @return View projection matrix of the camera
     */
    public Matrix4f getFacingMVPMatrix(float x, float y, float z) {

        Matrix4f mvpMatrix = new Matrix4f(facingModelMatrix);
        mvpMatrix.setColumn(3, x, y, z, 1.0f);
        mvpMatrix.mul(viewProjectionMatrix, mvpMatrix);

        return mvpMatrix;
    }

    /**
     * Gets the current rotation matrix around the x axis
     * 
     * @return X axis rotation matrix
     */
    public Matrix4f getRotX() {
        return rotX;
    }

    /**
     * Gets the current rotation matrix around the y axis
     * 
     * @return Y axis rotation matrix
     */
    public Matrix4f getRotY() {
        return rotY;
    }

    /**
     * Gets the current rotation matrix around the z axis
     * 
     * @return Z axis rotation matrix
     */
    public Matrix4f getRotZ() {
        return rotZ;
    }

    /**
     * Getting cameras facing matrix, where the object every time facing the camera
     *
     * @return Facing matrix of the camera
     */
    public Matrix4f getFacingMatrix() {
        return facingModelMatrix;
    }

    /**
     * @return Current ray the camera is looking to
     */
    public CameraRay getRay() {
        return ray;
    }
}
