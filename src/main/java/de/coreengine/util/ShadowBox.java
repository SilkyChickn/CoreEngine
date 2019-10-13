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

package de.coreengine.util;

import de.coreengine.rendering.renderable.Camera;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class ShadowBox {

    //Shadow map view projection matrix
    private Matrix4f vpMat = new Matrix4f();

    /**Aligning shadow box to camera view frustum.
     *
     * @param cam Camera to align to
     * @param light Camera of the lights view
     */
    public void alignTo(Camera cam, Camera light){
        /*
        Matrix4f inverseCamLightMat = getInverseCamLightMat(cam, light);
        Vector4f[] corners = transformNDCCube(inverseCamLightMat);
        recalcVPMatrix(corners);
        vpMat.mul(light.getViewMatrix());
         */
    }

    /**Transform the 8 corners of the opengl ndc cube with the given matrix.
     *
     * @param mat Matrix to transform corners with
     * @return Transformed corners
     */
    private Vector4f[] transformNDCCube(Matrix4f mat){
        Vector4f[] corners = new Vector4f[8];
        
        //Corner BottomLeftBack
        corners[0] = new Vector4f(-1, -1, 1, 1);
        mat.transform(corners[0]);

        //Corner BRB
        corners[1] = new Vector4f(1, -1, 1, 1);
        mat.transform(corners[1]);

        //Corner TLB
        corners[2] = new Vector4f(-1, 1, 1, 1);
        mat.transform(corners[2]);

        //Corner TRB
        corners[3] = new Vector4f(1, 1, 1, 1);
        mat.transform(corners[3]);

        //Corner BLF
        corners[4] = new Vector4f(-1, -1, -1, 1);
        mat.transform(corners[4]);

        //Corner BRF
        corners[5] = new Vector4f(1, -1, -1, 1);
        mat.transform(corners[5]);

        //Corner TLF
        corners[6] = new Vector4f(-1, 1, -1, 1);
        mat.transform(corners[6]);

        //Corner TRF
        corners[7] = new Vector4f(1, 1, -1, 1);
        mat.transform(corners[7]);

        return corners;
    }

    /**Getting the product of the inverse camera view projection matrix and the lights direction world matrix.
     * Can be used to transform the ndc cube into shadow map space to get the 8 corners of the cameras view  frustum
     * in shadow map space.
     *
     * @param cam Camera to get VP matrix from
     * @param light Camera of the lights view
     * @return Product of the inverse camera view projection matrix and the lights direction world matrix
     */
    private Matrix4f getInverseCamLightMat(Camera cam, Camera light){

        //Get inverse camera vp mat
        Matrix4f inverseCamVPMat = new Matrix4f();
        inverseCamVPMat.invert(cam.getViewProjectionMatrix());

        //Get light inverse world matrix
        light.updateViewMatrix();
        Matrix4f inverseLightWMat = new Matrix4f(light.getViewMatrix());
        inverseLightWMat.invert();

        //Combine inverse camera and light matrix
        Matrix4f inverseCamLightMat = new Matrix4f();
        inverseCamLightMat.mul(inverseLightWMat, inverseCamVPMat);

        return inverseCamLightMat;
    }

    /**Calculate ABB bounding box of the 8 shadow map space corners. Then creating the orthographic projection matrix
     * from the AABB box and store it into the vpMat variable.
     *
     * @param corners 8 shadow map space corners
     */
    private void recalcVPMatrix(Vector4f[] corners){

        //Get minimum corners of axis aligned bounding box
        float aabbMinX = Toolbox.min(corners[0].x, corners[1].x, corners[2].x, corners[3].x, 
                corners[4].x, corners[5].x, corners[6].x, corners[7].x);
        float aabbMinY = Toolbox.min(corners[0].y, corners[1].y, corners[2].y, corners[3].y,
                corners[4].y, corners[5].y, corners[6].y, corners[7].y);
        float aabbMinZ = Toolbox.min(corners[0].z, corners[1].z, corners[2].z, corners[3].z,
                corners[4].z, corners[5].z, corners[6].z, corners[7].z);

        //Get maximum corners of axis aligned bounding box
        float aabbMaxX = Toolbox.max(corners[0].x, corners[1].x, corners[2].x, corners[3].x,
                corners[4].x, corners[5].x, corners[6].x, corners[7].x);
        float aabbMaxY = Toolbox.max(corners[0].y, corners[1].y, corners[2].y, corners[3].y,
                corners[4].y, corners[5].y, corners[6].y, corners[7].y);
        float aabbMaxZ = Toolbox.max(corners[0].z, corners[1].z, corners[2].z, corners[3].z,
                corners[4].z, corners[5].z, corners[6].z, corners[7].z);

        float width = aabbMaxX -aabbMinX;
        float height = aabbMaxY -aabbMinY;

        System.out.println("\nData:");
        System.out.println(width);
        System.out.println(height);
        System.out.println(aabbMinZ);
        System.out.println(aabbMaxZ);

        vpMat.setIdentity();
        vpMat.m00 = 1.0f / width;
        vpMat.m11 = 1.0f / height;
        vpMat.m22 = -2.0f / (aabbMaxZ -aabbMinZ);
        vpMat.m23 = -(aabbMaxZ +aabbMinZ) / (aabbMaxZ -aabbMinZ);
    }

    /**Getting shadow map view projection matrix (toShadowMapSpaceMatrix).
     * This can be used to transform vertices into shadow map space.
     *
     * @return Shadow map view projection matrix
     */
    public Matrix4f getVpMat() {
        return vpMat;
    }
}
