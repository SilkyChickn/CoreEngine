package demo;

import io.github.suuirad.coreengine.framework.Keyboard;
import io.github.suuirad.coreengine.framework.Mouse;
import io.github.suuirad.coreengine.rendering.renderable.Camera;
import io.github.suuirad.coreengine.rendering.renderer.MasterRenderer;
import io.github.suuirad.coreengine.system.GameObject;
import io.github.suuirad.coreengine.util.FrameTimer;
import org.lwjgl.glfw.GLFW;

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

/**
 *
 * @author Darius Dinger
 */
public class FlyCam extends GameObject{
    
    private Camera cam = new Camera();
    
    private float flySpeed = 2, sprintSpeed = 6;
    private float speed;
    
    private float mouseIntensiveness = 5.0f;
    
    public FlyCam() {
        Mouse.setGrabbed(true);
        Mouse.setVisible(false);

        cam.setX(70.0f);
        cam.setY(30.0f);
        cam.setZ(40.0f);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();

        float speedx = 0, speedy = 0, speedz = 0;
        
        //Looking
        float miw = 1.0f * mouseIntensiveness * FrameTimer.getTslf();
        cam.setYaw(cam.getYaw() +(Mouse.getDx() * miw));
        cam.setPitch(cam.getPitch() +(Mouse.getDy() * miw));
        
        //Check that cameras pitch is between -90 and 90
        if(cam.getPitch() < -90) cam.setPitch(-90);
        else if(cam.getPitch() > 90) cam.setPitch(90);
        
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)){
            speed = sprintSpeed * FrameTimer.getTslf();
        }else{
            speed = flySpeed * FrameTimer.getTslf();
        }
        
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_W)){
            speedz -= speed;
        }
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_S)){
            speedz += speed;
        }
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_A)){
            speedx -= speed;
        }
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_D)){
            speedx += speed;
        }
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)){
            speedy -= speed;
        }
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE)){
            speedy += speed;
        }
        
        //Calc moving directions
        float moveZforward = (float) (speedz * Math.cos(Math.toRadians(-cam.getYaw())));
        float moveXforward = (float) (speedz * Math.sin(Math.toRadians(-cam.getYaw())));
        float moveZside = (float) (speedx * Math.cos(Math.toRadians(-cam.getYaw() + 90)));
        float moveXside = (float) (speedx * Math.sin(Math.toRadians(-cam.getYaw() + 90)));
        
        cam.setX(cam.getPosition().x +moveXforward +moveXside);
        cam.setY(cam.getPosition().y +speedy);
        cam.setZ(cam.getPosition().z +moveZforward +moveZside);

        cam.updateViewMatrix();
    }

    @Override
    public void onRender() {
        MasterRenderer.setCamera(cam);
        super.onRender();
    }

    public Camera getCamera() {
        return cam;
    }
}
