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

import de.coreengine.asset.FileLoader;
import de.coreengine.rendering.renderable.gui.GUIChar;
import de.coreengine.rendering.renderable.gui.GUIPane;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Matrix4f;

/**Shader for the font renderer
 *
 * @author Darius Dinger
 */
public class FontShader extends Shader{
    
    private final int fontAtlasUnit = 0;
    
    private int mMatTextLoc, vpMatLoc, offsetLoc, scaleLoc, fontColorLoc;
    
    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "font.vert", true), 
                GL20.GL_VERTEX_SHADER, "Font Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "font.frag", true), 
                GL20.GL_FRAGMENT_SHADER, "Font Fragment Shader");
    }
    
    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
        bindAttribute(1, "texCoord");
    }
    
    @Override
    protected void loadUniforms() {
        bindTextureUnit("fontAtlas", fontAtlasUnit);
        
        mMatTextLoc = getUniformLocation("mMatText");
        offsetLoc = getUniformLocation("offset");
        scaleLoc = getUniformLocation("scale");
        vpMatLoc = getUniformLocation("vpMat");
        fontColorLoc = getUniformLocation("fontColor");
    }
    
    /**@param vpMat View projection matrix to load
     */
    public void setVPMat(Matrix4f vpMat){
        setUniform(vpMatLoc, Toolbox.matrixToFloatArray(vpMat));
    }
    
    /**Prepare shader for next text to render
     * 
     * @param pane Pane that contains the text
     */
    public void prepareText(GUIPane pane){
        bindTexture(pane.getText().getFont().getTextureAtlas(), fontAtlasUnit, GL11.GL_TEXTURE_2D);
        setUniform(mMatTextLoc, pane.getRotPosMat());
        setUniform(scaleLoc, pane.getText().getFontSize());
        setUniform(fontColorLoc, pane.getText().getFontColor());
    }
    
    /**Prepare shader fo one next char to render
     * 
     * @param c Char to render
     */
    public void prepareChar(GUIChar c){
        setUniform(offsetLoc, c.getOffset().x, c.getOffset().y);
    }
}
