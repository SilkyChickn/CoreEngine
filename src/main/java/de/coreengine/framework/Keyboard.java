/*
 * Copyright (c) 2019, Darius Dinger
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.coreengine.framework;

import de.coreengine.util.Logger;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;

/**Class for checking key states
 *
 * @author Darius Dinger
 */
public class Keyboard {
    public static final int MAX_KEYS = 1024;
    
    //Keyboard listener for key pressing
    static final KeyPressedListener KEY_PRESSED_LISTENER = new KeyPressedListener();
    static final CharTypedListener CHAR_TYPED_LISTENER = new CharTypedListener();
    
    //Array that contains the current key stats (pressed or not) per key
    private static final boolean[] KEYS = new boolean[MAX_KEYS];
    
    //List of all typed chars since last frame
    private static final List<String> TYPED_CHARS = new LinkedList<>();
    
    /**Check if the key is pressed at the moment.<br>
     * Returns false, if key code is out of range!
     * 
     * @param keyCode Code of the key to check
     * @return True if the key is pressed, else false
     */
    public static boolean isKeyPressed(int keyCode){
        
        //check if key fits into array, else print warning
        if(keyCode < 0 || keyCode > KEYS.length -1){
            Logger.warn("Key Listener Error", "Key code:" + keyCode + " is out of range!");
            return false;
        }
        
        return KEYS[keyCode];
    }
    
    /**Update keyboard listener
     */
    public static void update(){
        TYPED_CHARS.clear();
    }
    
    /**@return Typed chars since last frame
     */
    public static List<String> getTypedChars() {
        return TYPED_CHARS;
    }
    
    /**Character typed listener class that adding a typed char to typed chars
     */
    private static class CharTypedListener implements GLFWCharCallbackI {
        
        @Override
        public void invoke(long window, int codepoint) {
            
            //Adding character to last typed
            String c = Character.toString((char) codepoint);
            if(c != null) TYPED_CHARS.add(c);
        }
    }
    
    /**Keyboard key pressed listener class that sets the current pressed keys
     */
    private static class KeyPressedListener implements GLFWKeyCallbackI {
        
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            
            //check if key fits into array, else print warning
            if(key < 0 || key > KEYS.length -1){
                Logger.warn("Key Listener Error", "Key code:" + key + " is out of range!");
                return;
            }
            
            //Set new key state
            if(action == org.lwjgl.glfw.GLFW.GLFW_PRESS) {
                KEYS[key] = true;
            } else if(action == org.lwjgl.glfw.GLFW.GLFW_RELEASE) {
                KEYS[key] = false;
            }
        }
    }
}
