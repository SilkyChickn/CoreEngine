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
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderable.Entity;
import de.coreengine.rendering.programs.EntityShader;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector4f;
import java.util.HashMap;
import java.util.List;

/**Renderer that can render an model into the world
 *
 * @author Darius Dinger
 */
public class EntityRenderer {
    
    private EntityShader shader = new EntityShader();
    
    /**Renders a list of entities into the bound framebuffer
     * 
     * @param entities Entity map to render
     * @param cam Camera to render from
     * @param clipPlane Clip plane of the entities
     */
    void render(HashMap<Mesh, List<Entity>> entities, Camera cam, Vector4f clipPlane){

        //Setup shader
        shader.start();
        shader.setCamera(cam);
        shader.setClipPlane(clipPlane.x, clipPlane.y,
                clipPlane.z, clipPlane.w);

        for(Mesh mesh: entities.keySet()){

            //Bind mesh data
            mesh.getVao().bind();
            mesh.getVao().enableAttributes();
            mesh.getIndexBuffer().bind();

            //Load material into shader
            shader.prepareMaterial(mesh.getMaterial());

            //Iterate instanced entities
            for(Entity entity: entities.get(mesh)){

                //Prepare entity
                shader.prepareEntity(entity);

                //Render entity
                GL11.glDrawElements(GL11.GL_TRIANGLES,
                        mesh.getIndexBuffer().getSize(), GL11.GL_UNSIGNED_INT, 0);
            }

            //Unbind mesh data
            mesh.getIndexBuffer().unbind();
            mesh.getVao().disableAttributes();
            mesh.getVao().unbind();
        }

        //Stop shader
        shader.stop();
    }
}
