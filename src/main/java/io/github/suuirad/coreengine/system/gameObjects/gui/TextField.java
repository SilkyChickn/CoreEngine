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
package io.github.suuirad.coreengine.system.gameObjects.gui;

import io.github.suuirad.coreengine.asset.meta.Font;
import io.github.suuirad.coreengine.framework.Keyboard;
import io.github.suuirad.coreengine.framework.Mouse;
import io.github.suuirad.coreengine.rendering.model.Color;
import io.github.suuirad.coreengine.rendering.renderable.gui.GUIPane;
import io.github.suuirad.coreengine.rendering.renderer.MasterRenderer;
import io.github.suuirad.coreengine.system.GameObject;
import io.github.suuirad.coreengine.util.Configuration;
import io.github.suuirad.coreengine.util.FrameTimer;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

/**Class that represents a textfield in the 2d or 3d world
 *
 * @author Darius Dinger
 */
public class TextField extends GameObject {
    private static final String DEFAULT_CURSOR = 
            Configuration.getValues("TEXT_FIELD_DEFAULT_CURSOR");
    private static final float[] DEFAULT_ACTIVE_COLOR = 
            Configuration.getValuefa("TEXT_FIELD_DEFAULT_ACTIVE_COLOR");
    private static final float[] DEFAULT_BACKGROUND_COLOR = 
            Configuration.getValuefa("TEXT_FIELD_DEFAULT_BACKGROUND_COLOR");
    private static final float DEFAULT_BACKSPACE_TRIGGER_TIME = 
            Configuration.getValuef("TEXT_FIELD_DEFAULT_BACKSPACE_TRIGGER_TIME");
    
    //Pane of the text field
    private final GUIPane pane;
    
    //Cursor char of the text field
    private String cursor = DEFAULT_CURSOR;
    
    //Current text fields text
    private String text = "";
    
    //Is text field currently active
    private boolean active = false;
    
    //Character filter
    private Set<String> filter = null;
    
    //Listener
    private TextFieldListener listener = null;
    
    //Backspacing
    private float backSpaceTrgTime = DEFAULT_BACKSPACE_TRIGGER_TIME;
    private float backSpaceTime = 0.0f;
    
    //Colors
    private Color activeColor = new Color(DEFAULT_ACTIVE_COLOR[0],
            DEFAULT_ACTIVE_COLOR[1], DEFAULT_ACTIVE_COLOR[2]);
    private Color backgroundColor = new Color(DEFAULT_BACKGROUND_COLOR[0], 
            DEFAULT_BACKGROUND_COLOR[1], DEFAULT_BACKGROUND_COLOR[2]);
    
    /**@param parent Parent pane of the text field
     * @param font Initial font of the text field
     */
    public TextField(GUIPane parent, Font font) {
        this.pane = new GUIPane(parent);
        this.pane.getText().setFont(font);
        this.pane.getText().setText(cursor);
        this.pane.enableText();
    }
    
    @Override
    public void onUpdate() {
        if(Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)){
            if(active && !pane.isMouseOver()) enter();
            else active = pane.isMouseOver();
        }
        
        if(active){
            pane.getColor().set(activeColor);
            
            //Check typing
            boolean textChanged = false;
            for(String c: Keyboard.getTypedChars()){
                if (filter == null || filter.contains(c)) {
                    text += c;
                    textChanged = true;
                }
            }
            
            //check back space
            if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_BACKSPACE)){
                if((backSpaceTime == 0.0f || backSpaceTime >= backSpaceTrgTime) 
                        && text.length() > 0){
                    text = text.substring(0, text.length() -1);
                    textChanged = true;
                }
                backSpaceTime += FrameTimer.getTslf();
            }else backSpaceTime = 0.0f;
            
            //Check enter
            if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_ENTER)){
                enter();
            }
            
            //Change pane text
            if(textChanged) setText(text);
        }else pane.getColor().set(backgroundColor);
        
        super.onUpdate();
    }
    
    /**Save entered value
     */
    private void enter(){
        active = false;
        if(listener != null) listener.textChanged(text);
    }
    
    /**@param text New text of the text field
     */
    public void setText(String text) {
        this.text = text;
        String fullText = text +cursor;
        pane.getText().setText(fullText);
    }
    
    /**@return Read/Writeablecolor of this textfield when its active
     */
    public Color getActiveColor() {
        return activeColor;
    }
    
    /**@return Read/Writeable color of this textfield when its not active
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    
    /**@return Current text of the textfield
     */
    public String getText() {
        return text;
    }
    
    /**@param cursor New cursor of the textfield
     */
    public void setCursor(String cursor) {
        this.cursor = cursor;
        setText(text);
    }
    
    /**@return GUIPane of the text field
     */
    public GUIPane getPane() {
        return pane;
    }
    
    @Override
    public void onRender() {
        MasterRenderer.renderGui2D(pane);
        super.onRender();
    }
    
    /**@param listener New listener of the textfield or null to remove listener
     */
    public void setListener(TextFieldListener listener) {
        this.listener = listener;
    }
    
    /**@param filter Setting filter for the textfield or null to remove filter
     */
    public void setFilter(Set<String> filter) {
        this.filter = filter;
    }
}
