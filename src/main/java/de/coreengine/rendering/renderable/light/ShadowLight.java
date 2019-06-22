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
package de.coreengine.rendering.renderable.light;

import de.coreengine.framework.Window;
import de.coreengine.rendering.FrameBufferObject;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.util.Configuration;
import de.coreengine.util.ShadowBox;

import javax.vecmath.Matrix4f;

/**Represents a shadow light in the scene.
 * This light does not light objects up, just creating a show behind the objects.
 *
 * @author Darius Dinger
 */
public class ShadowLight extends DirectionalLight{
    private static final float DEFAULT_QUALITY =
            Configuration.getValuef("SHADOW_DEFAULT_QUALITY");

    //Fbo that stores the actual shadow map from lights position
    private FrameBufferObject shadowMap;

    //Box to render shadows in
    private ShadowBox shadowBox = new ShadowBox();

    //Quality of the shadows
    private float quality = DEFAULT_QUALITY;

    /**Creating new shadow light.
     * Create shadow map fbo and recreating every window resize
     */
    public ShadowLight(){
        recreateFbo();
        Window.addWindowListener((x, y, aspect) -> recreateFbo());
    }

    /**(Re)creating shadow map fbo
     */
    private void recreateFbo(){
        shadowMap = new FrameBufferObject((int)(Window.getWidth() * quality),
                (int)(Window.getHeight() * quality), false);
    }

    /**Setting view projection matrix frustum to bounding box of perspective view matrix frustum.
     *
     * @param cam Camera to align vpMat for
     */
    public void alignTo(Camera cam){
        shadowBox.alignTo(cam, getDirection());
    }

    /**@return View projection matrix of the shadow light
     */
    public Matrix4f getVpMat() {
        return shadowBox.getVpMat();
    }

    /**@return Fbo that stores the actual shadow map from lights position
     */
    public FrameBufferObject getShadowMap() {
        return shadowMap;
    }
}
