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

import com.bulletphysics.dynamics.ActionInterface;
import com.bulletphysics.dynamics.RigidBody;
import de.coreengine.util.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Class that represents an object in the game
 *
 * @author Darius Dinger
 */
public abstract class GameObject {

    // Parent game object
    protected GameObject parent = null;

    // List with all child game objects
    private final List<GameObject> childs = new LinkedList<>();
    private final Semaphore childsSem = new Semaphore(1);

    // Scene, the game object currently belongs to
    private Scene scene = null;

    // Is the game object initialized
    private boolean initialized = false;

    /**
     * This method gets called once in the GameObject life cycle. Even if the
     * GameObject gets removed and readded, this method dont gets called again. Its
     * like an additional constructor, except that its not gets called when the
     * GameObject gets created, but when its first has to action.
     */
    public void onInit() {
        initialized = true;
    }

    /**
     * This method gets called, every time a network update occurs. Here the
     * GameObject has to syncronize all used network variables, like SyncFloats,
     * SimpleEvents, ChatEvents, SyncMatrices, ...
     */
    public void onSyncronize() {
        try {
            childsSem.acquire();
            childs.forEach((child) -> {
                if (!child.initialized)
                    child.onInit();
                child.onSyncronize();
            });
            childsSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs " + "while syncrinizing childs!");
        }
    }

    /**
     * This method gets called every frame before the render method. Here is place
     * for the GameObject logic updates, e.g. input handling, physics, actions, ...
     */
    public void onUpdate() {
        try {
            childsSem.acquire();
            childs.forEach((child) -> {
                if (!child.initialized)
                    child.onInit();
                child.onUpdate();
            });
            childsSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs " + "while updating childs!");
        }
    }

    /**
     * This method gets called every frame when the game is paused before the render
     * method.
     */
    public void onPauseUpdate() {
        try {
            childsSem.acquire();
            childs.forEach((child) -> {
                if (!child.initialized)
                    child.onInit();
                child.onPauseUpdate();
            });
            childsSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs " + "while pause updating childs!");
        }
    }

    /**
     * In this method the GameObject gets rendered onto the screen (if it has an
     * graphical representation). Its primary used for MasterRenderer calls.
     */
    public void onRender() {
        try {
            childsSem.acquire();
            childs.forEach((child) -> {
                if (!child.initialized)
                    child.onInit();
                child.onRender();
            });
            childsSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs " + "while rendering childs!");
        }
    }

    /**
     * This method gets called once, when the GameObject is added to a scene, or to
     * another GameObject as child. If the GameObject gets removed and readded, the
     * method will be called again.
     */
    private void onAdd() {
    }

    /**
     * This method gets called once, when the GameObject gets removed from a scene
     * or its parent GameObject (if it had one). If the GameObject gets added and
     * removed again, the method will be called again.
     */
    private void onRemove() {
    }

    /**
     * This method gets called asynchronous, when the game wants the GameObject to
     * save its current state. If you have to save your current state, convert the
     * relevant data into bytes and return them in this method.
     *
     * @return Current state in bytes
     */
    public byte[] onSave() {
        return null;
    }

    /**
     * This method gets called asynchronous, when the game wants to GameObject to
     * recreate its state from saved data. This method passing a byte array, which
     * contains the data, the GameObject saved/returned with the onSave method.
     *
     * @param state Loaded state in bytes
     */
    public void onLoad(byte[] state) {
    }

    /**
     * Adding a new child game object to the childs and setting this as parent
     * 
     * @param child Child to add
     */
    public final void addChild(GameObject child) {
        try {
            childsSem.acquire();
            childs.add(child);
            childsSem.release();
            child.parent = this;
            child.onAdd();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs " + "while adding a new child!");
        }
    }

    /**
     * Removing child game object from childs
     * 
     * @param child Game object to remove
     */
    public final void removeChild(GameObject child) {
        try {
            childsSem.acquire();
            childs.remove(child);
            childsSem.release();
            child.parent = null;
            child.onRemove();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs " + "while removing a child!");
        }
    }

    /**
     * Setting the scene of this game object
     * 
     * @param scene New scene of the game object
     */
    void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * Getting scene of this game object, by getting scene of the root game object
     * of the tree
     * 
     * @return Scene of the game object
     */
    public Scene getScene() {
        if (parent == null)
            return scene;
        else
            return parent.getScene();
    }

    /**
     * Adding rigidbody to the physics world of the game objects scene
     * 
     * @param rb Rigidbody to add to physics world
     */
    protected void addRigidBodyToWorld(RigidBody rb) {
        getScene().getPhysicWorld().addRigidBody(rb);
    }

    /**
     * Adding action interface object to the physics world of the scene
     * 
     * @param ai Action interface object to add
     */
    protected void addActionToPhysicWorld(ActionInterface ai) {
        getScene().getPhysicWorld().addAction(ai);
    }
}
