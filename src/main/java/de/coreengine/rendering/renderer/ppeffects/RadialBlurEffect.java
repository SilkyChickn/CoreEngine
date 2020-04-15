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

import de.coreengine.framework.Window;
import de.coreengine.rendering.programs.pp.RadialBlurPPShader;
import de.coreengine.util.Configuration;

import javax.vecmath.Vector2f;
import java.util.List;

/**
 * Effect that can apply an radial blur to the post processing process
 *
 * @author Darius Dinger
 */
public class RadialBlurEffect extends PostProcessingEffect {
    private static final float DEFAULT_INTENSITY = Configuration.getValuef("RADIAL_BLUR_DEFAULT_INTENSITY");
    private static final float DEFAULT_BRIGHTNESS = Configuration.getValuef("RADIAL_BLUR_DEFAULT_BRIGHTNESS");
    private static final int DEFAULT_QUALITY = Configuration.getValuei("RADIAL_BLUR_DEFAULT_QUALITY");

    // Intensity of the blur
    private float intensity = DEFAULT_INTENSITY;

    // Brightness changing of the blur
    private float bightness = DEFAULT_BRIGHTNESS;

    // Origin of the blur
    private Vector2f origin = new Vector2f(0.5f, 0.5f);

    // Quality of the blur
    private int quality = DEFAULT_QUALITY;

    /**
     * Creating new radial blur effect and setting shader
     */
    public RadialBlurEffect() {
        super(new RadialBlurPPShader());

        reloadTexelSize();
        Window.addWindowListener((x, y, aspect) -> reloadTexelSize());
    }

    /**
     * (Re)loading size of a texel into the shader
     */
    private void reloadTexelSize() {
        shader.start();
        ((RadialBlurPPShader) shader).setSize(new Vector2f(1.0f / Window.getWidth(), 1.0f / Window.getHeight()));
        shader.stop();
    }

    @Override
    protected void setUniforms() {
        ((RadialBlurPPShader) shader).prepareBlur(intensity, bightness, origin, quality);
    }

    /**
     * @return Read/writeable origin of the blur
     */
    public Vector2f getOrigin() {
        return origin;
    }

    /**
     * @param bightness New brightness of the blur
     */
    public void setBightness(float bightness) {
        this.bightness = bightness;
    }

    /**
     * @param intensity New intensity of the blur
     */
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    /**
     * @param quality New quality of the blur (iterations)
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    @Override
    public void addImpliedEffects(List<PostProcessingEffect> effects) {
    }
}
