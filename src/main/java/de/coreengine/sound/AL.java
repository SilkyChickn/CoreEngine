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
package de.coreengine.sound;

import de.coreengine.system.Game;
import de.coreengine.util.Logger;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

/**Class to manage openAL stuff
 *
 * @author Darius Dinger
 */
public class AL {
    
    //OpenAL context to use
    private static long context;
    
    //Audio device to use
    private static long device;
    
    /**Initalize OpenAL context and get default audio device
     */
    public static void init(){
        
        //Get default audio device and open
        String defDeviceName = ALC10.alcGetString(0, 
                ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        device = ALC10.alcOpenDevice(defDeviceName);
        
        //Create OpenAL context from device
        int[] attribs = {0};
        context = ALC10.alcCreateContext(device, attribs);
        ALC10.alcMakeContextCurrent(context);
        
        //create AL capabilities
        ALCCapabilities alcCaps = ALC.createCapabilities(device);
        ALCapabilities alCaps = org.lwjgl.openal.AL.createCapabilities(alcCaps);
        
        //Check if AL is supportes
        if(!alCaps.OpenAL10){
            Logger.err("Error by init OpenAL", "OpenAL 10 is not supportet!");
            Game.exit(1);
        }
    }
    
    /**Destroy OpenAL context and exit device
     */
    public static void deinit(){
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
    }
}
