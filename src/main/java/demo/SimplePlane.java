package demo;/*
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


import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import de.coreengine.rendering.renderable.gui.GUIPane;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;
import de.coreengine.util.bullet.Physics;
import javax.vecmath.Vector3f;

/**Simple plane game object
 *
 * @author Darius Dinger
 */
public class SimplePlane extends GameObject{
    private static final StaticPlaneShape SHAPE = 
            new StaticPlaneShape(new Vector3f(0, 1, 0), 0);
    
    //Renderable of the plane
    private final GUIPane plane = new GUIPane(null);
    
    //Physics object of the plane
    private final RigidBody physicBody = Physics.createRigidBody(0, SHAPE, false);
    
    @Override
    public void onInit() {
        addRigidBodyToWorld(physicBody);
        plane.setScaleX(100);
        plane.setScaleY(100);
        plane.setPosX(50);
        plane.setPosZ(50);
        plane.setRotX(-90);
    }
    
    @Override
    public void onRender() {
        MasterRenderer.renderGui3D(plane);
    }
}
