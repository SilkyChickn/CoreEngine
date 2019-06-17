package demo;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.linearmath.Transform;
import io.github.suuirad.coreengine.asset.meta.MetaMaterial;
import io.github.suuirad.coreengine.rendering.model.Material;
import io.github.suuirad.coreengine.system.Game;
import io.github.suuirad.coreengine.util.MaterialParser;
import io.github.suuirad.coreengine.util.Toolbox;
import io.github.suuirad.coreengine.util.bullet.CollisionShapeParser;

import javax.vecmath.Vector3f;

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
public class Test {
    
    public static void main(String[] args){
        
        Game.init(800, 600, "", false);
        
        MetaMaterial test = new MetaMaterial();
        test.tiling = 10.0f;
        test.shineDamping = 1.0f;
        test.reflectivity = 5.0f;
        test.displacementFactor = 2.1f;
        
        String str = MaterialParser.toString(test);
        System.out.println(str + "\n");
        
        Material test2 = MaterialParser.toMaterial(str, false);
        System.out.println("DF:" + test2.displacementFactor);
        System.out.println("T:" + test2.tiling);
        System.out.println("R:" + test2.reflectivity);
        System.out.println("SD:" + test2.shineDamping);
        
        //Game.exit(0);
        
        float[] mat = new float[16];
        Transform t = new Transform();
        //t.setIdentity();
        t.getOpenGLMatrix(mat);
        System.out.println(Toolbox.arrayToString(mat, "-"));
        
        //Game.exit(0);
        
        CompoundShape shape = new CompoundShape();
        shape.addChildShape(new Transform(), new SphereShape(5.0f));
        shape.addChildShape(new Transform(), new ConeShapeX(5.0f, 2.5f));
        shape.addChildShape(new Transform(), new CylinderShapeZ(
                new Vector3f(2.0f, 2.0f, 6.0f)));
        shape.addChildShape(new Transform(), new BoxShape(
                new Vector3f(6.0f, 10.0f, 10.0f)));
        shape.addChildShape(new Transform(), new CapsuleShape(50.0f, 10.0f));
        
        String shapeString = CollisionShapeParser.toString(shape);
        System.out.println(shapeString);
        
        CollisionShape s = CollisionShapeParser.toShape(shapeString);
        System.out.println(((CompoundShape) s).getChildList().size());
        
        Game.exit(0);
    }
    
}
