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

import de.coreengine.asset.TextureData;
import de.coreengine.util.Logger;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

import javax.vecmath.Matrix4f;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to cteate and manage a glfw window
 *
 * @author Darius Dinger
 */
public class Window implements GLFWWindowSizeCallbackI {
    public static enum Type {
        WINDOWED, FULLSCREEN, BORDERLESS_WINDOW
    }

    // ID of the glfw window
    private static long window = 0;

    // Window type
    private static Type type;

    // Windows current size/dimension
    private final static Dimension SIZE = new Dimension();

    private static String title = "";

    // Windows current aspect
    private static float aspect = 1.0f;

    // Orthogonal projection matrix of the window
    private static final Matrix4f ORTHO_MATRIX = new Matrix4f();

    // List with all listener to call at window changes
    private static final List<WindowChangedListener> WINDOW_LISTENERS = new LinkedList<>();

    // Instance of the window for resize callback
    private static final Window callbackInstance = new Window();

    // Window icon
    private static TextureData icon = null;

    // Is the window resizeable
    private static boolean resizeable = true;

    // Has the resolution changed
    private static boolean resolutionChanged = false;

    private Window() {
    }

    /**
     * Create and show a new glfw-window
     * 
     * @param width  Windows width in px
     * @param height Windows height in px
     * @param title  Window title
     * @param type   Type of the window
     * @param icon   Icon of the window or null for no icon
     */
    public static void create(int width, int height, String title, Type type, TextureData icon) {
        Window.type = type;
        SIZE.setSize(width, height);
        Window.aspect = 1.0f * SIZE.width / SIZE.height;
        Window.title = title;
        Window.icon = icon;

        recalcOrthoMatrix();

        recreate();
    }

    public static Buffer getSupportedVideoModes() {
        return glfwGetVideoModes(glfwGetPrimaryMonitor());
    }

    private static void recreate() {

        // Exit old window if exist
        if (window != 0) {
            glfwDestroyWindow(window);
        }

        glfwWindowHint(GLFW_DECORATED, type == Type.BORDERLESS_WINDOW ? 0 : 1);
        glfwWindowHint(GLFW_RESIZABLE, resizeable ? 1 : 0);

        // Create glfw window, sets height, width, title, monitor (if fullscreen
        // enabled)
        // Shared window is set to 0
        window = glfwCreateWindow(SIZE.width, SIZE.height, title, type == Type.FULLSCREEN ? glfwGetPrimaryMonitor() : 0,
                0);

        // Window resize callback
        glfwSetWindowSizeCallback(window, callbackInstance);

        // Set input listeners for window
        glfwSetCursorPosCallback(window, Mouse.MOUSE_MOVED_LISTENER);
        glfwSetMouseButtonCallback(window, Mouse.MOUSE_BUTTON_LISTENER);
        glfwSetScrollCallback(window, Mouse.MOUSE_WHEEL_LISTENER);

        glfwSetKeyCallback(window, Keyboard.KEY_PRESSED_LISTENER);
        glfwSetCharCallback(window, Keyboard.CHAR_TYPED_LISTENER);

        // Set Window icon, if one set
        if (icon != null) {

            // Create GLFWImage from ressource
            GLFWImage glfwImage = GLFWImage.malloc();
            glfwImage.set(icon.getWidth(), icon.getHeight(), icon.getData());

            // Create GLFWImage-Buffer from GLFWImage
            GLFWImage.Buffer imagebf = GLFWImage.malloc(1);
            imagebf.put(0, glfwImage);

            // Set window textureData from GLFWImage-Buffer
            glfwSetWindowIcon(window, imagebf);
        }

        // Bind OpenGL context
        glfwMakeContextCurrent(window);
    }

    /**
     * Recalculate orthogonal projection matrix
     */
    private static void recalcOrthoMatrix() {
        ORTHO_MATRIX.setIdentity();

        float left = -aspect;
        float right = aspect;
        float top = 1.0f;
        float bottom = -1.0f;
        float zNear = -1.0f;
        float zFar = 1.0f;

        ORTHO_MATRIX.m00 = 2.0f / (right - left);
        ORTHO_MATRIX.m03 = -1.0f * (right + left) / (right - left);
        ORTHO_MATRIX.m11 = 2.0f / (top - bottom);
        ORTHO_MATRIX.m13 = -1.0f * (top + bottom) / (top - bottom);
        ORTHO_MATRIX.m22 = -2.0f / (zFar - zNear);
        ORTHO_MATRIX.m23 = -1.0f * (zFar + zNear) / (zFar - zNear);
    }

    /**
     * Destroy glfw window
     */
    public static void destroy() {
        glfwDestroyWindow(window);
    }

    /**
     * Sets the size of the window. Only use supported sizes of the monitor for
     * fullscreen windows. Supported sizes can be get with
     * Window.getSupportedVideoModes()
     * 
     * @param width  New window width
     * @param height New window height
     */
    public static void setSize(int width, int height) {
        SIZE.setSize(width, height);
        glfwSetWindowSize(window, width, height);
    }

    /**
     * Only affective on WINDOWED typed windows
     * 
     * @param resizeable Should the window be resizeable
     */
    public static void setReiszeable(boolean resizeable) {
        Window.resizeable = resizeable;
        recreate();
    }

    /**
     * Switch between window types
     * 
     * @param type New window type
     */
    public static void setType(Type type) {
        Window.type = type;
        recreate();
    }

    /**
     * @return if the window gets a request to close
     */
    public static boolean keepAlive() {
        if (window == 0) {
            Logger.err("Window not created", "GLFW window was not created yet!");
            return false;
        }

        return !glfwWindowShouldClose(window);
    }

    /**
     * Sets the interval, for the window update (Default 1)
     * 
     * 0 = vsync Disabled 1 or higher = vsync Enabled
     * 
     * 1 Should be enough for vsync, more can async the visual from the logic
     * 
     * @param interval Interval for the update
     */
    public static void setVsyncInterval(int interval) {
        glfwSwapInterval(interval);
    }

    /**
     * Updates glfw window
     */
    public static void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();

        // Trigger all callbacks
        if (resolutionChanged) {
            resolutionChanged = false;

            // Update window stuff
            aspect = 1.0f * SIZE.width / SIZE.height;
            recalcOrthoMatrix();

            // Call first callback
            WINDOW_LISTENERS.forEach((t) -> t.resolutionChanged(SIZE.width, SIZE.height, aspect));
        }
    }

    /**
     * Set textureData as icon for the glfw window
     * 
     * @param textureData TextureData to set as icon
     */
    public static void setIcon(TextureData textureData) {

    }

    /**
     * Adding new listener that gets called, when the window changed.
     * 
     * @param listner Listener to add
     */
    public static void addWindowListener(WindowChangedListener listner) {
        WINDOW_LISTENERS.add(listner);
    }

    /**
     * @return The aspect of the current glfw window
     */
    public static float getAspect() {
        return aspect;
    }

    /**
     * @return The width of the current glfw window
     */
    public static int getWidth() {
        return SIZE.width;
    }

    /**
     * @return The height of the current glfw window
     */
    public static int getHeight() {
        return SIZE.height;
    }

    /**
     * @return ID of the glfw window
     */
    public static long getWindow() {
        return window;
    }

    /**
     * @return Current window type
     */
    public static Type getType() {
        return type;
    }

    /**
     * Get orthogonal projection matrix of the window
     * 
     * @return Window ortho matrix
     */
    public static Matrix4f getOrthoMatrix() {
        return ORTHO_MATRIX;
    }

    @Override
    public void invoke(long window, int width, int height) {
        if (width == 0 || height == 0)
            return;

        SIZE.setSize(width, height);
        resolutionChanged = true;
    }
}
