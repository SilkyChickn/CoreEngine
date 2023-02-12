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
package de.coreengine.system;

import de.coreengine.asset.TextureData;
import de.coreengine.framework.GLFW;
import de.coreengine.framework.Keyboard;
import de.coreengine.framework.Mouse;
import de.coreengine.framework.Window;
import de.coreengine.network.NetworkManager;
import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.rendering.renderer.PostProcesser;
import de.coreengine.sound.AL;
import de.coreengine.util.Configuration;
import de.coreengine.util.FrameTimer;
import de.coreengine.util.Logger;
import de.coreengine.util.gl.MemoryDumper;
import org.lwjgl.opengl.GL;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Class that manage the whole game
 *
 * @author Darius Dinger
 */
public class Game {
    private static final float SYNC_INTERVAL = Configuration.getValuef("SYNC_INTERVAL");

    // Time since last sync
    private static float tsls = 0;

    // Is game paused
    private static boolean paused = false;

    // Scenes of the game
    private static List<Scene> scenes = new LinkedList<>();
    private static Semaphore scenesSem = new Semaphore(1);
    private static int currentScene = 0;

    /**
     * Initializing the game and all relevant libraries
     * 
     * @param windowWidth    Initial width of the game window
     * @param windowHeight   Initial height of the game window
     * @param windowTitle    Initial title of the game window
     * @param windowType     Initial window type
     * @param windowIcon     Icon of the window, or null for no icon
     * @param configOverride Location of the engine configuration file in resources,
     *                       or null to use default configuration
     */
    public static void init(int windowWidth, int windowHeight, String windowTitle, Window.Type windowType,
            TextureData windowIcon, String configOverride) {

        // Load configuration from defaults or override config
        if (configOverride != null)
            Configuration.loadConfig(configOverride);
        else
            Configuration.loadConfig();

        // Init glfw and create window
        GLFW.init();
        Window.create(windowWidth, windowHeight, windowTitle, windowType, windowIcon);

        // Init GL
        GL.createCapabilities();

        // Init AL
        AL.init();

        // Check shader support
        if (!GL.getCapabilities().OpenGL40)
            Logger.err("OpenGL version not supported", "A minimum opengl version of 4.0 is required!");

        // Init engine
        MasterRenderer.init();
        PostProcesser.init();
        Material.init();
    }

    /**
     * Register scene in the game
     * 
     * @param scene Scene to register
     * @return Id of the scene
     */
    public static int registerScene(Scene scene) {
        try {
            scenesSem.acquire();
            scenes.add(scene);
            scenesSem.release();
            return scenes.indexOf(scene);
        } catch (InterruptedException ex) {
            Logger.err("Interrupt exception", "An Interrupt exception occurs by " + "register scene!");
            Game.exit(1);
        }
        return 0;
    }

    public static void removeScene(int id) {
        try {
            scenesSem.acquire();
            scenes.remove(id);
            scenesSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupt exception", "An Interrupt exception occurs by " + "removing scene!");
            Game.exit(1);
        }
    }

    /**
     * @return Current scene or null if no scene set
     */
    public static Scene getCurrentScene() {
        try {
            scenesSem.acquire();
            Scene result = scenes.get(currentScene);
            scenesSem.release();
            return result;
        } catch (InterruptedException ex) {
            Logger.err("Interrupt exception", "An Interrupt exception occurs by " + "getting current scene!");
            Game.exit(1);
        } catch (IndexOutOfBoundsException ex) {
            if (scenes.size() == 0) {
                Logger.err("Error getting scene", "No scene registered!");
            } else {
                Logger.err("Error getting scene", "Scene with id " + currentScene + " doesn't exist!");
            }
            Game.exit(1);
        }
        return null;
    }

    /**
     * Going to specific scene
     * 
     * @param id Id of the scene to enter
     */
    public static void gotoScene(int id) {
        currentScene = id;
    }

    /**
     * Updating inputs handlers, window and executing master renderers render call
     * to render all stuff
     */
    public static void tick() {
        Scene curScene = getCurrentScene();

        // Tick current scene
        if (curScene != null) {

            // Only syncing, when multiplayer
            if (NetworkManager.getState() != NetworkManager.NetworkState.SINGLEPLAYER) {
                tsls += FrameTimer.getTslf();

                // Is it time for new sync
                if (tsls >= SYNC_INTERVAL) {
                    tsls = 0;

                    // Syncronize
                    NetworkManager.sync();
                    curScene.syncronize();
                }
            }

            if (paused)
                curScene.pauseUpdate();
            else
                curScene.update();

            curScene.render();
        }

        MasterRenderer.render();

        Keyboard.update();
        Mouse.update();
        FrameTimer.update();

        Window.update();
    }

    /**
     * Set the game in paused state or the other way round.
     * 
     * @param paused Should game be paused
     */
    public static void setPaused(boolean paused) {
        Game.paused = paused;
    }

    /**
     * @return Is the game currently paused.
     */
    public static boolean isPaused() {
        return paused;
    }

    /**
     * Exiting the game and dumping all reserved memory
     * 
     * @param code Exit code (0 == Success, else Errror)
     */
    public static void exit(int code) {
        MemoryDumper.dumpMemory();
        Window.destroy();
        GLFW.deinit();
        AL.deinit();

        if (code != 0) {
            Logger.saveLog();
            JOptionPane.showMessageDialog(null, "Game crashed! See error log for more information.");
        }

        System.exit(code);
    }
}
