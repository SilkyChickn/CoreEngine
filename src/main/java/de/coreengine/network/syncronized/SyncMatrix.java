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
package de.coreengine.network.syncronized;

import de.coreengine.network.Syncronized;
import de.coreengine.util.Toolbox;
import javax.vecmath.Matrix4f;

/**Float that can be syncronized in a network
 *
 * @author Darius Dinger
 */
public class SyncMatrix extends Syncronized{
    
    private Matrix4f matrix = new Matrix4f();
    private float[] data = new float[16];
    
    /**@param tag Tag of the syncronized matrix
     */
    public SyncMatrix(String tag) {
        super(tag);
    }
    
    /**@param mat Matrix to get values from
     */
    public void set(Matrix4f mat){
        if(!mat.equals(matrix)){
            change();
            return;
        }
        
        matrix.set(mat);
        data[0] = matrix.m00; data[1] = matrix.m01; data[2] = matrix.m02; data[3] = matrix.m03;
        data[4] = matrix.m10; data[5] = matrix.m11; data[6] = matrix.m12; data[7] = matrix.m13;
        data[8] = matrix.m20; data[9] = matrix.m21; data[10] = matrix.m22; data[11] = matrix.m23;
        data[12] = matrix.m30; data[13] = matrix.m31; data[14] = matrix.m32; data[15] = matrix.m33;
    }
    
    /**@param mat Matrix to store values in
     */
    public void get(Matrix4f mat){
        mat.set(matrix);
    }
    
    @Override
    protected void sync(String sync) {
        matrix.set(Toolbox.stringToArray(sync, "-", 16));
    }
    
    @Override
    protected String sync() {
        return Toolbox.arrayToString(data, "-");
    }
}
