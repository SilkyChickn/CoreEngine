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

import de.coreengine.rendering.programs.pp.DofPPShader;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.util.Configuration;

import java.util.List;

/**
 * Dof effect for the post processer
 *
 * @author Darius Dinger
 */
public class DofEffect extends PostProcessingEffect {
    private static final float DEFAULT_DIRECTIONS = Configuration.getValuef("DOF_DEFAULT_BLUR_DIRECTIONS");
    private static final float DEFAULT_QUALITY = Configuration.getValuef("DOF_DEFAULT_BLUR_QUALITY");
    private static final float DEFAULT_SIZE = Configuration.getValuef("DOF_DEFAULT_BLUR_SIZE");
    private static final float DEFAULT_DENSITY = Configuration.getValuef("DOF_DEFAULT_DENSITY");
    private static final float DEFAULT_GRADIENT = Configuration.getValuef("DOF_DEFAULT_GRADIENT");

    // Fogs density
    private float density = DEFAULT_DENSITY;

    // Fogs gradient
    private float gradient = DEFAULT_GRADIENT;

    // Blur directions
    private float directions = DEFAULT_DIRECTIONS;

    // Quality of the blur
    private float quality = DEFAULT_QUALITY;

    // Blur size / radius
    private float size = DEFAULT_SIZE;

    /**
     * Creating new dof effect
     */
    public DofEffect() {
        super(new DofPPShader());
    }

    @Override
    protected void setUniforms() {
        ((DofPPShader) shader).prepareBlur(directions, quality, size);
        ((DofPPShader) shader).setStrengthTexture(MasterRenderer.getGBUFFER().getVariable1Buffer());
        ((DofPPShader) shader).setArea(density, gradient);
    }

    /**
     * Setting the directions to blur.
     * 
     * @param directions New blur directions
     */
    public void setDirections(float directions) {
        this.directions = directions;
    }

    /**
     * Setting the blur quality.
     * 
     * @param quality New blur quality
     */
    public void setQuality(float quality) {
        this.quality = quality;
    }

    /**
     * Setting the blur size. Often called blur radius.
     * 
     * @param size New blur size / radius
     */
    public void setSize(float size) {
        this.size = size;
    }

    /**
     * Getting the directions to blur.
     * 
     * @return Current blur direction
     */
    public float getDirections() {
        return directions;
    }

    /**
     * Getting the blur quality.
     * 
     * @return Current blur quality
     */
    public float getQuality() {
        return quality;
    }

    /**
     * Getting the blur size. Often called blur radius.
     * 
     * @return Current blur size / radius
     */
    public float getSize() {
        return size;
    }

    /**
     * Setting fogs density. Fog visibility will be calculated by this formula:<br>
     * f(x) = e^-(density * x)^gradient
     * 
     * @param density Fogs new density
     */
    public void setDensity(float density) {
        this.density = density;
    }

    /**
     * Setting fogs gradient. Fog visibility will be calculated by this formula:<br>
     * f(x) = e^-(density * x)^gradient
     * 
     * @param gradient Fogs new gradient
     */
    public void setGradient(float gradient) {
        this.gradient = gradient;
    }

    /**
     * Getting fogs density. Fog visibility will be calculated by this formula:<br>
     * f(x) = e^-(density * x)^gradient
     * 
     * @return Fogs density
     */
    public float getDensity() {
        return density;
    }

    /**
     * Getting fogs gradient. Fog visibility will be calculated by this formula:<br>
     * f(x) = e^-(density * x)^gradient
     * 
     * @return Fogs gradient
     */
    public float getGradient() {
        return gradient;
    }

    @Override
    public void addImpliedEffects(List<PostProcessingEffect> effects) {
    }
}
