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
package de.coreengine.util.bullet;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.util.ObjectArrayList;

import javax.vecmath.Vector3f;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**Class that contains someuseful jbullet methods
 *
 * @author Darius Dinger
 */
public class Physics {
    public static final float GRAVITY_OF_EARTH = -9.81f; /* N/Kg */
    
    /**Creating a default JBullet dynamics world
     * 
     * @param gravity Gravity of the world
     * @return Created dynamics world
     */
    public static DynamicsWorld createDynamicsWorld(float gravity){
        
        //Create collision world setting
        CollisionConfiguration config = new DefaultCollisionConfiguration();
        Dispatcher dispatcher = new CollisionDispatcher(config);
        BroadphaseInterface broadphaseInterface = new DbvtBroadphase();
        ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
        
        //Create collision/dynamics world and set gravity
        DynamicsWorld dynWorld = new DiscreteDynamicsWorld(dispatcher, broadphaseInterface, 
                constraintSolver, config);
        dynWorld.setGravity(new Vector3f(0, gravity, 0));
        
        return dynWorld;
    }
    
    /**Creates a collision shape for a triangle mesh shape. Only for static objects!
     * 
     * @param vertices 3D vertices of the body
     * @param indices Indices that connect the vertices
     * @return Created triangle mesh shaped rigid body
     */
    public static BvhTriangleMeshShape createTriangleMeshShape(float[] vertices, int[][] indices){
        
        //Create index vertex array
        TriangleIndexVertexArray tiva = new TriangleIndexVertexArray();

        for(int[] i: indices){

            IndexedMesh mesh = new IndexedMesh();
            mesh.numTriangles = i.length / 3;
            mesh.triangleIndexBase = ByteBuffer.allocateDirect(i.length * 4).order(ByteOrder.nativeOrder());
            mesh.triangleIndexBase.asIntBuffer().put(i);
            mesh.triangleIndexStride = 3 * 4;
            mesh.numVertices = vertices.length / 3;
            mesh.vertexBase = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder());
            mesh.vertexBase.asFloatBuffer().put(vertices);
            mesh.vertexStride = 3 * 4;
            tiva.addIndexedMesh(mesh);
        }
        
        //Create shape
        return new BvhTriangleMeshShape(tiva, true);
    }

    /**Creates a collision shape for a triangle mesh shape. Only for static objects!
     *
     * @param vertices 3D vertices of the body
     * @param indices Indices that connect the vertices
     * @return Created triangle mesh shaped rigid body
     */
    public static BvhTriangleMeshShape createTriangleMeshShape(float[] vertices, int[] indices){

        //Create index vertex array
        TriangleIndexVertexArray tiva = new TriangleIndexVertexArray();

        IndexedMesh mesh = new IndexedMesh();
        mesh.numTriangles = indices.length / 3;
        mesh.triangleIndexBase = ByteBuffer.allocateDirect(indices.length * 4).order(ByteOrder.nativeOrder());
        mesh.triangleIndexBase.asIntBuffer().put(indices);
        mesh.triangleIndexStride = 3 * 4;
        mesh.numVertices = vertices.length / 3;
        mesh.vertexBase = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder());
        mesh.vertexBase.asFloatBuffer().put(vertices);
        mesh.vertexStride = 3 * 4;
        tiva.addIndexedMesh(mesh);

        //Create shape
        return new BvhTriangleMeshShape(tiva, true);
    }

    /**Creating a convex hull shape from vertices. 
     * Also posible for dynamic objects.
     * 
     * @param vertices Vertex positions of the convex hull
     * @return Create convex hull shape
     */
    public static ConvexHullShape createConvexHullShape(float[] vertices){
        
        ObjectArrayList<Vector3f> points = new ObjectArrayList<>();
        for(int i = 0; i < vertices.length / 3; i++){
            points.add(new Vector3f(vertices[i * 3], vertices[i * 3 +1], 
                    vertices[i * 3 +2]));
        }
        
        return new ConvexHullShape(points);
    }
    
    /**Creating new RigidBody with a specific shape. 
     * A mass of zero means static object.
     * 
     * @param mass Mass of the body (or zero for a static body)
     * @param shape Shape of the body
     * @param rotate Can the body rotate
     * @return Created rigidy body
     */
    public static RigidBody createRigidBody(float mass, CollisionShape shape, 
            boolean rotate){
        if(rotate){
            Vector3f inertia = new Vector3f();
            shape.calculateLocalInertia(mass, inertia);
            return new RigidBody(mass, new DefaultMotionState(), shape, inertia);
        }else return new RigidBody(mass, new DefaultMotionState(), shape);
    }
    
    /**Create new terrain rigid body.
     * 
     * @return Created terrain rigid body
     */
    public static RigidBody createTerrainRigidBody(){
        
        return null;
    }
    
    /**Scaling a rigidbody in a dynamics world
     * 
     * @param body Rigidbody to scale
     * @param sx X Scale of the rigid body
     * @param sy Y Scale of the rigid body
     * @param sz Z Scale of the rigid body
     * @param physicWorld Dynamic world, the rigid body belongs to
     */
    public static void scaleRigidBody(RigidBody body, float sx, 
            float sy, float sz, DynamicsWorld physicWorld){
        body.getCollisionShape().setLocalScaling(new Vector3f(sx, sy, sz));
        physicWorld.updateSingleAabb(body);
    }
}
