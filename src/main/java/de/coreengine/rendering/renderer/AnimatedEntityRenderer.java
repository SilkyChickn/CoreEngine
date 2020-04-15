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

import de.coreengine.animation.Joint;
import de.coreengine.framework.Keyboard;
import de.coreengine.rendering.model.Mesh;
import de.coreengine.rendering.programs.AnimatedEntityShader;
import de.coreengine.rendering.renderable.AnimatedEntity;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.util.Toolbox;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector4f;
import java.util.HashMap;
import java.util.List;

/**
 * Renderer that can render an model into the world
 *
 * @author Darius Dinger
 */
public class AnimatedEntityRenderer {

    private AnimatedEntityShader shader = new AnimatedEntityShader();

    /**
     * Renders a list of animated entities into the bound framebuffer
     *
     * @param entities  Entity list to render
     * @param cam       Camera to render from
     * @param clipPlane Clip plane of the entities
     */
    void render(HashMap<Mesh, List<AnimatedEntity>> entities, Camera cam, Vector4f clipPlane) {

        // DEBUG ENABLE SKELETON RENDERING
        if (Keyboard.isKeyPressed(GLFW.GLFW_KEY_P)) {
            for (Mesh mesh : entities.keySet())
                for (AnimatedEntity entity : entities.get(mesh))
                    renderSkeleton(entity.getSkeleton(), cam, entity.getTransform().getTransMat());
            return;
        }

        // Setup shader
        shader.start();
        shader.setCamera(cam);
        shader.setClipPlane(clipPlane.x, clipPlane.y, clipPlane.z, clipPlane.w);

        for (Mesh mesh : entities.keySet()) {

            // Bind mesh data
            mesh.getVao().bind();
            mesh.getVao().enableAttributes();
            mesh.getIndexBuffer().bind();

            // Load material into shader
            shader.prepareMaterial(mesh.getMaterial());

            // Iterate instanced entities
            for (AnimatedEntity entity : entities.get(mesh)) {

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

        // Stop shader
        shader.stop();
    }

    /**
     * Render a skeleton for debugging
     *
     * @param skeleton Skeleton to render
     */
    private void renderSkeleton(Joint skeleton, Camera cam, Matrix4f modelMatrix) {

        // calc mvp matrix of the entity
        Matrix4f mvp = new Matrix4f(cam.getViewProjectionMatrix());
        mvp.mul(modelMatrix);

        // Setup gl
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadMatrixf(Toolbox.matrixToFloatArray(mvp));

        // Render skeleton
        renderNode(skeleton, null);
    }

    /**
     * Render node and all child nodes recursively of a skeleton
     *
     * @param node Node to render
     */
    private void renderNode(Joint node, Point3f parent) {

        Point3f pos = new Point3f(0, 0, 0);
        node.getPose().transform(pos);

        // Render joint in blue
        GL11.glColor3f(0, 0, 1);
        GL11.glPointSize(10.0f);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glVertex3f(pos.x, pos.y, pos.z);
        GL11.glEnd();

        // Render bone in green
        if (parent != null) {
            GL11.glColor3f(0, 1, 0);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex3f(parent.x, parent.y, parent.z);
            GL11.glVertex3f(pos.x, pos.y, pos.z);
            GL11.glEnd();
        }

        // Render all children
        for (Joint child : node.getChildren())
            renderNode(child, pos);
    }
}
