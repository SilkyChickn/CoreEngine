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
package de.coreengine.system;

import com.bulletphysics.dynamics.DynamicsWorld;
import de.coreengine.util.FrameTimer;
import de.coreengine.util.bullet.Physics;

/**Class that represent a scene/level in the game
 *
 * @author Darius Dinger
 */
public class Scene {
    
    //Physic world of the scene
    private final DynamicsWorld dynWorld = Physics.createDynamicsWorld(Physics.GRAVITY_OF_EARTH);
    
    //Root game object of the scene, contains all scene game objects
    private final GameObject rootGameObject = new GameObject() {};
    
    /**Initialize scene
     */
    public void init(){
        rootGameObject.setScene(this);
        rootGameObject.onInit();
    }
    
    /**Syncronize scene with network
     */
    public void syncronize(){
        rootGameObject.onSyncronize();
    }
    
    /**Updating scene
     */
    public void update(){
        rootGameObject.onUpdate();
        dynWorld.stepSimulation(FrameTimer.getTslf());
    }
    
    /**Rendering scene
     */
    public void render(){
        rootGameObject.onRender();
    }
    
    /**Adding new game object to the root game object
     * 
     * @param gameObject Game object to add
     */
    public final void addGameObject(GameObject gameObject){
        rootGameObject.addChild(gameObject);
    }
    
    /**Removing game object from the root game object
     * 
     * @param gameObject Game object to remove
     */
    public void removeGameObject(GameObject gameObject){
        rootGameObject.removeChild(gameObject);
    }
    
    /**Getting the physical collision world of this scene. Here rigidbodys and
     * vehicles can be added or the gravity can be changed.
     * 
     * @return Dynamics physics world of the scene
     */
    public DynamicsWorld getPhysicWorld() {
        return dynWorld;
    }
}
