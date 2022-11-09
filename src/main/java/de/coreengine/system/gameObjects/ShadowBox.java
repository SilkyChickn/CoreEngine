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

package de.coreengine.system.gameObjects;

import de.coreengine.asset.AssetDatabase;
import de.coreengine.framework.Window;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderable.gui.GUIPane;
import de.coreengine.rendering.renderable.light.ShadowLight;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class ShadowBox extends GameObject {
    private static final float OFFSET = 10;
    private static final Vector4f UP = new Vector4f(0, 1, 0, 0);
    private static final Vector4f FORWARD = new Vector4f(0, 0, -1, 0);
    private static final float SHADOW_DISTANCE = 10;

    private ShadowLight shadowLight = new ShadowLight();

    private Camera viewCamera;
    private Vector3f lightDirection = new Vector3f();

    private float minX, maxX;
    private float minY, maxY;
    private float minZ, maxZ;

    private float farHeight, farWidth, nearHeight, nearWidth;

    /**
     * Set camera to align shadow map to view frustuum
     *
     * @param viewCamera Camera to align to
     */
    public void setViewCamera(Camera viewCamera) {
        this.viewCamera = viewCamera;
        calculateWidthsAndHeights();
    }

    /**
     * Read/Writeable vector of the light/shadow direction (most likely inverted
     * lightposition)
     * 
     * @return Read/Writeable vector of the light/shadow direction
     */
    public Vector3f getLightDirection() {
        return lightDirection;
    }

    @Override
    public void onInit() {
        shadowLight.setQuality(10);
        super.onInit();
    }

    /**
     * Updates the bounds of the shadow box based on the light direction and the
     * camera's view frustum, to make sure that the box covers the smallest area
     * possible while still ensuring that everything inside the camera's view
     * (within a certain range) will cast shadows.
     */
    @Override
    public void onUpdate() {
        Matrix4f rotation = calculateCameraRotationMatrix();
        Vector4f forwardVector = new Vector4f();
        rotation.transform(FORWARD, forwardVector);

        Vector3f toFar = new Vector3f(forwardVector.x, forwardVector.y, forwardVector.z);
        toFar.scale(SHADOW_DISTANCE);
        Vector3f toNear = new Vector3f(forwardVector.x, forwardVector.y, forwardVector.z);
        toNear.scale(viewCamera.getNearPlane());
        Vector3f centerNear = new Vector3f();
        centerNear.add(toNear, viewCamera.getPosition());
        Vector3f centerFar = new Vector3f();
        centerFar.add(toFar, viewCamera.getPosition());

        Vector4f[] points = calculateFrustumVertices(rotation,
                new Vector3f(forwardVector.x, forwardVector.y, forwardVector.z), centerNear,
                centerFar);

        boolean first = true;
        for (Vector4f point : points) {
            if (first) {
                minX = point.x;
                maxX = point.x;
                minY = point.y;
                maxY = point.y;
                minZ = point.z;
                maxZ = point.z;
                first = false;
                continue;
            }
            if (point.x > maxX) {
                maxX = point.x;
            } else if (point.x < minX) {
                minX = point.x;
            }
            if (point.y > maxY) {
                maxY = point.y;
            } else if (point.y < minY) {
                minY = point.y;
            }
            if (point.z > maxZ) {
                maxZ = point.z;
            } else if (point.z < minZ) {
                minZ = point.z;
            }
        }
        maxZ += OFFSET;

        // Update shadowlight
        shadowLight.setDimension(getWidth(), getHeight(), getLength());
        updateLightViewMatrix(lightDirection, getCenter());

        super.onUpdate();
    }

    /**
     * Calculates the center of the "view cuboid" in light space first, and then
     * converts this to world space using the inverse light's view matrix.
     * 
     * @return The center of the "view cuboid" in world space.
     */
    protected Vector3f getCenter() {
        float x = (minX + maxX) / 2f;
        float y = (minY + maxY) / 2f;
        float z = (minZ + maxZ) / 2f;
        Vector4f cen = new Vector4f(x, y, z, 1);
        Matrix4f invertedLight = new Matrix4f(shadowLight.getLightsView().getViewMatrix());
        invertedLight.invert();
        invertedLight.transform(cen);
        return new Vector3f(cen.x, cen.y, cen.z);
    }

    /**
     * @return The width of the "view cuboid" (orthographic projection area).
     */
    protected float getWidth() {
        return maxX - minX;
    }

    /**
     * @return The height of the "view cuboid" (orthographic projection area).
     */
    protected float getHeight() {
        return maxY - minY;
    }

    /**
     * @return The length of the "view cuboid" (orthographic projection area).
     */
    protected float getLength() {
        return maxZ - minZ;
    }

    /**
     * Calculates the position of the vertex at each corner of the view frustum
     * in light space (8 vertices in total, so this returns 8 positions).
     * 
     * @param rotation      Camera's rotation.
     * @param forwardVector The direction that the camera is aiming, and thus the
     *                      direction of the frustum.
     * @param centerNear    The center point of the frustum's near plane.
     * @param centerFar     The center point of the frustum's (possibly adjusted)
     *                      far plane.
     * @return The positions of the vertices of the frustum in light space.
     */
    private Vector4f[] calculateFrustumVertices(Matrix4f rotation, Vector3f forwardVector,
            Vector3f centerNear, Vector3f centerFar) {

        Vector4f upVector = new Vector4f();
        rotation.transform(UP, upVector);
        Vector3f rightVector = new Vector3f();
        rightVector.cross(forwardVector, new Vector3f(upVector.x, upVector.y, upVector.z));
        Vector3f downVector = new Vector3f(-upVector.x, -upVector.y, -upVector.z);
        Vector3f leftVector = new Vector3f(-rightVector.x, -rightVector.y, -rightVector.z);

        Vector3f farTop = new Vector3f();
        farTop.add(centerFar, new Vector3f(upVector.x * farHeight,
                upVector.y * farHeight, upVector.z * farHeight));
        Vector3f farBottom = new Vector3f();
        farBottom.add(centerFar, new Vector3f(downVector.x * farHeight,
                downVector.y * farHeight, downVector.z * farHeight));
        Vector3f nearTop = new Vector3f();
        nearTop.add(centerNear, new Vector3f(upVector.x * nearHeight,
                upVector.y * nearHeight, upVector.z * nearHeight));
        Vector3f nearBottom = new Vector3f();
        nearBottom.add(centerNear, new Vector3f(downVector.x * nearHeight,
                downVector.y * nearHeight, downVector.z * nearHeight));

        Vector4f[] points = new Vector4f[8];
        points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth);
        points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth);
        points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth);
        points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth);
        points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth);
        points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth);
        points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth);
        points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth);
        return points;
    }

    /**
     * Calculates one of the corner vertices of the view frustum in world space
     * and converts it to light space.
     * 
     * @param startPoint The starting center point on the view frustum.
     * @param direction  The direction of the corner from the start point.
     * @param width      The distance of the corner from the start point.
     * @return The relevant corner vertex of the view frustum in light space.
     */
    private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction,
            float width) {
        Vector3f point = new Vector3f();
        point.add(startPoint,
                new Vector3f(direction.x * width, direction.y * width, direction.z * width));
        Vector4f point4f = new Vector4f(point.x, point.y, point.z, 1f);
        shadowLight.getLightsView().getViewMatrix().transform(point4f);
        return point4f;
    }

    /**
     * @return The rotation of the camera represented as a matrix.
     */
    private Matrix4f calculateCameraRotationMatrix() {
        Matrix4f rotation = new Matrix4f();
        rotation.mul(viewCamera.getRotX(), viewCamera.getRotY());
        return rotation;
    }

    /**
     * Calculates the width and height of the near and far planes of the
     * camera's view frustum. However, this doesn't have to use the "actual" far
     * plane of the view frustum. It can use a shortened view frustum if desired
     * by bringing the far-plane closer, which would increase shadow resolution
     * but means that distant objects wouldn't cast shadows.
     */
    private void calculateWidthsAndHeights() {
        farWidth = (float) (SHADOW_DISTANCE * Math.tan(Math.toRadians(viewCamera.getFov())));
        nearWidth = (float) (viewCamera.getNearPlane()
                * Math.tan(Math.toRadians(viewCamera.getFov())));
        farHeight = farWidth / getAspectRatio();
        nearHeight = nearWidth / getAspectRatio();
    }

    /**
     * @return The aspect ratio of the display (width:height ratio).
     */
    private float getAspectRatio() {
        return (float) Window.getWidth() / (float) Window.getHeight();
    }

    /**
     * Updates the "view" matrix of the light. This creates a view matrix which
     * will line up the direction of the "view cuboid" with the direction of the
     * light. The light itself has no position, so the "view" matrix is centered
     * at the center of the "view cuboid". The created view matrix determines
     * where and how the "view cuboid" is positioned in the world. The size of
     * the view cuboid, however, is determined by the projection matrix.
     * 
     * @param direction The light direction, and therefore the direction that the
     *                  "view cuboid" should be pointing.
     * @param center    The center of the "view cuboid" in world space.
     */
    private void updateLightViewMatrix(Vector3f direction, Vector3f center) {

        direction.normalize();
        center.negate();

        float pitch = (float) Math.toDegrees(Math.acos(new Vector2f(direction.x, direction.z).length()));
        float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
        yaw = direction.z > 0 ? yaw - 180 : yaw;

        shadowLight.getLightsView().setPitch(pitch);
        shadowLight.getLightsView().setYaw(-yaw);
        shadowLight.getLightsView().setX(center.x);
        shadowLight.getLightsView().setY(center.y);
        shadowLight.getLightsView().setZ(center.z);
        shadowLight.updateVpMat();
    }

    private GUIPane testPane = new GUIPane(null);

    @Override
    public void onRender() {
        testPane.setPosX(0.5f);
        testPane.setPosY(0.5f);
        testPane.setScaleX(0.25f);
        testPane.setScaleY(0.25f);
        AssetDatabase.addTexture("SHADOW_MAP", shadowLight.getShadowMap().getColorAttachment0());
        testPane.setTexture("SHADOW_MAP");
        MasterRenderer.renderGui2D(testPane);
        MasterRenderer.setShadowLight(shadowLight);
        super.onRender();
    }
}
