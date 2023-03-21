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
import de.coreengine.rendering.programs.EntityShader;
import de.coreengine.rendering.programs.ShadowMapShader;
import de.coreengine.rendering.renderable.Entity;
import de.coreengine.rendering.renderable.gui.GUIPane;
import de.coreengine.rendering.renderable.light.ShadowLight;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.List;

public class ShadowMapRenderer {

    private ShadowMapShader shader = new ShadowMapShader();

    /**
     * Rendering a list of entities and 3d GUIs into the shadow map of the shadow
     * light. Only the depth information will be rendered!
     *
     * @param entities    Entities to render
     * @param guis        3 Dimensional GUIs to render
     * @param shadowLight Shadow Light to render from
     */
    void render(HashMap<EntityShader, HashMap<Mesh, List<Entity>>> entities, List<GUIPane> guis,
            ShadowLight shadowLight) {
        GL11.glCullFace(GL11.GL_FRONT);
        shadowLight.getShadowMap().bind(GL30.GL_COLOR_ATTACHMENT0);
        GL11.glClearColor(0, 0, 0, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        for (EntityShader currenShader : entities.keySet()) {
            HashMap<Mesh, List<Entity>> entityBatch = entities.get(currenShader);

            shader.start();
            shader.setVPMat(shadowLight.getVpMat());

            for (Mesh mesh : entityBatch.keySet()) {

                // Bind mesh data
                mesh.getVao().bind();
                mesh.getVao().enableAttributes();
                mesh.getIndexBuffer().bind();

                // Iterate instanced entities
                for (Entity entity : entityBatch.get(mesh)) {

                    // Prepare entity
                    shader.prepareEntity(entity);

                    // Render entity
                    GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getIndexBuffer().getSize(), GL11.GL_UNSIGNED_INT, 0);
                }

                // Unbind mesh data
                mesh.getIndexBuffer().unbind();
                mesh.getVao().disableAttributes();
                mesh.getVao().unbind();
            }

            shader.stop();
        }

        shadowLight.getShadowMap().unbind();
        GL11.glCullFace(GL11.GL_BACK);
    }
}
