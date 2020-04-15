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
import de.coreengine.rendering.programs.GUIShader;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderable.gui.GUIPane;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Class that can render 2d/3d gui elements/panes
 *
 * @author Darius Dinger
 */
public class GUIRenderer {

    private GUIShader shader = new GUIShader();

    /**
     * Rendering a gui onto the bound framebuffer
     *
     * @param guis  GUIs to render
     * @param cam   Camera to render from for 3d guis
     * @param world Place the gui into the 3d world
     */
    void render(List<GUIPane> guis, Camera cam, boolean world) {

        Mesh model = Quad2D.getInstance();

        shader.start();

        // render 2d or 3d?
        if (world)
            shader.setVPMat(cam.getViewProjectionMatrix());
        else
            shader.setVPMat(Window.getOrthoMatrix());

        model.getVao().bind();
        model.getVao().enableAttributes();
        model.getIndexBuffer().bind();

        guis.forEach(gui -> {

            // Render pane
            shader.prepareGui(gui);
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.getIndexBuffer().getSize(), GL11.GL_UNSIGNED_INT, 0);
        });

        model.getIndexBuffer().unbind();
        model.getVao().disableAttributes();
        model.getVao().unbind();

        shader.stop();
    }
}
