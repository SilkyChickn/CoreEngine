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
package de.coreengine.framework;

import de.coreengine.util.Logger;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

/**Class to manage the cursor and get mouse inputs
 *
 * @author Darius Dinger
 */
public class Mouse {
    private static final int MAX_MOUSE_BUTTONS = 16;
    
    //Listeners, when mouse moved or mouse button is clicked
    static final MouseButtonListener MOUSE_BUTTON_LISTENER = new MouseButtonListener();
    static final MouseMovedListener MOUSE_MOVED_LISTENER = new MouseMovedListener();
    static final MouseWheelListener MOUSE_WHEEL_LISTENER = new MouseWheelListener();
    
    //Current mouse position on screen
    private static double posx, posy;
    
    //Last Frame mouse position
    private static double oldx, oldy;
    
    //Delta position from last frame update
    private static double dx, dy, dwheel;
    
    //Array that contains the current button stats (clicked or not) per button
    private static final boolean[] BUTTONS = new boolean[MAX_MOUSE_BUTTONS];
    
    //Is mouse grabbed
    private static boolean grabbed = false;
    
    //Is cursor visible
    private static boolean visible = true;
    
    /**Update the mouse and go to thenext frame. Should be called once per frame
     */
    public static void update(){
        
        //Check if mouse is grabbed
        if(grabbed){
            
            //Calc screen center
            float midX =  Window.getWidth() / 2.0f;
            float midY = Window.getHeight() / 2.0f;
            
            //Calc delta relative to center
            dx = posx -midX;
            dy = posy -midY;
            
            //Grab mouse to center
            org.lwjgl.glfw.GLFW.glfwSetCursorPos(Window.getWindow(), midX, midY);
        }else{
            
            //Calc delta relative to last position
            dx = posx -oldx;
            dy = posy -oldy;
        }
        
        //Reset old mouse position
        oldx = posx;
        oldy = posy;
        
        //Reset scroll
        dwheel = 0.0;
    }
    
    /**@return Is mouse cursor visible at the moment
     */
    public static boolean isVisible() {
        return visible;
    }
    
    /**Setting mouse cursor visibility.<br>
     * true = visible<br>
     * false = invisible<br>
     * 
     * @param visible New visibility
     */
    public static void setVisible(boolean visible) {
        if(Mouse.visible != visible){
            if(visible) org.lwjgl.glfw.GLFW.glfwSetInputMode(Window.getWindow(), 
                    org.lwjgl.glfw.GLFW.GLFW_CURSOR, org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL);
            else org.lwjgl.glfw.GLFW.glfwSetInputMode(Window.getWindow(), 
                    org.lwjgl.glfw.GLFW.GLFW_CURSOR, org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN);
            Mouse.visible = visible;
        }
    }
    
    /**@param grabbed Grab mouse at the center of the screen (true), or not(false)
     */
    public static void setGrabbed(boolean grabbed) {
        Mouse.grabbed = grabbed;
    }
    
    /**@return Is mouse currently grabbed at the center of the screen
     */
    public static boolean isGrabbed() {
        return grabbed;
    }
    
    /**Check if the button is pressed at the moment.<br>
     * Returns false, if button code is out of range!
     * 
     * @param buttonCode Code of the button to check
     * @return True if the button is pressed, else false
     */
    public static boolean isButtonPressed(int buttonCode){
        
        //check if button fits into array, else print warning
        if(buttonCode < 0 || buttonCode > BUTTONS.length -1){
            Logger.warn("Mouse Listener Error", "Button code:" + buttonCode + " is out of range!");
            return false;
        }
        
        return BUTTONS[buttonCode];
    }
    
    /**@return Vertical delta mouse wheel scroll offset since last frame
     */
    public static double getDWheel() {
        return dwheel;
    }
    
    /**@return Delta mouse position offset since last frame x
     */
    public static float getDx() {
        return (float)dx;
    }
    
    /**@return Delta mouse position offset since last frame y
     */
    public static float getDy() {
        return (float)dy;
    }
    
    /**@return Current mouse position x
     */
    public static float getPosx() {
        return (float)posx;
    }
    
    /**@return Current mouse position y
     */
    public static float getPosy() {
        return (float)posy;
    }
    
    /**Mouse wheel scroll listener to set the current scroll offset
     */
    private static class MouseWheelListener implements GLFWScrollCallbackI {
        
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            dwheel = yoffset;
        }
    }
    
    /**Mouse moved listener class that sets the current mouse position
     */
    private static class MouseMovedListener implements GLFWCursorPosCallbackI {
        
        @Override
        public void invoke(long window, double xpos, double ypos) {
            posx = xpos;
            posy = ypos;
        }
    }
    
    /**Mouse button clicked listener class that sets the current clicked mouse buttons
     */
    private static class MouseButtonListener implements GLFWMouseButtonCallbackI {
        
        @Override
        public void invoke(long window, int button, int action, int mods) {
            
            //check if button fits into array, else print warning
            if(button < 0 || button > BUTTONS.length -1){
                Logger.warn("Mouse Listener Error", "Button code:" + button + " is out of range!");
                return;
            }
            
            //Set new button state
            if(action == org.lwjgl.glfw.GLFW.GLFW_PRESS) 
                BUTTONS[button] = true;
            else if(action == org.lwjgl.glfw.GLFW.GLFW_RELEASE) 
                BUTTONS[button] = false;
        }
    }
}
