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
package io.github.suuirad.coreengine.rendering.renderer.ppeffects;

import io.github.suuirad.coreengine.rendering.programs.pp.HsbPPShader;

import java.util.List;

/**Hue, saturation and value post processing effect
 *
 * @author Darius Dinger
 */
public class HsbEffect extends PostProcessingEffect{
    
    //Hue saturation and brightness values for this effect
    private float hue = 1.0f, saturation = 1.0f, brightness = 1.0f;
    
    /**Creating new Hue saturation and brightness effect
     */
    public HsbEffect() {
        super(new HsbPPShader());
    }
    
    /**Setting the hue factor of this effect. The output color will be calculated
     * by<br> NewHue = OldHue * hue
     * 
     * @param hue New hue value
     */
    public void setHue(float hue) {
        this.hue = hue;
    }
    
    /**Setting the saturation factor of this effect. The output color will be calculated
     * by<br> NewSaturation = OldSaturation * saturation
     * 
     * @param saturation New saturation value
     */
    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }
    
    /**Setting the brightness factor of this effect. The output color will be calculated
     * by<br> NewBrightness = OldBrightness * brightness
     * 
     * @param brightness New brightness value
     */
    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }
    
    /**@return Hue value of the effect
     */
    public float getHue() {
        return hue;
    }
    
    /**@return Saturation value of the effect
     */
    public float getSaturation() {
        return saturation;
    }
    
    /**@return Brightness value of the effect
     */
    public float getBrightness() {
        return brightness;
    }
    
    @Override
    protected void setUniforms() {
        ((HsbPPShader) shader).setHsb(hue, saturation, brightness);
    }
    
    @Override
    public void addImpliedEffects(List<PostProcessingEffect> effects) {}
}
