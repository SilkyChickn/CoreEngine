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
package de.coreengine.rendering.programs;

import de.coreengine.asset.AssetDatabase;
import de.coreengine.asset.FileLoader;
import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderable.gui.GUIPane;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Matrix4f;

/**
 * Shader for the gui renderer
 *
 * @author Darius Dinger
 */
public class GUIShader extends Shader {

    private final int colorTextureUnit = 0;

    private int transMatLoc, vpMatLoc, colorLoc, textureSetLoc, pickColorLoc, additionalScaleLoc;

    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "gui.vert", true), GL20.GL_VERTEX_SHADER,
                "GUI Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "gui.frag", true), GL20.GL_FRAGMENT_SHADER,
                "GUI Fragment Shader");
    }

    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
    }

    @Override
    protected void loadUniforms() {
        bindTextureUnit("colorTexture", colorTextureUnit);

        colorLoc = getUniformLocation("color");
        pickColorLoc = getUniformLocation("pickColor");
        transMatLoc = getUniformLocation("transMat");
        textureSetLoc = getUniformLocation("textureSet");
        vpMatLoc = getUniformLocation("vpMat");
        additionalScaleLoc = getUniformLocation("additionalScale");
    }

    /**
     * Prepare shader for next gui to render
     * 
     * @param gui Next gui to render
     */
    public void prepareGui(GUIPane gui, Camera cam) {
        setUniform(colorLoc, gui.getColor());
        setUniform(pickColorLoc, gui.getPickColor());

        if (gui.isFacingCamera()) {
            setUniform(transMatLoc, gui.getTransMatFacing(cam));
            setUniform(additionalScaleLoc, gui.getScaleY(), gui.getScaleY());
        } else {
            setUniform(transMatLoc, gui.getTransMat());
            setUniform(additionalScaleLoc, 1, 1);
        }

        if (gui.getTexture() != Material.TEXTURE_BLACK) {
            bindTexture(AssetDatabase.getTexture(gui.getTexture()), colorTextureUnit, GL11.GL_TEXTURE_2D);
            setUniform(textureSetLoc, true);
        } else
            setUniform(textureSetLoc, false);

    }

    /**
     * Setting the vpMat variable of the shader
     * 
     * @param mat Matrix to set as vpMat
     */
    public void setVPMat(Matrix4f mat) {
        setUniform(vpMatLoc, Toolbox.matrixToFloatArray(mat));
    }
}
