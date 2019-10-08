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
package de.coreengine.rendering.model.singletons;

import com.bulletphysics.collision.shapes.BU_Simplex1to4;
import de.coreengine.rendering.model.Mesh;
import de.coreengine.util.gl.IndexBuffer;
import de.coreengine.util.gl.VertexArrayObject;

import javax.vecmath.Vector3f;

/**Representing a simple 2 dimensional quad model singleton
 *
 * @author Darius Dinger
 */
public class Quad2D {
    
    //Instance of a 2D quad model
    private static Mesh instance = null;
    
    /**@return Instance of a 2D quad model
     */
    public static Mesh getInstance() {
        if(instance == null) create();
        return instance;
    }
    
    /**Creating instance
     */
    private static void create(){
        VertexArrayObject vao = new VertexArrayObject();
        vao.addVertexBuffer(new float[]{
            -1, -1, 1, -1, 1, 1, -1, 1
        }, 2, 0);
        
        IndexBuffer index = vao.addIndexBuffer(new int[]{
            3, 0, 2, 2, 0, 1
        });
        
        instance = new Mesh(vao, index, new BU_Simplex1to4(
                new Vector3f(-1, -1, 0), new Vector3f(1, -1, 0), 
                new Vector3f(1, 1, 0), new Vector3f(-1, 1, 0)
        ));
    }
}
