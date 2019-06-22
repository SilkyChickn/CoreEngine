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

import de.coreengine.rendering.model.Color;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.util.Arrays;
import java.util.List;

/**Class that contains some useful functions
 *
 * @author Darius Dinger
 */
public class Toolbox {
    
    //Buffer for faster matrix calculation
    private static final float[] MATRIX4BUFFER = new float[16];
    
    /**Adding an element to an array
     * 
     * @param <T> Type of the array data
     * @param array Array where the element should be added
     * @param element Element to add into array
     * @return New array with element added
     */
    public static <T> T[] addElement(T[] array, T element){
        array = Arrays.copyOf(array, array.length +1);
        array[array.length - 1] = element;
        return array;
    }
    
    /**Adding an int element to an int array
     * 
     * @param array Array where the element should be added
     * @param element Element to add into array
     * @return New array with element added
     */
    public static int[] addElement(int[] array, int element){
        array = Arrays.copyOf(array, array.length +1);
        array[array.length -1] = element;
        return array;
    }
    
    /**Adding an float element to an float array
     * 
     * @param array Array where the element should be added
     * @param element Element to add into array
     * @return New array with element added
     */
    public static float[] addElement(float[] array, float element){
        array = Arrays.copyOf(array, array.length +1);
        array[array.length -1] = element;
        return array;
    }
    
    /**Transforming a matrix4f from vecmtath to a 16 valued float array.<br>
     * Maybe not the most performant procedure...
     * 
     * @param mat Matrix to transform
     * @return Transformed float array
     */
    public static float[] matrixToFloatArray(Matrix4f mat){
        int ctr = 0;
        for(int r = 0; r < 4; r++){
            for(int c = 0; c < 4; c++){
                MATRIX4BUFFER[ctr++] = mat.getElement(c, r);
            }
        }
        
        return MATRIX4BUFFER;
    }
    
    /**@return Random color
     */
    public static Color generateRandomColor(){
        Color result = new Color();
        
        result.setRed   ((float) Math.random());
        result.setGreen ((float) Math.random());
        result.setBlue  ((float) Math.random());
        
        return result;
    }
    
    /**Converting a list of floats into a float array. Because java hates that...
     * 
     * @param floats Float list to convert
     * @return Converted float array
     */
    public static float[] toArrayf(List<Float> floats){
        float[] result = new float[floats.size()];
        
        int i = 0;
        for(Float f: floats) result[i++] = f;
        
        return result;
    }
    
    /**Converting a list of integers into a int array.
     * 
     * @param ints Integer list to convert
     * @return Converted int array
     */
    public static int[] toArrayi(List<Integer> ints){
        int[] result = new int[ints.size()];
        
        int i = 0;
        for(Integer in: ints) result[i++] = in;
        
        return result;
    }
    
    /**Convert a float array into a string
     * 
     * @param arr Array to convert
     * @param seperator Sperator of the string
     * @return Generated string
     */
    public static String arrayToString(float[] arr, String seperator){
        if(arr == null || arr.length == 0) return "";
        StringBuilder result = new StringBuilder(Float.toString(arr[0]));
        for(int i = 1; i < arr.length; i++){
            result.append(seperator).append(arr[i]);
        }
        return result.toString();
    }
    
    /**Convert a string into a float array
     * 
     * @param str String to convert
     * @param seperator Seperator of the string
     * @param len Count of floats in the string
     * @return Generated float array
     */
    public static float[] stringToArray(String str, String seperator, int len){
        String[] data = str.split(seperator);
        float[] result = new float[data.length];
        for(int i = 0; i < len; i++) result[i] = Float.parseFloat(data[i]);
        return result;
    }

    /**Generating a random float between min and max
     *
     * @param min Min value of the float
     * @param max Max value of the float
     * @return Generated value
     */
    public static float randomFloat(float min, float max){
        return min +((float) Math.random() * (max -min));
    }

    /**Generating a random integer between min and max
     *
     * @param min Min value of the integer
     * @param max Max value of the integer
     * @return Generated value
     */
    public static int randomInt(int min, int max){
        return min +(int)((float) Math.random() * (max -min));
    }

    /**Generating random vector between min and max
     *
     * @param min Min vector values
     * @param max Max vector values
     * @return Generated vector
     */
    public static Vector3f randomVector(Vector3f min, Vector3f max){
        return new Vector3f(randomFloat(min.x, max.x), randomFloat(min.y, max.y), randomFloat(min.z, max.z));
    }

    /**Generating random vector between min and max
     *
     * @param min Min vector values
     * @param max Max vector values
     * @return Generated vector
     */
    public static Vector2f randomVector(Vector2f min, Vector2f max){
        return new Vector2f(randomFloat(min.x, max.x), randomFloat(min.y, max.y));
    }

    /**Getting the smallest float of values
     * 
     * @param values Floats to gets smallest from
     * @return Smallest float of values
     */
    public static float min(float ... values){
        if(values.length < 1) return 0;
        float min = values[0];
        for(float f: values){
            if(f < min) min = f;
        }
        return min;
    }

    /**Getting the greatest float of values
     *
     * @param values Floats to gets greatest from
     * @return Greatest float of values
     */
    public static float max(float ... values){
        if(values.length < 1) return 0;
        float max = values[0];
        for(float f: values){
            if(f > max) max = f;
        }
        return max;
    }
}

