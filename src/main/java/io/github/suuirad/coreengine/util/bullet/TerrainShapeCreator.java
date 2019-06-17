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
package io.github.suuirad.coreengine.util.bullet;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import io.github.suuirad.coreengine.rendering.renderable.terrain.Terrain;

import javax.vecmath.Vector3f;
import java.nio.ByteBuffer;

/**Class that can create a terrain collision shape
 *
 * @author Darius Dinger
 */
public class TerrainShapeCreator {
    
    /**Creating a static collision shape for a terrain with a specific resolution.
     * 
     * @param terr Terrain to create the shape for
     * @param numQuads Quads of the shape per row/column
     * @return Created static terrain shape
     */
    public static BvhTriangleMeshShape createTerrainShape(Terrain terr, 
            int numQuads){
        
        int numVertices = numQuads +1;
        
        int[] indices = new int[numQuads * numQuads * 6];
        float[] vertices = new float[numVertices * numVertices * 3];
        
        float triSize = 1.0f / numQuads; //Size of one triangle
        
        ByteBuffer heights = terr.getConfig().getHeightMap().getData();
        int width = terr.getConfig().getHeightMap().getWidth();
        int height = terr.getConfig().getHeightMap().getHeight();
        float amplitude = terr.getConfig().getAmplitude();
        
        //Generate vertices
        int c = 0;
        for(int z = 0; z < numVertices; z++){
            for(int x = 0; x < numVertices; x++){
                
                float posX = x * triSize;
                float posZ = z * triSize;
                float posY = getHeightAt(posX, posZ, heights, width, height, amplitude);
                
                vertices[c++] = posX;
                vertices[c++] = posY;
                vertices[c++] = posZ;
            }
        }
        
        //Connect vertices and gen indices
        c = 0;
        for(int x = 0; x < numQuads; x++){
            for(int z = 0; z < numQuads; z++){
                
                //Get quad vertex ids
                int v0 = x +z * numVertices;
                int v1 = v0 +1;
                int v2 = v1 +numVertices;
                int v3 = v0 +numVertices;
                
                indices[c++] = v0;
                indices[c++] = v1;
                indices[c++] = v2;
                
                indices[c++] = v2;
                indices[c++] = v3;
                indices[c++] = v0;
            }
        }
        
        BvhTriangleMeshShape shape = Physics.createTriangleMeshShape(vertices, new int[][] {indices});
        shape.setLocalScaling(new Vector3f(terr.getScale(), terr.getScale(), terr.getScale()));
        
        return shape;
    }
    
    /**@param x X texture coordinate of the heightmap
     * @param y Y texture coordinate of the heightmap
     * @param heights Bytebuffer of the terrain heightmap texture
     * @param width Width of the terrain height map texture in pixels
     * @param height Height of the terrain height map texture in pixels
     * @param amplitude Amplitude of the terrain
     * @return Height of a specific point on the terrain 
     */
    private static float getHeightAt(float x, float y, ByteBuffer heights, 
            int width, int height, float amplitude){
        
        int pixX = Integer.min((int) (x * width), width -1);
        int pixY = Integer.min((int) (y * height), height -1);
        
        int pixelIndex = (pixX +pixY * width) * 4;
        
        float h = (float) (heights.get(pixelIndex) & 0xFF) / 255.0f;
        h *= amplitude;
        
        return h;
    }
}
