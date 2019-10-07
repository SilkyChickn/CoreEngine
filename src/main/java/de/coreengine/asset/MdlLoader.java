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
package de.coreengine.asset;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.TriangleMeshShape;
import de.coreengine.asset.meta.MetaModel;
import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.model.Mesh;
import de.coreengine.rendering.model.Model;
import de.coreengine.util.Logger;
import de.coreengine.util.MaterialParser;
import de.coreengine.util.bullet.CollisionShapeParser;
import de.coreengine.util.bullet.Physics;
import de.coreengine.util.gl.IndexBuffer;
import de.coreengine.util.gl.VertexArrayObject;

import java.io.IOException;

/**Load that can load and save MDL Files.<br>
 * Loading into models and saving meta models.
 *
 * @author Darius Dinger
 */
public class MdlLoader {
    
    /**Loading a model for a mdl file and storing into asset database
     * 
     * @param file File to load
     * @param asResource Should file loaded from resources
     */
    public static void loadModel(String file, boolean asResource){
        if(AssetDatabase.models.containsKey(file)) return;

        try {
            
            //Read file
            String[] lines;
            if(asResource) lines = FileLoader.getResource(file, false);
            else lines = FileLoader.readFile(file, false);
            
            //Prepare meta data
            Mesh[] meshes  = null;
            Material[] materials = null;
            float[][] vertices = null, texCoords = null, normals = null, tangents = null;
            int[][] indices = null;
            CollisionShape[] shapes = null;
            
            int meshCounter = -1;
            int parts = 0;
            
            //Parse lines
            for(String line: lines){
                if(line.startsWith("newMesh")){
                    meshCounter++;
                }else if(line.startsWith("vertices")){
                    String[] args = line.split(" ");
                    vertices[meshCounter] = new float[args.length -1];
                    for(int i = 0; i < vertices[meshCounter].length; i++)
                        vertices[meshCounter][i] = Float.parseFloat(args[i +1]);
                }else if(line.startsWith("texCoords")){
                    String[] args = line.split(" ");
                    texCoords[meshCounter] = new float[args.length -1];
                    for(int i = 0; i < texCoords[meshCounter].length; i++)
                        texCoords[meshCounter][i] = Float.parseFloat(args[i +1]);
                }else if(line.startsWith("normals")){
                    String[] args = line.split(" ");
                    normals[meshCounter] = new float[args.length -1];
                    for(int i = 0; i < normals[meshCounter].length; i++)
                        normals[meshCounter][i] = Float.parseFloat(args[i +1]);
                }else if(line.startsWith("tangents")){
                    String[] args = line.split(" ");
                    tangents[meshCounter] = new float[args.length -1];
                    for(int i = 0; i < tangents[meshCounter].length; i++)
                        tangents[meshCounter][i] = Float.parseFloat(args[i +1]);
                }else if(line.startsWith("shape ")){
                    shapes[meshCounter] =
                            CollisionShapeParser.toShape(line.replaceFirst("shape ", ""));
                }else if(line.startsWith("indexBuffer")){
                    String[] args = line.split(" ");
                    indices[meshCounter] = new int[args.length -1];
                    for(int i = 0; i < indices[meshCounter].length; i++)
                        indices[meshCounter][i] = Integer.parseInt(args[i +1]);
                }else if(line.startsWith("material ")){
                    materials[meshCounter] =
                            MaterialParser.toMaterial(line.replaceFirst("material ", ""), asResource);
                }else if(line.startsWith("parts")){
                    String[] args = line.split(" ");
                    parts = Integer.parseInt(args[1]);

                    //Reserve data storage
                    meshes = new Mesh[parts];
                    vertices = new float[parts][];
                    texCoords = new float[parts][];
                    normals = new float[parts][];
                    tangents = new float[parts][];
                    indices = new int[parts][];
                    materials = new Material[parts];
                    shapes = new CollisionShape[parts];
                }
            }

            //create model
            for(int i = 0; i < parts; i++) {

                //Create collision shape
                if (shapes[i] instanceof ConvexHullShape) {
                    assert vertices != null;
                    shapes[i] = Physics.createConvexHullShape(vertices[i]);
                } else if (shapes[i] instanceof TriangleMeshShape) {
                    assert indices != null;
                    shapes[i] = Physics.createTriangleMeshShape(vertices[i], indices);
                }

                //Adding data to vao
                VertexArrayObject vao = new VertexArrayObject();
                vao.addVertexBuffer(vertices[i], 3, 0);
                vao.addVertexBuffer(texCoords[i], 2, 1);
                vao.addVertexBuffer(normals[i], 3, 2);
                vao.addVertexBuffer(tangents[i], 3, 3);

                //Create index buffers
                IndexBuffer indexBuffer = vao.addIndexBuffer(indices[i]);

                //Create model
                meshes[i] = new Mesh(vao, indexBuffer, materials[i], shapes[i]);
            }
            
            AssetDatabase.models.put(file, new Model(meshes));
        } catch (IOException ex) {
            Logger.warn("Error by loading model", "The modelfile '" + file + "' "
                    + "could not be loaded. Returning null!");
        }
    }
    
    /**Saving meta model into a mdl file
     * 
     * @param file File to save in
     * @param model MetaModel to save
     */
    public static void saveModel(String file, MetaModel model){
        
        try {
            int partCount = model.meshes.length;
            
            //Prepare model data strings
            String parts = "parts " + partCount;

            //Prepare parts data strings
            String[] vertices = new String[partCount];
            for(int i = 0; i < partCount; i++){
                StringBuilder vertexBuffer = new StringBuilder("vertices");
                for (float vertex : model.meshes[i].vertices) vertexBuffer.append(" ").append(vertex);
                vertices[i] = vertexBuffer.toString();
            }

            //Prepare parts data strings
            String[] texCoords = new String[partCount];
            for(int i = 0; i < partCount; i++){
                StringBuilder texCoordBuffer = new StringBuilder("texCoords");
                for (float texCoord : model.meshes[i].texCoords) texCoordBuffer.append(" ").append(texCoord);
                texCoords[i] = texCoordBuffer.toString();
            }

            //Prepare parts data strings
            String[] normals = new String[partCount];
            for(int i = 0; i < partCount; i++){
                StringBuilder normalBuffer = new StringBuilder("normals");
                for (float normal : model.meshes[i].normals) normalBuffer.append(" ").append(normal);
                normals[i] = normalBuffer.toString();
            }

            //Prepare parts data strings
            String[] tangents = new String[partCount];
            for(int i = 0; i < partCount; i++){
                StringBuilder tangentBuffer = new StringBuilder("tangents");
                for (float tangent : model.meshes[i].tangents) tangentBuffer.append(" ").append(tangent);
                tangents[i] = tangentBuffer.toString();
            }

            boolean animated = true;

            //Prepare parts data strings
            String[] jointIds = new String[partCount];
            for(int i = 0; i < partCount; i++){
                if(model.meshes[i].jointIds == null){
                    animated = false;
                    break;
                }
                StringBuilder jointIdBuffer = new StringBuilder("jointIds");
                for (int jointId : model.meshes[i].jointIds) jointIdBuffer.append(" ").append(jointId);
                jointIds[i] = jointIdBuffer.toString();
            }

            //Prepare parts data strings
            String[] weights = new String[partCount];
            for(int i = 0; i < partCount; i++){
                if(model.meshes[i].weights == null){
                    animated = false;
                    break;
                }
                StringBuilder weightBuffer = new StringBuilder("weights");
                for (float weight : model.meshes[i].weights) weightBuffer.append(" ").append(weight);
                weights[i] = weightBuffer.toString();
            }

            //Prepare parts data strings
            String[] indexBuffers = new String[partCount];
            for(int i = 0; i < partCount; i++){
                StringBuilder indexBuffer = new StringBuilder("indexBuffer");
                for (int index : model.meshes[i].indices) indexBuffer.append(" ").append(index);
                indexBuffers[i] = indexBuffer.toString();
            }

            //Prepare parts data strings
            String[] materials = new String[partCount];
            for(int i = 0; i < partCount; i++)
                materials[i] = "material " + 
                        MaterialParser.toString(model.meshes[i].material);

            //Prepare parts data strings
            String[] shapes = new String[partCount];
            for(int i = 0; i < partCount; i++)
                shapes[i] = "shape " +
                        CollisionShapeParser.toString(model.meshes[i].shape);

            //Combine data strings
            String[] data = new String[partCount * (animated ? 10 : 8) +1];

            //Model data
            data[0] = parts;

            //Mesh data
            int counter = data.length;
            for(int i = 0; i < partCount; i++){
                data[counter++] = "newMesh";
                data[counter++] = vertices[i];
                data[counter++] = texCoords[i];
                data[counter++] = normals[i];
                data[counter++] = tangents[i];
                data[counter++] = shapes[i];
                data[counter++] = materials[i];
                data[counter++] = indexBuffers[i];
                if(animated)data[counter++] = jointIds[i];
                if(animated)data[counter++] = weights[i];
            }
            
            //Write data strings into file
            FileLoader.writeFile(file, data);
        } catch (IOException ex) {
            Logger.warn("Error by saving model", "The modelfile '" + file + "' "
                    + "could not be saved!");
        }
    }
}
