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
package io.github.suuirad.coreengine.asset;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.TriangleMeshShape;
import io.github.suuirad.coreengine.asset.meta.MetaModel;
import io.github.suuirad.coreengine.rendering.model.Material;
import io.github.suuirad.coreengine.rendering.model.Model;
import io.github.suuirad.coreengine.util.Logger;
import io.github.suuirad.coreengine.util.MaterialParser;
import io.github.suuirad.coreengine.util.bullet.CollisionShapeParser;
import io.github.suuirad.coreengine.util.bullet.Physics;
import io.github.suuirad.coreengine.util.gl.IndexBuffer;
import io.github.suuirad.coreengine.util.gl.VertexArrayObject;

import java.io.IOException;

/**Load that can load and save MDL Files.<br>
 * Loading into models and saving meta models.
 *
 * @author Darius Dinger
 */
public class MdlLoader {
    
    /**Loading a model for a mdl file
     * 
     * @param file File to load
     * @param asResource Should file loaded from resources
     * @return Loaded model file or null if an error occurs
     */
    public static Model loadModel(String file, boolean asResource){
        
        try {
            
            //Read file
            String[] lines;
            if(asResource) lines = FileLoader.getResource(file, false);
            else lines = FileLoader.readFile(file, false);
            
            //Prepare meta data
            Material[] materials = null;
            float[] vertices = null, texCoords = null, normals = null, tangents = null;
            int[][] indices = null;
            IndexBuffer[] indexBuffers = null;
            CollisionShape shape = null;
            
            int indexCounter = 0;
            int materialCounter = 0;
            int parts = 0;
            
            //Parse lines
            for(String line: lines){
                if(line.startsWith("vertices")){
                    String[] args = line.split(" ");
                    vertices = new float[args.length -1];
                    for(int i = 0; i < vertices.length; i++) 
                        vertices[i] = Float.parseFloat(args[i +1]);
                }else if(line.startsWith("texCoords")){
                    String[] args = line.split(" ");
                    texCoords = new float[args.length -1];
                    for(int i = 0; i < texCoords.length; i++) 
                        texCoords[i] = Float.parseFloat(args[i +1]);
                }else if(line.startsWith("normals")){
                    String[] args = line.split(" ");
                    normals = new float[args.length -1];
                    for(int i = 0; i < normals.length; i++) 
                        normals[i] = Float.parseFloat(args[i +1]);
                }else if(line.startsWith("tangents")){
                    String[] args = line.split(" ");
                    tangents = new float[args.length -1];
                    for(int i = 0; i < tangents.length; i++) 
                        tangents[i] = Float.parseFloat(args[i +1]);
                }else if(line.startsWith("shape ")){
                    shape = CollisionShapeParser.toShape(line.replaceFirst("shape ", ""));
                }else if(line.startsWith("indexBuffer")){
                    String[] args = line.split(" ");
                    assert indices != null;
                    indices[indexCounter] = new int[args.length -1];
                    for(int i = 0; i < indices[indexCounter].length; i++) 
                        indices[indexCounter][i] = Integer.parseInt(args[i +1]);
                    indexCounter++;
                }else if(line.startsWith("material ")){
                    assert materials != null;
                    materials[materialCounter++] =
                            MaterialParser.toMaterial(line.replaceFirst("material ", ""), asResource);
                }else if(line.startsWith("parts")){
                    String[] args = line.split(" ");
                    parts = Integer.parseInt(args[1]);
                    
                    materials = new Material[parts];
                    indices = new int[parts][];
                    indexBuffers = new IndexBuffer[parts];
                }
            }
            
            //Create collision shape
            if (shape instanceof ConvexHullShape) {
                assert vertices != null;
                shape = Physics.createConvexHullShape(vertices);
            } else if (shape instanceof TriangleMeshShape) {
                assert indices != null;
                shape = Physics.createTriangleMeshShape(vertices, indices);
            }
            
            //Adding data to vao
            VertexArrayObject vao = new VertexArrayObject();
            vao.addVertexBuffer(vertices, 3, 0);
            vao.addVertexBuffer(texCoords, 2, 1);
            vao.addVertexBuffer(normals, 3, 2);
            vao.addVertexBuffer(tangents, 3, 3);
            
            //Create index buffers
            for(int i = 0; i < parts; i++){
                indexBuffers[i] = vao.addIndexBuffer(indices[i]);
            }
            
            return new Model(vao, indexBuffers, materials, shape);
        } catch (IOException ex) {
            Logger.warn("Error by loading model", "The modelfile '" + file + "' "
                    + "could not be loaded. Returning null!");
            return null;
        }
    }
    
    /**Saving meta model into a mdl file
     * 
     * @param file File to save in
     * @param model MetaModel to save
     */
    public static void saveModel(String file, MetaModel model){
        
        try {
            int partCount = model.getIndices().length;
            
            //Prepare model data strings
            String parts = "parts " + partCount;
            String shape = "shape " + CollisionShapeParser.toString(model.getShape());

            StringBuilder vertices = new StringBuilder("vertices");
            for (float vertex : model.getVertices()) vertices.append(" ").append(vertex);

            StringBuilder texCoords = new StringBuilder("texCoords");
            for (float texCoord : model.getTexCoords()) texCoords.append(" ").append(texCoord);

            StringBuilder normals = new StringBuilder("normals");
            for (float normal : model.getNormals()) normals.append(" ").append(normal);

            StringBuilder tangents = new StringBuilder("tangents");
            for (float tangent : model.getTangents()) tangents.append(" ").append(tangent);
            
            //Prepare parts data strings
            String[] indexBuffers = new String[partCount];
            for(int i = 0; i < partCount; i++){
                StringBuilder indexBuffer = new StringBuilder("indexBuffer");
                for (int index : model.getIndices()[i]) indexBuffer.append(" ").append(index);
                indexBuffers[i] = indexBuffer.toString();
            }
            
            String[] materials = new String[partCount];
            for(int i = 0; i < partCount; i++)
                materials[i] = "material " + 
                        MaterialParser.toString(model.getMaterials()[i]);
            
            //Combine data strings
            String[] data = new String[6 +(partCount * 2)];
            data[0] = parts;
            data[1] = vertices.toString();
            data[2] = texCoords.toString();
            data[3] = normals.toString();
            data[4] = tangents.toString();
            data[5] = shape;
            for(int i = 0; i < partCount; i++){
                data[6 +(i * 2)] = materials[i];
                data[7 +(i * 2)] = indexBuffers[i];
            }
            
            //Write data strings into file
            FileLoader.writeFile(file, data);
        } catch (IOException ex) {
            Logger.warn("Error by saving model", "The modelfile '" + file + "' "
                    + "could not be saved!");
        }
    }
}
