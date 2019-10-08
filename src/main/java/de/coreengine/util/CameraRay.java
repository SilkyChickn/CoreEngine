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

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**Class that can calcthe ray, the camera is looking to
 *
 * @author Darius Dinger
 */
public class CameraRay {
    
    private Vector3f ray = new Vector3f();
    
    private Vector2f offset = new Vector2f();
    
    /**Recalculate the current camera ray
     * 
     * @param inverseVMat Inverse view matrix of the camera
     * @param inversePMat Inverse projection matrix of the camera
     */
    public void recalcRay(Matrix4f inverseVMat, Matrix4f inversePMat){
        Vector4f coords = new Vector4f(offset.x, offset.y, -1.0f, 1.0f);
        
        inversePMat.transform(coords);
        coords.z = (-1.0f);
        coords.w = (0.0f);
        
        inverseVMat.transform(coords);
        ray.set(coords.x, coords.y, coords.z);
        ray.normalize();
    }
    
    /**@return Current ray, the camera is looking to
     */
    public Vector3f getRay() {
        return ray;
    }
}
