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


import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import de.coreengine.asset.ObjLoader;
import de.coreengine.rendering.model.Model;
import de.coreengine.rendering.renderable.Entity;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;
import de.coreengine.util.bullet.Physics;
import javax.vecmath.Vector3f;

/**Simple cube game object
 *
 * @author Darius Dinger
 */
public class SimpleCube extends GameObject{
    private static final Model MODEL = ObjLoader.loadModel
        ("res/models/Cube/Cube.obj", new BoxShape(new Vector3f(1, 1, 1)), false, null);
    
    //Cube renderable
    private Entity cubeEntity = new Entity();
    private Transform initPos = new Transform();
    
    //Cube physic object
    private RigidBody physicBody;
    
    public SimpleCube(float x, float y, float z) {
        cubeEntity.getTransform().setScaleX(2);
        cubeEntity.getTransform().setScaleY(2);
        cubeEntity.getTransform().setScaleZ(2);
        initPos.origin.set(x, y, z);
        
        MODEL.getShape().setLocalScaling(new Vector3f(2, 2, 2));
        physicBody = Physics.createRigidBody(25.0f, MODEL.getShape(), true);
    }
    
    @Override
    public void onInit() {
        addRigidBodyToWorld(physicBody);
        physicBody.setWorldTransform(initPos);
        physicBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        cubeEntity.setModel(MODEL);
    }
    
    public RigidBody getPhysicBody() {
        return physicBody;
    }
    
    @Override
    public void onRender() {
        MasterRenderer.renderEntity(cubeEntity);
    }
    
    @Override
    public void onUpdate() {
        cubeEntity.getTransform().setFromRigidBody(physicBody);
    }
}
