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
package de.coreengine.rendering.renderer.ppeffects;

import de.coreengine.framework.Window;
import de.coreengine.framework.WindowChangedListener;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.rendering.programs.pp.LightScatteringPPShader;
import de.coreengine.util.Configuration;
import java.util.List;
import javax.vecmath.Vector2f;

/**Light scattering effect for the post processer
 *
 * @author Darius Dinger
 */
public class LightScatteringEffect extends PostProcessingEffect{
    private static final float DEFAULT_BRIGHTNESS = 
            Configuration.getValuef("LIGHT_SCATTERING_DEFAULT_BRIGHTNESS");
    private static final float DEFAULT_INTENSITY = 
            Configuration.getValuef("LIGHT_SCATTERING_DEFAULT_INTENSITY");
    private static final int DEFAULT_QUALITY = 
            Configuration.getValuei("LIGHT_SCATTERING_DEFAULT_QUALITY");
    
    //Scattering configuration
    private float brightness = DEFAULT_BRIGHTNESS, intensity = DEFAULT_INTENSITY;
    private int quality = DEFAULT_QUALITY;
    
    /**Create new light scattering effect
     */
    public LightScatteringEffect() {
        super(new LightScatteringPPShader());
        
        reloadTexelSize();
        Window.addWindowListener(new WindowChangedListener() {
            @Override
            public void resolutionChanged(int x, int y, float aspect) {
                reloadTexelSize();
            }
        });
    }
    
    /**(Re)loading size of a texel into the shader
     */
    private void reloadTexelSize(){
        shader.start();
        ((LightScatteringPPShader) shader).setSize(new Vector2f(
                1.0f / Window.getWidth(), 1.0f / Window.getHeight()
        ));
        shader.stop();
    }
    
    @Override
    protected void setUniforms() {
        ((LightScatteringPPShader) shader).setSunTexture(
                MasterRenderer.getGBUFFER().getSunBuffer());
        ((LightScatteringPPShader) shader).reloadSun();
        ((LightScatteringPPShader) shader).prepareEffect
            (intensity, brightness, quality);
    }
    
    /**@param brightness New brightness of the light scatters
     */
    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }
    
    /**@param intensity New intensity of the light scatters (radius)
     */
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    
    /**@param quality New quality of the light scatters (iterations)
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }
    
    @Override
    public void addImpliedEffects(List<PostProcessingEffect> effects) {}
}
