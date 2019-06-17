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
package de.coreengine.rendering.renderer.ppeffects;

import de.coreengine.rendering.model.Color;
import de.coreengine.util.Configuration;
import de.coreengine.rendering.programs.pp.FogPPShader;
import de.coreengine.rendering.renderer.MasterRenderer;

import java.util.List;

/**Fog effect for the post processer
 *
 * @author Darius Dinger
 */
public class FogEffect extends PostProcessingEffect{
    private static final float DEFAULT_DENSITY = 
            Configuration.getValuef("FOG_DEFAULT_DENSITY");
    private static final float DEFAULT_GRADIENT = 
            Configuration.getValuef("FOG_DEFAULT_GRADIENT");
    
    //Fogs density
    private float density = DEFAULT_DENSITY;
    
    //Fogs gradient
    private float gradient = DEFAULT_GRADIENT;
    
    //Fogs color
    private Color color = new Color();
    
    /**Creating new fog effect
     */
    public FogEffect() {
        super(new FogPPShader());
    }
    
    @Override
    protected void setUniforms() {
        ((FogPPShader) shader).setStrengthTexture(MasterRenderer.getGBUFFER().getVariable1Buffer());
        ((FogPPShader) shader).setValues(density, gradient, color);
    }
    
    /**Setting fogs gradient. Fog visibility will be calculated by this 
     * formula:<br>
     * f(x) = e^-(density * x)^gradient
     * 
     * @param density Fogs new density
     */
    public void setDensity(float density) {
        this.density = density;
    }
    
    /**Setting fogs density. Fog visibility will be calculated by this 
     * formula:<br>
     * f(x) = e^-(density * x)^gradient
     * 
     * @param gradient Fogs new gradient
     */
    public void setGradient(float gradient) {
        this.gradient = gradient;
    }
    
    /**@return Color of the fog
     */
    public Color getColor() {
        return color;
    }

    @Override
    public void addImpliedEffects(List<PostProcessingEffect> effects) {}
}
