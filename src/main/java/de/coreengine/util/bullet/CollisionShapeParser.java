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

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import de.coreengine.util.Toolbox;

import javax.vecmath.Vector3f;

/**
 * Parse and unparsing collisionshapes from/into strings
 *
 * @author Darius Dinger
 */
public class CollisionShapeParser {

        private static final ConvexHullShape EMPTY_CONVEX_HULL = new ConvexHullShape(new ObjectArrayList<>());

        private static final TriangleMeshShape EMPTY_TRIANGLE_MESH = new BvhTriangleMeshShape();

        /**
         * Parsing collision shape from string. No encapsulation of multiple compound
         * shapes supported! Returning null for convex hull shape, triangle mesh shape
         * or impossible parse
         * 
         * @param line String to parse
         * @return Parsed collision shape or null
         */
        public static CollisionShape toShape(String line) {
                if (line == null || line.startsWith("convex"))
                        return EMPTY_CONVEX_HULL;
                else if (line.startsWith("triangleMesh"))
                        return EMPTY_TRIANGLE_MESH;
                else if (line.startsWith("compound")) {
                        String[] args = line.split(" ");
                        CompoundShape shape = new CompoundShape();
                        for (int i = 1; i < args.length; i += 2) {
                                CollisionShape child = toShape(args[i]);
                                Transform transform = new Transform();
                                transform.setFromOpenGLMatrix(Toolbox.stringToArrayf(args[i + 1], "-"));
                                assert child != null;
                                shape.addChildShape(transform, child);
                        }
                        return shape;
                } else if (line.startsWith("sphere")) {
                        String[] args = line.split("/");
                        return new SphereShape(Float.parseFloat(args[1]));
                } else if (line.startsWith("box")) {
                        String[] args = line.split("/");
                        return new BoxShape(new Vector3f(Float.parseFloat(args[1]), Float.parseFloat(args[2]),
                                        Float.parseFloat(args[3])));
                } else if (line.startsWith("capsule")) {
                        String[] args = line.split("/");
                        return new CapsuleShape(Float.parseFloat(args[1]), Float.parseFloat(args[2]));
                } else if (line.startsWith("capsulex")) {
                        String[] args = line.split("/");
                        return new CapsuleShapeX(Float.parseFloat(args[1]), Float.parseFloat(args[2]));
                } else if (line.startsWith("capsulez")) {
                        String[] args = line.split("/");
                        return new CapsuleShapeZ(Float.parseFloat(args[1]), Float.parseFloat(args[2]));
                } else if (line.startsWith("cylinder")) {
                        String[] args = line.split("/");
                        return new CylinderShape(new Vector3f(Float.parseFloat(args[1]), Float.parseFloat(args[2]),
                                        Float.parseFloat(args[3])));
                } else if (line.startsWith("cylinderx")) {
                        String[] args = line.split("/");
                        return new CylinderShapeX(new Vector3f(Float.parseFloat(args[1]), Float.parseFloat(args[2]),
                                        Float.parseFloat(args[3])));
                } else if (line.startsWith("cylinderz")) {
                        String[] args = line.split("/");
                        return new CylinderShapeZ(new Vector3f(Float.parseFloat(args[1]), Float.parseFloat(args[2]),
                                        Float.parseFloat(args[3])));
                } else if (line.startsWith("cone")) {
                        String[] args = line.split("/");
                        return new ConeShape(Float.parseFloat(args[1]), Float.parseFloat(args[2]));
                } else if (line.startsWith("conex")) {
                        String[] args = line.split("/");
                        return new ConeShapeX(Float.parseFloat(args[1]), Float.parseFloat(args[2]));
                } else if (line.startsWith("conez")) {
                        String[] args = line.split("/");
                        return new ConeShapeZ(Float.parseFloat(args[1]), Float.parseFloat(args[2]));
                }
                return null;
        }

        /**
         * Convert a collision shape into a string. No encapsulation of multiple
         * compound shapes supported!
         * 
         * @param shape Shape to convert
         * @return Converted shape as string
         */
        public static String toString(CollisionShape shape) {
                if (shape.getShapeType() == BroadphaseNativeType.CONVEX_HULL_SHAPE_PROXYTYPE)
                        return "convex";
                else if (shape.getShapeType() == BroadphaseNativeType.TRIANGLE_MESH_SHAPE_PROXYTYPE)
                        return "triangleMesh";
                else if (shape.getShapeType() == BroadphaseNativeType.COMPOUND_SHAPE_PROXYTYPE) {
                        CompoundShape comp = (CompoundShape) shape;
                        StringBuilder result = new StringBuilder("compound");
                        for (CompoundShapeChild child : comp.getChildList()) {
                                result.append(" ").append(toString(child.childShape));

                                float[] mat = new float[16];
                                child.transform.getOpenGLMatrix(mat);
                                result.append(" ").append(Toolbox.arrayToString(mat, "-"));
                        }
                        return result.toString();
                } else if (shape.getShapeType() == BroadphaseNativeType.SPHERE_SHAPE_PROXYTYPE)
                        return "sphere/" + ((SphereShape) shape).getRadius();
                else if (shape.getShapeType() == BroadphaseNativeType.BOX_SHAPE_PROXYTYPE) {
                        Vector3f halfExtends = ((BoxShape) shape).getHalfExtentsWithoutMargin(new Vector3f());
                        return "box/" + halfExtends.x + "/" + halfExtends.y + "/" + halfExtends.z;
                } else if (shape.getShapeType() == BroadphaseNativeType.CAPSULE_SHAPE_PROXYTYPE
                                && ((CapsuleShape) shape).getUpAxis() == 0)
                        return "capsulex/" + ((CapsuleShapeX) shape).getRadius() + "/"
                                        + ((CapsuleShapeX) shape).getHalfHeight() * 2.0f;
                else if (shape.getShapeType() == BroadphaseNativeType.CAPSULE_SHAPE_PROXYTYPE
                                && ((CapsuleShape) shape).getUpAxis() == 1)
                        return "capsule/" + ((CapsuleShape) shape).getRadius() + "/"
                                        + ((CapsuleShape) shape).getHalfHeight() * 2.0f;
                else if (shape.getShapeType() == BroadphaseNativeType.CAPSULE_SHAPE_PROXYTYPE
                                && ((CapsuleShape) shape).getUpAxis() == 2)
                        return "capsulez/" + ((CapsuleShapeZ) shape).getRadius() + "/"
                                        + ((CapsuleShapeZ) shape).getHalfHeight() * 2.0f;
                else if (shape.getShapeType() == BroadphaseNativeType.CYLINDER_SHAPE_PROXYTYPE
                                && ((CylinderShape) shape).getUpAxis() == 0) {
                        Vector3f halfExtends = ((CylinderShapeX) shape).getHalfExtentsWithoutMargin(new Vector3f());
                        return "cylinderx/" + halfExtends.x + "/" + halfExtends.y + "/" + halfExtends.z;
                } else if (shape.getShapeType() == BroadphaseNativeType.CYLINDER_SHAPE_PROXYTYPE
                                && ((CylinderShape) shape).getUpAxis() == 1) {
                        Vector3f halfExtends = ((CylinderShape) shape).getHalfExtentsWithoutMargin(new Vector3f());
                        return "cylinder/" + halfExtends.x + "/" + halfExtends.y + "/" + halfExtends.z;
                } else if (shape.getShapeType() == BroadphaseNativeType.CYLINDER_SHAPE_PROXYTYPE
                                && ((CylinderShape) shape).getUpAxis() == 2) {
                        Vector3f halfExtends = ((CylinderShapeZ) shape).getHalfExtentsWithoutMargin(new Vector3f());
                        return "cylinderz/" + halfExtends.x + "/" + halfExtends.y + "/" + halfExtends.z;
                } else if (shape.getShapeType() == BroadphaseNativeType.CONE_SHAPE_PROXYTYPE
                                && ((ConeShape) shape).getConeUpIndex() == 0)
                        return "conex/" + ((ConeShapeX) shape).getRadius() + "/" + ((ConeShapeX) shape).getHeight();
                else if (shape.getShapeType() == BroadphaseNativeType.CONE_SHAPE_PROXYTYPE
                                && ((ConeShape) shape).getConeUpIndex() == 1)
                        return "cone/" + ((ConeShape) shape).getRadius() + "/" + ((ConeShape) shape).getHeight();
                else if (shape.getShapeType() == BroadphaseNativeType.CONE_SHAPE_PROXYTYPE
                                && ((ConeShape) shape).getConeUpIndex() == 2)
                        return "conez/" + ((ConeShapeZ) shape).getRadius() + "/" + ((ConeShapeZ) shape).getHeight();
                else
                        return "";
        }
}
