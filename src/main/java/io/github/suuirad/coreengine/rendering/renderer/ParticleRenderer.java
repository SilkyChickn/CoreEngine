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

import io.github.suuirad.coreengine.rendering.model.SimpleModel;
import io.github.suuirad.coreengine.rendering.model.singletons.Quad2D;
import io.github.suuirad.coreengine.rendering.programs.ParticleShader;
import io.github.suuirad.coreengine.rendering.renderable.Camera;
import io.github.suuirad.coreengine.rendering.renderable.Particle;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;

/**Renderer that can render 3d particles
 */
public class ParticleRenderer {

    private ParticleShader shader = new ParticleShader();

    /**Rendering a batch of particles sortet by texture
     *
     * @param particles Particle batches, sortet by textures
     * @param cam Camera to render particles from
     */
    public void render(HashMap<Integer, List<Particle>> particles, Camera cam){

        SimpleModel model = Quad2D.getInstance();

        shader.start();
        shader.prepareCam(cam);

        model.getVao().bind();
        model.getVao().enableAttributes();

        //Iterate particle textures
        for(int tex: particles.keySet()){
            shader.prepareParticles(tex);

            //Iterate particles for texture
            for(Particle particle: particles.get(tex)){
                shader.setNextTransform(particle.getSize(), particle.getPosition());
                GL11.glDrawArrays(GL11.GL_POINTS, 0, 1);
            }
        }

        model.getVao().disableAttributes();
        model.getVao().unbind();

        shader.stop();
    }
}
