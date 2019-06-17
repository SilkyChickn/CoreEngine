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

import de.coreengine.rendering.model.SimpleModel;
import de.coreengine.rendering.model.singletons.Quad2D;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderable.Water;
import de.coreengine.rendering.programs.WaterShader;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**Class that can render water
 *
 * @author Darius Dinger
 */
public class WaterRenderer {
    
    private WaterShader shader = new WaterShader();
    
    /**Rendering water using a water shader
     * 
     * @param waters Waters to render
     * @param camera Camera to render from
     */
    void render(List<Water> waters, Camera camera){
        
        SimpleModel model = Quad2D.getInstance();
        
        shader.start();
        shader.setCamera(camera);
        
        model.getVao().bind();
        model.getVao().enableAttributes();
        model.getIndexBuffer().bind();
        
        waters.forEach(water -> {
            shader.prepareWater(water);
            
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.getIndexBuffer().getSize(),
                    GL11.GL_UNSIGNED_INT, 0);
        });
        
        model.getIndexBuffer().unbind();
        model.getVao().disableAttributes();
        model.getVao().unbind();
        shader.stop();
    }
}
