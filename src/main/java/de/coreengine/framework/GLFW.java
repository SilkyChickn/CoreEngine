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

import de.coreengine.system.Game;
import de.coreengine.util.Logger;
import org.lwjgl.glfw.GLFWVidMode;

/**Class to manage glfw context
 *
 * @author Darius Dinger
 */
public class GLFW {
    
    //ID of the primary monitor
    static long primMonitor;
    
    /**Initialize glfw
     */
    public static void init() {
        
        //Init glfw and check for error (can throw exception)
        boolean err = org.lwjgl.glfw.GLFW.glfwInit();
        if(!err){
            Logger.err("GLFW init", "Error by initializing glfw!");
            Game.exit(1);
        }
        
        //Get primary monitor
        primMonitor = org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor();
        
        //Enable 1 interval vsync
        org.lwjgl.glfw.GLFW.glfwSwapInterval(1);
    }
    
    /**@return all supported video modes of the primary monitor
     */
    public static GLFWVidMode.Buffer getPrimaryMonitorVideoModes(){
        
        //Get video mode of the primary monitor
        return org.lwjgl.glfw.GLFW.glfwGetVideoModes(primMonitor);
    }
    
    /**Deinitialize glfw
     */
    public static void deinit(){
        
        //Deinitialize glfw
        org.lwjgl.glfw.GLFW.glfwTerminate();
        
    }
    
}
