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
package io.github.suuirad.coreengine.system;

import io.github.suuirad.coreengine.framework.GLFW;
import io.github.suuirad.coreengine.framework.Keyboard;
import io.github.suuirad.coreengine.framework.Mouse;
import io.github.suuirad.coreengine.framework.Window;
import io.github.suuirad.coreengine.network.NetworkManager;
import io.github.suuirad.coreengine.rendering.renderer.MasterRenderer;
import io.github.suuirad.coreengine.rendering.renderer.PostProcesser;
import io.github.suuirad.coreengine.sound.AL;
import io.github.suuirad.coreengine.util.Configuration;
import io.github.suuirad.coreengine.util.FrameTimer;
import io.github.suuirad.coreengine.util.Logger;
import io.github.suuirad.coreengine.util.gl.MemoryDumper;
import org.lwjgl.opengl.GL;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**Class that manage the whole game
 *
 * @author Darius Dinger
 */
public class Game {
    private static final float SYNC_INTERVAL = 
            Configuration.getValuef("SYNC_INTERVAL");
    
    //Time since last sync
    private static float tsls = 0;
    
    //Scenes of the game
    private static  List<Scene> scenes = new LinkedList<>();
    private static Semaphore scenesSem = new Semaphore(1);
    private static int currentScene = 0;
    
    /**Initializing the game and all relevant libraries
     * 
     * @param windowWidth Initial width of the game window
     * @param windowHeight Initial height of the game window
     * @param windowTitle Initial title of the game window
     * @param startFullscreen Should game window start in fullscreen
     */
    public static void init(int windowWidth, int windowHeight, String windowTitle, 
            boolean startFullscreen){
        
        //Init glfw and create window
        GLFW.init();
        Window.create(windowWidth, windowHeight, windowTitle, startFullscreen);
        
        //Init GL
        GL.createCapabilities();
        
        //Init AL
        AL.init();
        
        //Check shader support
        if(!GL.getCapabilities().OpenGL40) Logger.err(
                "OpenGL version not supported", 
                "A minimum opengl version of 4.0 is required!"
        );
        
        //Init engine
        MasterRenderer.init();
        PostProcesser.init();
    }
    
    /**Register scene in the game
     * 
     * @param scene Scene to register
     * @return Id of the scene
     */
    public static int registerScene(Scene scene){
        try {
            scenesSem.acquire();
            scenes.add(scene);
            scenesSem.release();
            scene.init();
            return scenes.indexOf(scene);
        } catch (InterruptedException ex) {
            Logger.err("Interrupt exception", "An Interrupt exception occurs by "
                    + "register scene!");
            Game.exit(1);
        }
        return 0;
    }
    
    public static void removeScene(int id){
        try {
            scenesSem.acquire();
            scenes.remove(id);
            scenesSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupt exception", "An Interrupt exception occurs by "
                    + "removing scene!");
            Game.exit(1);
        }
    }
    
    /**@return Current scene or null if no scene set
     */
    public static Scene getCurrentScene(){
        try {
            scenesSem.acquire();
            Scene result = scenes.get(currentScene);
            scenesSem.release();
            return result;
        } catch (InterruptedException ex) {
            Logger.err("Interrupt exception", "An Interrupt exception occurs by "
                    + "getting current scene!");
            Game.exit(1);
        }
        return null;
    }
    
    /**Going to specific scene
     * 
     * @param id Id of the scene to enter
     */
    public static void gotoScene(int id){
        currentScene = id;
    }
    
    /**Updating inputs handlers, window and executing master renderers render 
     * call to render all stuff
     */
    public static void tick(){
        Scene curScene = getCurrentScene();
        
        //Tick current scene
        if(curScene != null){
            
            //Only syncing, when multiplayer
            if(NetworkManager.getState() != NetworkManager.NetworkState.SINGLEPLAYER){
                tsls += FrameTimer.getTslf();
                
                //Is it time for new sync
                if(tsls >= SYNC_INTERVAL){
                    tsls = 0;
                    
                    //Syncronize
                    NetworkManager.sync();
                    curScene.syncronize();
                }
            }
            
            curScene.update();
            curScene.render();
        }
        
        MasterRenderer.render();
        
        Keyboard.update();
        Mouse.update();
        FrameTimer.update();
        
        Window.update();
    }
    
    /**Exiting the game and dumping all reserved memory
     * 
     * @param code Exit code (0 == Success, else Errror)
     */
    public static void exit(int code){
        MemoryDumper.dumpMemory();
        GLFW.deinit();
        AL.deinit();
        
        if(code != 0) JOptionPane.showInternalMessageDialog(null, 
                "Game crashed! See error log for more information.");
        
        System.exit(code);
    }
}
