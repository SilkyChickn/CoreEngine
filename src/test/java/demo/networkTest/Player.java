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
package demo.networkTest;

import com.bulletphysics.collision.shapes.CapsuleShape;
import io.github.suuirad.coreengine.asset.ObjLoader;
import io.github.suuirad.coreengine.framework.Keyboard;
import io.github.suuirad.coreengine.network.syncronized.SyncFloat;
import io.github.suuirad.coreengine.rendering.renderable.Entity;
import io.github.suuirad.coreengine.rendering.renderer.MasterRenderer;
import io.github.suuirad.coreengine.system.PlayerGameObject;
import io.github.suuirad.coreengine.util.FrameTimer;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Darius Dinger
 */
public class Player extends PlayerGameObject{
    
    private static final float SPEED = 10.0f;
    
    private Entity playerEntity;
    private SyncFloat posX, posY, posZ;

    @Override
    public void onJoin() {
        System.out.println(getPlayerName() + " joined");
    }
    
    @Override
    public void onInit() {
        playerEntity = new Entity();
        playerEntity.setModel(ObjLoader.loadModel("res/models/Cube/Cube.obj", 
                new CapsuleShape(1, 2), false, null));
        
        playerEntity.getTransform().setPosX(0);
        playerEntity.getTransform().setPosY(0);
        playerEntity.getTransform().setPosZ(-100);
        
        playerEntity.getTransform().setScaleX(1.0f);
        playerEntity.getTransform().setScaleY(1.0f);
        playerEntity.getTransform().setScaleZ(1.0f);
        
        posX = new SyncFloat(getPlayerName() + "posx");
        posY = new SyncFloat(getPlayerName() + "posy");
        posZ = new SyncFloat(getPlayerName() + "posz");
    }
    
    @Override
    public void onSyncronize() {
        posX.syncronize();
        posY.syncronize();
        posZ.syncronize();
    }
    
    @Override
    public void onUpdate() {
        if(isControlled()){
            if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_W)){
                posZ.set(posZ.get() -(SPEED * FrameTimer.getTslf()));
            }if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_S)){
                posZ.set(posZ.get() +(SPEED * FrameTimer.getTslf()));
            }if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_A)){
                posX.set(posX.get() -(SPEED * FrameTimer.getTslf()));
            }if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_D)){
                posX.set(posX.get() +(SPEED * FrameTimer.getTslf()));
            }
        }
        
        playerEntity.getTransform().setPosX(posX.get());
        playerEntity.getTransform().setPosY(posY.get());
        playerEntity.getTransform().setPosZ(posZ.get());
    }
    
    @Override
    public void onDisconnect() {
        System.out.println(getPlayerName() + " disconnected");
        getScene().removeGameObject(this);
    }
    
    @Override
    public void onRender() {
        MasterRenderer.renderEntity(playerEntity);
    }
}
