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

import java.io.Serializable;

/**Represent a color in syncron rgb, hsb and srgb color models
 *
 * @author Darius Dinger
 */
public class Color implements Serializable{
    
    //Represent the red, blue and green value of the rgb color model
    private final float[] rgb = new float[3];
    
    /**Creates a new white color
     */
    public Color() {
        rgb[0] = 1.0f;
        rgb[1] = 1.0f;
        rgb[2] = 1.0f;
    }
    
    /**Creating new color with init rgb values
     * 
     * @param r Red value of the color
     * @param g Green value of the color
     * @param b Blue value of the color
     */
    public Color(float r, float g, float b){
        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;
    }
    
    /**Setting new rgb value of this color
     * 
     * @param r New red value
     * @param g New green value
     * @param b New blue value
     */
    public void set(float r, float g, float b){
        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;
    }
    
    /**Sets the value of this color to the value of c
     * 
     * @param c Color to get value from
     */
    public void set(Color c){
        rgb[0] = c.rgb[0];
        rgb[1] = c.rgb[1];
        rgb[2] = c.rgb[2];
    }
    
    /**Sets the Red value of the rgb model and reclaculate the hsb and srgb
     * 
     * @param val New red value
     */
    public void setRed(float val){
        rgb[0] = val;
    }
    
    /**Sets the Green value of the rgb model and reclaculate the hsb and srgb
     * 
     * @param val New green value
     */
    public void setGreen(float val){
        rgb[1] = val;
    }
    
    /**Sets the Blue value of the rgb model and reclaculate the hsb and srgb
     * 
     * @param val New blue value
     */
    public void setBlue(float val){
        rgb[2] = val;
    }
    
    /**@return The red value of the rgb model
     */
    public float getRed(){
        return rgb[0];
    }
    
    /**@return The green value of the rgb model
     */
    public float getGreen(){
        return rgb[1];
    }
    
    /**@return The blue value of the rgb model
     */
    public float getBlue(){
        return rgb[2];
    }
    
    @Override
    public String toString() {
        return "RGB[" + rgb[0] + ";" + rgb[1] + ";" + rgb[2] + "] ";
    }
    
    /**Comparing the rgb values of this color and the the parameter color
     * and returning result
     * 
     * @param col Other color to compare with
     * @return Is RGB value identical
     */
    public boolean compare(Color col){
        return rgb[0] == col.rgb[0] && rgb[1] == col.rgb[1] && rgb[2] == col.rgb[2];
    }
}
