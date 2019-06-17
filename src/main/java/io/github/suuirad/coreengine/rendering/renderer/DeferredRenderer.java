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
package io.github.suuirad.coreengine.rendering.renderer;

import io.github.suuirad.coreengine.rendering.GBuffer;
import io.github.suuirad.coreengine.rendering.model.SimpleModel;
import io.github.suuirad.coreengine.rendering.model.singletons.Quad2D;
import io.github.suuirad.coreengine.rendering.programs.DeferredShader;
import io.github.suuirad.coreengine.rendering.renderable.Camera;
import io.github.suuirad.coreengine.rendering.renderable.light.AmbientLight;
import io.github.suuirad.coreengine.rendering.renderable.light.DirectionalLight;
import io.github.suuirad.coreengine.rendering.renderable.light.PointLight;
import io.github.suuirad.coreengine.rendering.renderable.light.SpotLight;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**Representing a renderer that rendering deferred lights into a gbuffer
 *
 * @author Darius Dinger
 */
public class DeferredRenderer {
    
    private DeferredShader shader = new DeferredShader();
    
    /**Rendering lights into a gbuffer using deferred rendering
     * 
     * @param gBuffer GBuffer to get rendered texture from
     * @param pointLights Point lights to render
     * @param spotLights Spot lights to render
     * @param ambientLights Ambient lights to render
     * @param directionalLights Directional lights to render
     * @param cam Camera to use for lighting calculation
     */
    void render(GBuffer gBuffer, List<PointLight> pointLights,
                List<SpotLight> spotLights, List<AmbientLight> ambientLights,
                List<DirectionalLight> directionalLights, Camera cam) {
        
        SimpleModel quad = Quad2D.getInstance();
        
        shader.start();
        quad.getVao().bind();
        quad.getVao().enableAttributes();
        quad.getIndexBuffer().bind();
        
        shader.setGBuffer(gBuffer);
        shader.setCameraPosition(cam.getPosition());
        shader.setLightSources(pointLights, spotLights, ambientLights, directionalLights);
        
        GL11.glDrawElements(GL11.GL_TRIANGLES, quad.getIndexBuffer().getSize(), 
                GL11.GL_UNSIGNED_INT, 0);
        
        quad.getIndexBuffer().unbind();
        quad.getVao().disableAttributes();
        quad.getVao().unbind();
        shader.stop();
    }
}
