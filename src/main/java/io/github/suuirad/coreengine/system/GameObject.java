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

import com.bulletphysics.dynamics.ActionInterface;
import com.bulletphysics.dynamics.RigidBody;
import io.github.suuirad.coreengine.util.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**Class that represents an object in the game
 *
 * @author Darius Dinger
 */
public abstract class GameObject {
    
    //Parent game object
    protected GameObject parent = null;
    
    //List with all child game objects
    private final List<GameObject> childs = new LinkedList<>();
    private final Semaphore childsSem = new Semaphore(1);
    
    //Scene, the game object currently belongs to
    private Scene scene = null;
    
    //Is the game object initialized
    private boolean initialized = false;
    
    /**Getting called when the object getting initialized
     */
    public void onInit(){
        try {
            childsSem.acquire();
            childs.forEach((child) -> {
                child.onInit();
                child.initialized = true;
            });
            childsSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs "
                    + "while initialize childs!");
        }
    }
    
    /**Getting called when the object should syncronize with network
     */
    public void onSyncronize(){
        try {
            childsSem.acquire();
            childs.forEach((child) -> {
                if(child.initialized) child.onSyncronize();
            });
            childsSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs "
                    + "while syncrinizing childs!");
        }
    }
    
    /**Getting called when the object getting updated
     */
    public void onUpdate(){
        try {
            childsSem.acquire();
            childs.forEach((child) -> {
            if(child.initialized) child.onUpdate();
                else{
                    child.onInit();
                    child.initialized = true;
                    child.onUpdate();
                }
            });
            childsSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs "
                    + "while updating childs!");
        }
        
    }
    
    /**Getting called when the object getting rendered
     */
    public void onRender(){
        try {
            childsSem.acquire();
            childs.forEach((child) -> {
                if(child.initialized) child.onRender();
            });
            childsSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs "
                    + "while rendering childs!");
        }
    }
    
    /**Getting called when the object getting deleted
     */
    public void onDelete(){
        try {
            childsSem.acquire();
            childs.forEach((child) -> {
                if(child.initialized) child.onDelete();
            });
            childsSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs "
                    + "while deleting childs!");
        }
    }
    
    /**Adding a new child game object to the childs and setting this as parent
     * 
     * @param child Child to add
     */
    public final void addChild(GameObject child){
        try {
            childsSem.acquire();
            childs.add(child);
            childsSem.release();
            child.parent = this;
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs "
                    + "while adding a new child!");
        }
    }
    
    /**Removing child game object from childs
     * 
     * @param child Game object to remove
     */
    public final void removeChild(GameObject child){
        try {
            childsSem.acquire();
            childs.remove(child);
            childsSem.release();
        } catch (InterruptedException ex) {
            Logger.err("Interrupted Exception", "An Interrupted exception occurs "
                    + "while removing a child!");
        }
    }
    
    /**Setting the scene of this game object
     * 
     * @param scene New scene of the game object
     */
    void setScene(Scene scene) {
        this.scene = scene;
    }
    
    /**Getting scene of this game object, by getting scene of the root game
     * object of the tree
     * 
     * @return Scene of the game object
     */
    public Scene getScene(){
        if(parent == null) return scene;
        else return parent.getScene();
    }
    
    /**Adding rigidbody to the physics world of the game objects scene
     * 
     * @param rb Rigidbody to add to physics world
     */
    protected void addRigidBodyToWorld(RigidBody rb){
        getScene().getPhysicWorld().addRigidBody(rb);
    }
    
    /**Adding action interface object to the physics world of the scene
     * 
     * @param ai Action interface object to add
     */
    protected void addActionToPhysicWorld(ActionInterface ai){
        getScene().getPhysicWorld().addAction(ai);
    }
}
