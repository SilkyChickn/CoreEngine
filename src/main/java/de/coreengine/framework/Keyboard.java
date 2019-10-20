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
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.util.LinkedList;
import java.util.List;

/**Class for checking key states
 *
 * @author Darius Dinger
 */
public class Keyboard {
    private static final int MAX_KEYS = 1024;
    
    //Key codes (From GLFW)
    public static final int 
            KEY_UNKNOWN = -1,
            KEY_SPACE         = 32,
            KEY_APOSTROPHE    = 39,
            KEY_COMMA         = 44,
            KEY_MINUS         = 45,
            KEY_PERIOD        = 46,
            KEY_SLASH         = 47,
            KEY_0             = 48,
            KEY_1             = 49,
            KEY_2             = 50,
            KEY_3             = 51,
            KEY_4             = 52,
            KEY_5             = 53,
            KEY_6             = 54,
            KEY_7             = 55,
            KEY_8             = 56,
            KEY_9             = 57,
            KEY_SEMICOLON     = 59,
            KEY_EQUAL         = 61,
            KEY_A             = 65,
            KEY_B             = 66,
            KEY_C             = 67,
            KEY_D             = 68,
            KEY_E             = 69,
            KEY_F             = 70,
            KEY_G             = 71,
            KEY_H             = 72,
            KEY_I             = 73,
            KEY_J             = 74,
            KEY_K             = 75,
            KEY_L             = 76,
            KEY_M             = 77,
            KEY_N             = 78,
            KEY_O             = 79,
            KEY_P             = 80,
            KEY_Q             = 81,
            KEY_R             = 82,
            KEY_S             = 83,
            KEY_T             = 84,
            KEY_U             = 85,
            KEY_V             = 86,
            KEY_W             = 87,
            KEY_X             = 88,
            KEY_Y             = 89,
            KEY_Z             = 90,
            KEY_LEFT_BRACKET  = 91,
            KEY_BACKSLASH     = 92,
            KEY_RIGHT_BRACKET = 93,
            KEY_GRAVE_ACCENT  = 96,
            KEY_WORLD_1       = 161,
            KEY_WORLD_2       = 162,
            KEY_ESCAPE        = 256,
            KEY_ENTER         = 257,
            KEY_TAB           = 258,
            KEY_BACKSPACE     = 259,
            KEY_INSERT        = 260,
            KEY_DELETE        = 261,
            KEY_RIGHT         = 262,
            KEY_LEFT          = 263,
            KEY_DOWN          = 264,
            KEY_UP            = 265,
            KEY_PAGE_UP       = 266,
            KEY_PAGE_DOWN     = 267,
            KEY_HOME          = 268,
            KEY_END           = 269,
            KEY_CAPS_LOCK     = 280,
            KEY_SCROLL_LOCK   = 281,
            KEY_NUM_LOCK      = 282,
            KEY_PRINT_SCREEN  = 283,
            KEY_PAUSE         = 284,
            KEY_F1            = 290,
            KEY_F2            = 291,
            KEY_F3            = 292,
            KEY_F4            = 293,
            KEY_F5            = 294,
            KEY_F6            = 295,
            KEY_F7            = 296,
            KEY_F8            = 297,
            KEY_F9            = 298,
            KEY_F10           = 299,
            KEY_F11           = 300,
            KEY_F12           = 301,
            KEY_F13           = 302,
            KEY_F14           = 303,
            KEY_F15           = 304,
            KEY_F16           = 305,
            KEY_F17           = 306,
            KEY_F18           = 307,
            KEY_F19           = 308,
            KEY_F20           = 309,
            KEY_F21           = 310,
            KEY_F22           = 311,
            KEY_F23           = 312,
            KEY_F24           = 313,
            KEY_F25           = 314,
            KEY_KP_0          = 320,
            KEY_KP_1          = 321,
            KEY_KP_2          = 322,
            KEY_KP_3          = 323,
            KEY_KP_4          = 324,
            KEY_KP_5          = 325,
            KEY_KP_6          = 326,
            KEY_KP_7          = 327,
            KEY_KP_8          = 328,
            KEY_KP_9          = 329,
            KEY_KP_DECIMAL    = 330,
            KEY_KP_DIVIDE     = 331,
            KEY_KP_MULTIPLY   = 332,
            KEY_KP_SUBTRACT   = 333,
            KEY_KP_ADD        = 334,
            KEY_KP_ENTER      = 335,
            KEY_KP_EQUAL      = 336,
            KEY_LEFT_SHIFT    = 340,
            KEY_LEFT_CONTROL  = 341,
            KEY_LEFT_ALT      = 342,
            KEY_LEFT_SUPER    = 343,
            KEY_RIGHT_SHIFT   = 344,
            KEY_RIGHT_CONTROL = 345,
            KEY_RIGHT_ALT     = 346,
            KEY_RIGHT_SUPER   = 347,
            KEY_MENU          = 348,
            KEY_LAST          = KEY_MENU;
    
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
            TYPED_CHARS.add(c);
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
