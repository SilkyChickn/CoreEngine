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
package de.coreengine.rendering.renderer;

import de.coreengine.rendering.model.Mesh;
import de.coreengine.rendering.model.singletons.Cube3D;
import de.coreengine.rendering.programs.SkyboxShader;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderable.Skybox;
import de.coreengine.util.Configuration;
import org.lwjgl.opengl.GL11;

/**Renderer that can render a skybox
 *
 * @author Darius Dinger
 */
public class SkyboxRenderer {
    private static final float SKYBOX_SIZE = 
            Configuration.getValuef("SKYBOX_SIZE");
    
    private SkyboxShader shader = new SkyboxShader();

    public SkyboxRenderer() {
        shader.start();
        shader.setSkyboxSize(SKYBOX_SIZE);
        shader.stop();
    }
    
    /**Rendering skybox onto the bound framebuffer
     * 
     * @param skybox Skybox to render
     * @param cam Camera to render skyboc from
     */
    void render(Skybox skybox, Camera cam){
        GL11.glDisable(GL11.GL_CULL_FACE);
        
        Mesh model = Cube3D.getInstance();
        
        shader.start();
        
        model.getVao().bind();
        model.getVao().enableAttributes();
        model.getIndexBuffer().bind();
        
        shader.setCamera(cam);
        shader.prepareSkybox(skybox);
        
        GL11.glDrawElements(GL11.GL_TRIANGLES, 
                model.getIndexBuffer().getSize(), GL11.GL_UNSIGNED_INT, 0);
        
        model.getIndexBuffer().unbind();
        model.getVao().disableAttributes();
        model.getVao().unbind();
        
        shader.stop();
        
        GL11.glEnable(GL11.GL_CULL_FACE);
    }
    
}
