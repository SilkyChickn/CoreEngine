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

import io.github.suuirad.coreengine.framework.Mouse;
import io.github.suuirad.coreengine.rendering.model.Material;
import io.github.suuirad.coreengine.rendering.renderable.gui.GUIPane;
import io.github.suuirad.coreengine.rendering.renderer.MasterRenderer;
import io.github.suuirad.coreengine.system.GameObject;
import org.lwjgl.glfw.GLFW;

/**Class that represents a button in the 2d or 3d world
 *
 * @author Darius Dinger
 */
public class Button extends GameObject{
    
    //Listener for the button
    private ButtonListener listener = null;
    
    //Pane of the button
    private final GUIPane pane;
    
    //State variables
    private boolean mouseOver = false;
    private boolean clicked = false;
    private boolean pressed = false;
    private boolean mouseEntered = false;
    private boolean mouseLeaved = false;
    private boolean clickAble = true;
    
    //Textures to change to, when state changed
    private int overTexture = Material.TEXTURE_BLACK;
    private int pressedTexture = Material.TEXTURE_BLACK;
    private int texture = Material.TEXTURE_WHITE;
    
    /**Creating new Button and setting ts parent or null, if no parent gui
     * exist
     * 
     * @param parent Parent gui or null
     */
    public Button(GUIPane parent) {
        pane = new GUIPane(parent);
    }
    
    @Override
    public void onUpdate() {
        
        //Reset one time events
        mouseEntered = false;
        mouseLeaved = false;
        clicked = false;
        
        //Check if mouse entered or leaved
        boolean newOver = pane.isMouseOver();
        if(newOver != mouseOver){
            mouseEntered = newOver;
            mouseLeaved = !newOver;
            mouseOver = newOver;
        }
        
        //Check if button gets pressed
        if(mouseOver &&  Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)){
            if(!pressed && clickAble){
                pressed = true;
                clicked = true;
            }
        }else if(!mouseOver &&  Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)){
            clickAble = false;
            pressed = false;
        }else{
            pressed = false;
            clickAble = true;
        }
        
        //Reset textures
        if(pressed){
            pane.setTexture(pressedTexture);
        }else if(mouseOver){
            pane.setTexture(overTexture);
        }else{
            pane.setTexture(texture);
        }
        
        //Call Listener
        if(listener != null){
            if(mouseOver) listener.onMouseOver();
            if(mouseEntered) listener.onMouseEnter();
            if(mouseLeaved) listener.onMouseLeave();
            if(pressed) listener.onPress();
            if(clicked) listener.onClick();
        }
    }
    
    /**@return Gui pane of the button
     */
    public GUIPane getPane() {
        return pane;
    }
    
    @Override
    public void onRender() {
        MasterRenderer.renderGui2D(pane);
    }
    
    /**@return Is the button get clicked
     */
    public boolean isClicked() {
        return clicked;
    }
    
    /**@return Is the mouse entered the button since the last update
     */
    public boolean isMouseEntered() {
        return mouseEntered;
    }
    
    /**@return Is the mouse leaved the button since the last update
     */
    public boolean isMouseLeaved() {
        return mouseLeaved;
    }
    
    /**@return Is the button getting pressed
     */
    public boolean isPressed() {
        return pressed;
    }
    
    public boolean isMouseOver() {
        return mouseOver;
    }
    
    public void setTexture(int texture) {
        pane.setTexture(texture);
        this.texture = texture;
    }
    
    /**Setting the listener to call at state changes or null, to call
     * no listener
     * 
     * @param listener New Listener
     */
    public void setListener(ButtonListener listener) {
        this.listener = listener;
    }
    
    /**Setting texture to change to, at mouse entering button or
     * Material.NO_TEXTURE_SET for no change.
     * 
     * @param overTexture New mouse over texture
     */
    public void setOverTexture(int overTexture) {
        this.overTexture = overTexture;
    }
    
    /**Setting texture to change to, at mouse pressing button or
     * Material.NO_TEXTURE_SET for no change.
     * 
     * @param pressedTexture New mouse pressed texture
     */
    public void setPressedTexture(int pressedTexture) {
        this.pressedTexture = pressedTexture;
    }
}
