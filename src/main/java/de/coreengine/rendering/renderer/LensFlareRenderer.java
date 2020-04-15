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

import de.coreengine.framework.Window;
import de.coreengine.rendering.model.Mesh;
import de.coreengine.rendering.model.singletons.Quad2D;
import de.coreengine.rendering.programs.LensFlareShader;
import de.coreengine.rendering.renderable.LensFlare;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

/**
 * Renderer that can render a lens flar effect
 *
 * @author Darius Dinger
 */
public class LensFlareRenderer {

    private LensFlareShader shader = new LensFlareShader();
    private Vector4f sunPos = new Vector4f();
    private Vector2f fromSun = new Vector2f();

    /**
     * Creating new lens flare renderer
     */
    public LensFlareRenderer() {
        reloadOrthoMatrix();
        Window.addWindowListener((x, y, aspect) -> reloadOrthoMatrix());
    }

    /**
     * (Re)load orthogonal projection matrix
     */
    private void reloadOrthoMatrix() {
        shader.start();
        shader.reloadOrtho(Window.getOrthoMatrix());
        shader.stop();
    }

    /**
     * Rendering lens flare effect onto the bound framebuffer
     * 
     * @param lensFlare Lens flare to render
     */
    void render(LensFlare lensFlare) {

        Mesh model = Quad2D.getInstance();

        shader.start();

        model.getVao().bind();
        model.getVao().enableAttributes();
        model.getIndexBuffer().bind();

        // Calculate sun position on screen
        Matrix4f vpMat = MasterRenderer.getCamera().getViewProjectionMatrix();
        sunPos.set(MasterRenderer.getSun().getPosition().x, MasterRenderer.getSun().getPosition().y,
                MasterRenderer.getSun().getPosition().z, 1.0f);

        vpMat.transform(sunPos);

        sunPos.x /= sunPos.w;
        sunPos.y /= sunPos.w;

        // Check if sun is visible, else dont render
        if (sunPos.w < 0.0f || sunPos.x < -1.0f || sunPos.x > 1.0f || sunPos.y < -1.0f || sunPos.y > 1.0f
                || !MasterRenderer.getSun().isLensFlareEnabled()) {
            return;
        }

        // Calc vector from sun to screen center
        fromSun.set(-sunPos.x, -sunPos.y);

        // Normalize fromSun and get delta offset of tiles
        float length = fromSun.length();
        float delta = length / lensFlare.getTextures().length * 2.0f;
        fromSun.x /= length;
        fromSun.y /= length;

        // Render textures onto vector
        for (int i = 0; i < lensFlare.getTextures().length; i++) {

            float x = sunPos.x + fromSun.x * delta * i;
            float y = sunPos.y + fromSun.y * delta * i;

            // Setting next lens flare
            shader.prepareLensFlareTile(lensFlare.getTextures()[i], lensFlare.getSize(), x, y,
                    1.0f - (i + 1) * delta / 2.0f);

            // Render tile
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.getIndexBuffer().getSize(), GL11.GL_UNSIGNED_INT, 0);
        }

        model.getIndexBuffer().unbind();
        model.getVao().disableAttributes();
        model.getVao().unbind();

        shader.stop();
    }

}
