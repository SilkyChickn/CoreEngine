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

import de.coreengine.asset.meta.Image;
import de.coreengine.util.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

import javax.vecmath.Matrix4f;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**Class to cteate and manage a glfw window
 *
 * @author Darius Dinger
 */
public class Window implements GLFWWindowSizeCallbackI{
    
    //ID of the glfw window
    private static long window = 0;
    
    //Is fullscreen enabled
    private static boolean fullscreen = false;
    
    //Windows current size/dimension
    private final static Dimension SIZE = new Dimension();
    
    //Windows current aspect
    private static float aspect = 1.0f;
    
    //Video mode of the primary monitor
    private static GLFWVidMode selectedVideoMode;
    
    //Orthogonal projection matrix of the window
    private static final Matrix4f ORTHO_MATRIX = new Matrix4f();
    
    //List with all listener to call at window changes
    private static final List<WindowChangedListener> 
            WINDOW_LISTENERS = new LinkedList<>();
    
    /**Create and show a new glfw-window
     * 
     * @param width Windows width in px
     * @param height Windows height in px
     * @param title Window title
     * @param fullscreen true (fullscreen), false (windowed)
     */
    public static void create(int width, int height, String title, boolean fullscreen){
        Window.fullscreen = fullscreen;
        SIZE.setSize(width, height);
        Window.aspect = 1.0f * SIZE.width / SIZE.height;
        
        recalcOrthoMatrix();
        
        //Exit old window if exist
        if(window != 0){
            GLFW.glfwDestroyWindow(window);
        }
        
        //Disable Window Resizeable
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, 0);
        
        //Create glfw window, sets height, width, title, monitor (if fullscreen enabled)
        //Shared windowis set to 0
        window = GLFW.glfwCreateWindow(width, height, title, 
                fullscreen ? de.coreengine.framework.GLFW.primMonitor : 0, 0);
        
        //Set input listeners for window
        GLFW.glfwSetCursorPosCallback(window, Mouse.MOUSE_MOVED_LISTENER);
        GLFW.glfwSetMouseButtonCallback(window, Mouse.MOUSE_BUTTON_LISTENER);
        GLFW.glfwSetScrollCallback(window, Mouse.MOUSE_WHEEL_LISTENER);
        
        GLFW.glfwSetKeyCallback(window, Keyboard.KEY_PRESSED_LISTENER);
        GLFW.glfwSetCharCallback(window, Keyboard.CHAR_TYPED_LISTENER);
        
        //Set selected video mode to primary monitor default
        selectedVideoMode = GLFW.glfwGetVideoMode(de.coreengine.framework.GLFW.primMonitor);
        
        //Bind OpenGL context
        GLFW.glfwMakeContextCurrent(window);
    }
    
    /**Recalculate orthogonal projection matrix
     */
    private static void recalcOrthoMatrix(){
        ORTHO_MATRIX.setIdentity();
        
        float left = -aspect;
        float right = aspect;
        float top = 1.0f;
        float bottom = -1.0f;
        float zNear = -1.0f;
        float zFar = 1.0f;
        
        ORTHO_MATRIX.m00 = 2.0f / (right -left);
        ORTHO_MATRIX.m03 = -1.0f * (right +left) / (right -left);
        ORTHO_MATRIX.m11 = 2.0f / (top -bottom);
        ORTHO_MATRIX.m13 = -1.0f * (top +bottom) / (top -bottom);
        ORTHO_MATRIX.m22 = -2.0f / (zFar -zNear);
        ORTHO_MATRIX.m23 = -1.0f * (zFar +zNear) / (zFar -zNear);
    }
    
    /**Destroy glfw window
     */
    public static void destroy(){
        GLFW.glfwDestroyWindow(window);
    }
    
    /**Set the window video mode
     * 
     * @param videoMode Video mode to set
     */
    public static void setVideoMode(GLFWVidMode videoMode){
        Window.selectedVideoMode = videoMode;
    }
    
    /**Switch between fullscreen and windowed mode for the glfw window
     * 
     * @param fullscreen true (fullscreen), false (windowed)
     */
    public static void setFullscreen(boolean fullscreen){
        if(window == 0){
            Logger.err("Window not created", "GLFW window was not created yet!");
            return;
        }
        
        //Switch mode, sets width, height, refreshRate to selected mode refresh rate
        GLFW.glfwSetWindowMonitor(window, 
                fullscreen ? de.coreengine.framework.GLFW.primMonitor : 0, 
                0, 0, selectedVideoMode.width(), selectedVideoMode.height(), 
                selectedVideoMode.refreshRate());
        
        Window.fullscreen = fullscreen;
    }
    
    /**@return if the window gets a request to close
     */
    public static boolean keepAlive() {
        if(window == 0){
            Logger.err("Window not created", "GLFW window was not created yet!");
            return false;
        }

        return !GLFW.glfwWindowShouldClose(window);
    }
    
    /**Sets the interval, for the window update (Default 1)
     * 
     * 0 = vsync Disabled
     * 1 or higher = vsync Enabled
     * 
     * 1 Should be enough for vsync, more can async the visual from the logic
     * 
     * @param interval Interval for the update
     */
    public static void setVsyncInterval(int interval){
        GLFW.glfwSwapInterval(interval);
    }
    
    /**Updates glfw window
     */
    public static void update(){
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }
    
    /**Set image as icon for the glfw window
     * 
     * @param image Image to set as icon
     */
    public static void setIcon(Image image) {
        
        //Create GLFWImage from ressource
        GLFWImage glfwImage = GLFWImage.malloc();
        glfwImage.set(image.getWidth(), image.getHeight(), image.getData());
        
        //Create GLFWImage-Buffer from GLFWImage
        GLFWImage.Buffer imagebf = GLFWImage.malloc(1);
        imagebf.put(0, glfwImage);
        
        //Set window image from GLFWImage-Buffer
        GLFW.glfwSetWindowIcon(window, imagebf);
    }
    
    /**Adding new listener that gets called, when the window changed.
     * 
     * @param listner Listener to add
     */
    public static void addWindowListener(WindowChangedListener listner){
        WINDOW_LISTENERS.add(listner);
    }
    
    /**@return The aspect of the current glfw window
     */
    public static float getAspect(){
        return aspect;
    }
    
    /**@return The width of the current glfw window
     */
    public static int getWidth(){
        return SIZE.width;
    }
    
    /**@return The height of the current glfw window
     */
    public static int getHeight(){
        return SIZE.height;
    }
    
    /**@return ID of the glfw window
     */
    public static long getWindow() {
        return window;
    }
    
    /**@return Is the window currently in fullscreen mode
     */
    public static boolean isFullscreen() {
        return fullscreen;
    }
    
    /**Get orthogonal projection matrix of the window
     * 
     * @return Window ortho matrix
     */
    public static Matrix4f getOrthoMatrix() {
        return ORTHO_MATRIX;
    }

    @Override
    public void invoke(long window, int width, int height) {
        
        //Update window stuff
        recalcOrthoMatrix();
        SIZE.setSize(width, height);
        aspect = 1.0f * width / height;
        
        //Call listeners
        WINDOW_LISTENERS.forEach((t) -> t.resolutionChanged(width, height, aspect));
    }
}
