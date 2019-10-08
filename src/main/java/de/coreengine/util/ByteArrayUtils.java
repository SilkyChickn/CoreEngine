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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**Utilities with byte arrays
 *
 * @author Darius Dinger
 */
public class ByteArrayUtils {

    /**Combining multiple byte arrays
     *
     * @param in Byte arrays to combine
     * @return Combined byte array
     */
    public static byte[] combine(byte[]... in){
        int newSize = 0, counter = 0;
        for(byte[] b: in) newSize += b.length;
        byte[] out = new byte[newSize];
        for(byte[] ba: in) for(byte b: ba) out[counter++] = b;
        return out;
    }
    
    /**Converting a float array into a byte array
     *
     * @param in Float array to convert
     * @return Converted byte array
     */
    public static byte[] toBytes(float[] in){
        byte[] out = new byte[in.length * 4];
        ByteBuffer byteBuffer = ByteBuffer.wrap(out);
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(in);
        return out;
    }

    /**Converting a byte array into a float array
     *
     * @param in Byte array to convert
     * @return Converted float array
     */
    public static float[] fromBytesf(byte[] in){
        float[] out = new float[in.length / 4];
        ByteBuffer byteBuffer = ByteBuffer.wrap(in);
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.get(out);
        return out;
    }

    /**Converting an int array into a byte array
     *
     * @param in Int array to convert
     * @return Converted byte array
     */
    public static byte[] toBytes(int[] in){
        byte[] out = new byte[in.length * 4];
        ByteBuffer byteBuffer = ByteBuffer.wrap(out);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(in);
        return out;
    }

    /**Converting a byte array into a int array
     *
     * @param in Byte array to convert
     * @return Converted int array
     */
    public static int[] fromBytesi(byte[] in){
        int[] out = new int[in.length / 4];
        ByteBuffer byteBuffer = ByteBuffer.wrap(in);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.get(out);
        return out;
    }

    /**Converting a short array into a byte array
     *
     * @param in Short array to convert
     * @return Converted byte array
     */
    public static byte[] toBytes(short[] in){
        byte[] out = new byte[in.length * 2];
        ByteBuffer byteBuffer = ByteBuffer.wrap(out);
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.put(in);
        return out;
    }

    /**Converting a byte array into a short array
     *
     * @param in Byte array to convert
     * @return Converted short array
     */
    public static short[] fromBytess(byte[] in){
        short[] out = new short[in.length / 2];
        ByteBuffer byteBuffer = ByteBuffer.wrap(in);
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.get(out);
        return out;
    }
}
