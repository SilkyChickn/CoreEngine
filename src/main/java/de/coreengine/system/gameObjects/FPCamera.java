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
package de.coreengine.system.gameObjects;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import de.coreengine.framework.Keyboard;
import de.coreengine.framework.Mouse;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.sound.AudioListener;
import de.coreengine.system.GameObject;
import de.coreengine.util.Configuration;
import de.coreengine.util.FrameTimer;
import de.coreengine.util.bullet.Physics;

import javax.vecmath.Vector3f;

/**Represents a first person camera to walk around
 *
 * @author Darius Dinger
 */
public class FPCamera extends GameObject{
    
    private final int MIN_PICH = Configuration.getValuei("FPC_CAMERA_MIN_PITCH");
    private final int MAX_PICH = Configuration.getValuei("FPC_CAMERA_MAX_PITCH");
    
    //Controls/key bindings (load from config)
    private final int keyForward = Configuration.getValuei("FPC_DEFAULT_KEY_FORWARD");
    private final int keyBackward = Configuration.getValuei("FPC_DEFAULT_KEY_BACKWARD");
    private final int keyLeft = Configuration.getValuei("FPC_DEFAULT_KEY_LEFT");
    private final int keyRight = Configuration.getValuei("FPC_DEFAULT_KEY_RIGHT");
    private final int keyUp = Configuration.getValuei("FPC_DEFAULT_KEY_UP");
    //private final int keyDown = Configuration.getValuei("FPC_DEFAULT_KEY_DOWN");
    private final int keySprint = Configuration.getValuei("FPC_DEFAULT_KEY_SPRINT");
    
    //Settings (load from config)
    private final float mouseIntensiveness = Configuration.getValuef("FPC_DEFAULT_MOUSE_INTENSIVENESS");
    private final float walkSpeed = Configuration.getValuef("FPC_DEFAULT_WALK_SPEED");
    private final float sprintSpeed = Configuration.getValuef("FPC_DEFAULT_SPRINT_SPEED");
    
    //Player size
    private final float playSizeX = Configuration.getValuef("FPC_DEFAULT_PLAYER_SIZE_X");
    private final float playSizeY = Configuration.getValuef("FPC_DEFAULT_PLAYER_SIZE_Y");
    private final float playerMass = Configuration.getValuef("FPC_DEFAULT_PLAYER_MASS");
    
    //Physics
    private final Vector3f move = new Vector3f();
    private final RigidBody rigidBody;
    private final Transform transform = new Transform();
    
    private boolean spacePressed = false;
    
    //Camera to render
    private Camera camera = new Camera();
    
    //Audio listener of the camera
    private AudioListener listener = new AudioListener();
    
    /**Create new fp cam and grab mouse
     * @param x Initial X position
     * @param y Initial Y position
     * @param z Initial Z position
     */
    public FPCamera(float x, float y, float z) {
        Mouse.setGrabbed(true);
        Mouse.setVisible(false);
        
        rigidBody = Physics.createRigidBody(playerMass, 
                new CapsuleShape(playSizeX, playSizeY), false);
        
        transform.origin.set(x, y, z);
        rigidBody.setWorldTransform(transform);
        rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        rigidBody.setDamping(0.75f, 1.0f);
        rigidBody.setFriction(1.25f);
        
        camera.setX(x);
        camera.setY(y);
        camera.setZ(z);
    }
    
    @Override
    public void onInit() {
        addRigidBodyToWorld(rigidBody);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        rigidBody.getWorldTransform(transform);
        camera.setX(transform.origin.x);
        camera.setY(transform.origin.y);
        camera.setZ(transform.origin.z);
        
        //Looking
        float miw = 1.0f * mouseIntensiveness * FrameTimer.getTslf();
        camera.setYaw(camera.getYaw() +(Mouse.getDx() * miw));
        camera.setPitch(camera.getPitch() +(Mouse.getDy() * miw));
        
        //Check that cameras pitch is between -90 and 90
        if(camera.getPitch() < -MAX_PICH) camera.setPitch(-MAX_PICH);
        else if(camera.getPitch() > -MIN_PICH) camera.setPitch(-MIN_PICH);
        
        //Check if player sprinting
        float speed = Keyboard.isKeyPressed(keySprint) ? sprintSpeed : walkSpeed;
        
        //Moving key checking
        float speedx = 0, speedz = 0;
        if(Keyboard.isKeyPressed(keyLeft))
            speedx = -speed;
        if(Keyboard.isKeyPressed(keyRight))
            speedx = speed;
        if(Keyboard.isKeyPressed(keyForward))
            speedz = -speed;
        if(Keyboard.isKeyPressed(keyBackward))
            speedz = speed;
        
        if(Keyboard.isKeyPressed(keyUp)){
            if(!spacePressed){
                move.set(0, 5, 0);
                rigidBody.setLinearVelocity(move);
                spacePressed = true;
            }
        }else spacePressed = false;
        
        if(speedx != 0 || speedz != 0){
            
            //Calc moving directions
            float moveZforward = (float) (speedz * Math.cos(Math.toRadians(-camera.getYaw())));
            float moveXforward = (float) (speedz * Math.sin(Math.toRadians(-camera.getYaw())));
            float moveZside = (float) (speedx * Math.cos(Math.toRadians(-camera.getYaw() + 90)));
            float moveXside = (float) (speedx * Math.sin(Math.toRadians(-camera.getYaw() + 90)));
            
            //Apply force camera
            move.set(moveXforward +moveXside, 0, moveZforward +moveZside);
            rigidBody.applyCentralImpulse(move);
        }
        
        camera.updateViewMatrix();
        
        //Replace audio listener
        listener.getPosition().set(camera.getPosition());
        listener.setOrientation(camera.getRay().getRay().x, 0,
                camera.getRay().getRay().z);
        listener.apply();
        
        move.set(0, 0, 0);
    }
    
    @Override
    public void onRender() {
        super.onRender();
        
        MasterRenderer.setCamera(camera);
    }
    
    /**@return Camera object that used by the first person camera
     */
    public Camera getCamera() {
        return camera;
    }
}
