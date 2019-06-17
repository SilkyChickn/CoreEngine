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
package de.coreengine.rendering.renderer;

import de.coreengine.rendering.model.singletons.TerrainMesh;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderable.terrain.Terrain;
import de.coreengine.rendering.renderable.terrain.TerrainNode;
import de.coreengine.util.gl.IndexBuffer;
import de.coreengine.rendering.programs.TerrainShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL40;

import javax.vecmath.Vector4f;
import java.util.List;

/**Class that can render a terrain into a gBuffer
 *
 * @author Darius Dinger
 */
public class TerrainRenderer {
    
    private final TerrainShader shader = new TerrainShader();
    
    /**Rendering a terrain using a TerrainShader
     * 
     * @param terrains Terrains to render
     * @param camera Camera to render from
     * @param clipPlane Clipplane to clip terrain
     */
    void render(List<Terrain> terrains, Camera camera, Vector4f clipPlane){
        
        TerrainMesh mesh = TerrainMesh.getInstance();
        
        //Prepare shader
        shader.start();
        shader.setViewProjectionMatrix(camera.getViewProjectionMatrix());
        shader.setCameraPosition(camera.getPosition());
        shader.setClipPlane(clipPlane.x, clipPlane.y, clipPlane.z, clipPlane.w);
        
        //Bind terrain mesh data and index buffer
        mesh.getVao().bind();
        mesh.getVao().enableAttributes();
        
        terrains.forEach(terrain -> {
            
            //Prepare shader for next terrain
            shader.setTerrainTransform(terrain.getTransMat());
            shader.setTerrainConfig(terrain.getConfig());
            
            //Render terrain nodes
            renderNode(terrain.getTerrainQuadtree(), mesh);
        });
        
        //Stop shader and unbind terrain mesh data and index buffer
        mesh.getVao().disableAttributes();
        mesh.getVao().unbind();
        shader.stop();
    }
    
    /**Rendering a node from the terrain quad tree
     * 
     * @param node Node to render
     * @param mesh Mesh to use for render
     */
    private void renderNode(TerrainNode node, TerrainMesh mesh){
        
        //Render node if its a leaf
        if(node.isLeaf()){
            
            //Get greater or euqal neightbor nodes
            TerrainNode neighbourLeft = node.getNeighboursGeLeft();
            TerrainNode neighbourRight = node.getNeighboursGeRight();
            TerrainNode neighbourTop = node.getNeighboursGeTop();
            TerrainNode neighbourBottom = node.getNeighboursGeBottom();
            
            //Check if neighbours lod levels are greater than the own
            boolean morphLeft = (neighbourLeft != null && 
                    neighbourLeft.getLod() < node.getLod());
            boolean morphRight = (neighbourRight != null && 
                    neighbourRight.getLod() < node.getLod());
            boolean morphBottom = (neighbourBottom != null && 
                    neighbourBottom.getLod() < node.getLod());
            boolean morphTop = (neighbourTop != null && 
                    neighbourTop.getLod() < node.getLod());
            
            //Select and bind selected index buffer
            IndexBuffer index = getMorphingBuffer(morphLeft, morphRight,
                    morphTop, morphBottom, mesh);
            index.bind();
            
            //Prepare shader/loading offset, size
            shader.setChunkData(node.getPosition(), node.getSize());
            
            //Render node
            GL11.glDrawElements(GL40.GL_PATCHES, index.getSize(), GL11.GL_UNSIGNED_INT, 0);
            
            //Unbind index buffer and return
            index.unbind();
            return;
        }
        
        //Render all childs of this node, if node isnt a leaf
        for(TerrainNode child: node.getChilds()){
            renderNode(child, mesh);
        }
    }
    
    /**Checks wich index buffer must be used to get the right morphing
     * 
     * @param morphLeft Should mesh be morphed left
     * @param morphRight Should mesh be morphed right
     * @param morphTop Should mesh be morphed top
     * @param morphBottom Should mesh be morphed bottom
     * @param mesh Terrain mesh to use the index buffer from
     * @return Correct morphed index buffer
     */
    private IndexBuffer getMorphingBuffer(boolean morphLeft, boolean morphRight, 
            boolean morphTop, boolean morphBottom, TerrainMesh mesh){
        
        if(morphLeft && !morphRight && !morphTop && !morphBottom) 
            return mesh.getMorphL();
        else if(morphLeft && !morphRight && morphTop && !morphBottom) 
            return mesh.getMorphTL();
        else if (morphLeft && !morphRight && !morphTop && morphBottom)
            return mesh.getMorphBL();
        else if(!morphLeft && morphRight && !morphTop && !morphBottom) 
            return mesh.getMorphR();
        else if(!morphLeft && morphRight && morphTop && !morphBottom) 
            return mesh.getMorphTR();
        else if(!morphLeft && morphRight && !morphTop && morphBottom) 
            return mesh.getMorphBR();
        else if(!morphLeft && !morphRight && morphTop && !morphBottom) 
            return mesh.getMorphB();
        else if(!morphLeft && !morphRight && !morphTop && morphBottom) 
            return mesh.getMorphT();
        else return mesh.getFullMesh();
    }
}
