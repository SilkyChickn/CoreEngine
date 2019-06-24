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

import de.coreengine.framework.Mouse;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;
import de.coreengine.util.Configuration;
import de.coreengine.util.FrameTimer;
import org.lwjgl.glfw.GLFW;

import javax.vecmath.Vector3f;

/**Tird person camera game object
 *
 * @author Darius Dinger
 */
public class TPCamera extends GameObject{
    private static final float DEFAULT_DISTANCE = 
            Configuration.getValuef("TPC_DEFAULT_DISTANCE");
    private static final float[] DEFAULT_DISTANCE_LIMIT = 
            Configuration.getValuefa("TPC_DEFAULT_DISTANCE_LIMIT");
    private static final float DEFAULT_PITCH = 
            Configuration.getValuef("TPC_DEFAULT_PITCH");
    private static final float DEFAULT_ROTATE_SPEED = 
            Configuration.getValuef("TPC_DEFAULT_ROTATE_SPEED");
    private static final float DEFAULT_MOVE_SPEED = 
            Configuration.getValuef("TPC_DEFAULT_MOVE_SPEED");
    private static final float DEFAULT_ZOOM_SPEED = 
            Configuration.getValuef("TPC_DEFAULT_ZOOM_SPEED");
    private static final float DEFAULT_COOLDOWN = 
            Configuration.getValuef("TPC_DEFAULT_COOLDOWN");
    private static final float[] DEFAULT_PITCH_LIMIT =
            Configuration.getValuefa("TPC_DEFAULT_PITCH_LIMIT");
    
    //Target to look at
    private Vector3f target = new Vector3f();
    
    //Camera to render
    private Camera camera = new Camera();
    
    //Limits for the distance
    private float[] distanceLimit = DEFAULT_DISTANCE_LIMIT;

    //Limits for the pitch
    private float[] pitchLimit = DEFAULT_PITCH_LIMIT;

    //State variables
    private float distance = DEFAULT_DISTANCE, pitch = DEFAULT_PITCH, rotation;
    private float curDistanceSpeed, curPitchSpeed, curRotationSpeed;
    
    //Speeds
    private float zoomSpeed = DEFAULT_ZOOM_SPEED, rotateSpeed = DEFAULT_ROTATE_SPEED;
    private float cooldown = DEFAULT_COOLDOWN;
    
    @Override
    public void onUpdate() {
        
        //Rotate
        if(Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)){
            curRotationSpeed = Mouse.getDx() * FrameTimer.getTslf() * rotateSpeed;
            curPitchSpeed = Mouse.getDy() * FrameTimer.getTslf() * rotateSpeed;
        }else{
            curRotationSpeed *= cooldown;
            curPitchSpeed *= cooldown;
        }
        
        //Zooming
        if(Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)){
            curDistanceSpeed = -Mouse.getDx() * FrameTimer.getTslf() * zoomSpeed * distance;
        }else{
            curDistanceSpeed *= cooldown;
        }
        
        //Applying changes
        distance += curDistanceSpeed;
        pitch += curPitchSpeed;
        rotation += curRotationSpeed;
        
        //Clamp distance
        if(distance < distanceLimit[0]) distance = distanceLimit[0];
        if(distance > distanceLimit[1]) distance = distanceLimit[1];

        //Clamp pitch
        if(pitch < pitchLimit[0]) pitch = pitchLimit[0];
        if(pitch > pitchLimit[1]) pitch = pitchLimit[1];

        //Calculate Positions / Rotations
        float pitchRadians = (float) Math.toRadians(pitch);
        float horizontalDistance = (float) (distance * Math.cos(pitchRadians));
        float verticalDistance = (float) (distance * Math.sin(pitchRadians));

        float rotationRadians = (float) Math.toRadians(rotation);
        float xOffset = (float) (horizontalDistance * Math.sin(rotationRadians));
        float zOffset = (float) (horizontalDistance * Math.cos(rotationRadians));

        camera.setX(target.x -xOffset);
        camera.setY(target.y +verticalDistance);
        camera.setZ(target.z +zOffset);
        
        camera.setPitch(pitch);
        camera.setYaw(rotation);
        
        camera.updateViewMatrix();

        super.onUpdate();
    }
    
    /**@return Renderable camera of the tp camera
     */
    public Camera getCamera() {
        return camera;
    }

    /**Setting distance of the third person camera to the target
     *
     * @param distance New distance to target
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    /**Setting pitch of the third person camera over the target
     *
     * @param pitch New pitch
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**Setting rotation of the third person camera around the target
     *
     * @param rotation New rotation around target
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public void onRender() {
        MasterRenderer.setCamera(camera);
        super.onRender();
    }
    
    /**@return Read/writeable vector of the target to look at
     */
    public Vector3f getTarget() {
        return target;
    }
}
