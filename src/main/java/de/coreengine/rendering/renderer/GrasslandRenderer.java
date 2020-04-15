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

import de.coreengine.rendering.programs.GrasslandShader;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderable.terrain.Terrain;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;

import java.util.List;

/**
 * Class that can render a grassland
 *
 * @author Darius Dinger
 */
public class GrasslandRenderer {

    GrasslandShader shader = new GrasslandShader();

    /**
     * Rendering a grassland using a grassland shader
     * 
     * @param terrains Terrains that contains the grasslands
     * @param camera   Camera to render from
     */
    void renderGrassland(List<Terrain> terrains, Camera camera) {
        GL11.glDisable(GL11.GL_CULL_FACE);

        // Start shader and bind vao and indices
        shader.start();
        shader.setcamera(camera);

        terrains.forEach(terrain -> {
            if (terrain.isGrasslandEnabled()) {
                terrain.getGrassland().getMesh().getVao().bind();
                terrain.getGrassland().getMesh().getVao().enableAttributes();
                terrain.getGrassland().getMesh().getIndexBuffer().bind();

                // Prepare shader
                shader.prepareTerrain(terrain);

                int instances = terrain.getGrassland().getDensity() * terrain.getGrassland().getDensity();

                // Render call
                GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES,
                        terrain.getGrassland().getMesh().getIndexBuffer().getSize(), GL11.GL_UNSIGNED_INT, 0,
                        instances);

                // Unbind vao and indices
                terrain.getGrassland().getMesh().getIndexBuffer().unbind();
                terrain.getGrassland().getMesh().getVao().disableAttributes();
                terrain.getGrassland().getMesh().getVao().unbind();
            }
        });

        shader.stop();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }
}
