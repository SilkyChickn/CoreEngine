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
import de.coreengine.asset.meta.MetaMaterial;
import de.coreengine.asset.meta.MetaModel;
import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.model.Model;
import de.coreengine.rendering.model.SimpleModel;
import de.coreengine.util.Logger;
import de.coreengine.util.Toolbox;
import de.coreengine.util.bullet.Physics;
import de.coreengine.util.gl.IndexBuffer;
import de.coreengine.util.gl.VertexArrayObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**Class that can load a obj file and its mtl file, if exist
 *
 * @author Darius Dinger
 */
public class ObjLoader {
    
    /**Loading a obj file into a simple model. Just the first object in the obj file
     * will be loaded. The other discarted.<br>
     * 
     * @param file Obj file name to load
     * @param shape Collision shape of the model
     * @param asResource Load model from resources
     * @return Loaded simple model
     */
    public static SimpleModel loadSimpleModel(String file, CollisionShape shape, 
            boolean asResource){
        Model model = loadModel(file, shape, asResource, null);
        
        //Check if enough models exist
        assert model != null;
        if(model.getMeshCount() < 1){
            Logger.warn("Error by loading simple model", 
                    "The obj file doesnt contain any model or error by loading!");
            return null;
        }
        
        //Returning first model of obj file
        return new SimpleModel(model.getVao(), model.getIndexBufferAt(0), 
                model.getMaterialAt(0), model.getShape());
    }
    
    /**Loading obj file and mtl file, if exist.<br>
     * 
     * @param file File to load relative to application
     * @param shape Collision shape of the model
     * @param asResource Load model from resources
     * @param meta Metamodel to store meta data in or null to dont store this data
     * @return Loaded model
     */
    public static Model loadModel(String file, CollisionShape shape, 
            boolean asResource, MetaModel meta){
        
        try {
            String[] objFile;
            if(asResource)objFile = FileLoader.getResource(file, false);
            else objFile = FileLoader.readFile(file, false);
            
            String[] mtlFile = new String[0];
            
            //Get path of file
            int lastSlash = file.lastIndexOf("/") +1;
            String path;
            if(lastSlash == 0) path = "";
            else path = file.substring(0, lastSlash);
            
            //Check if mtl file exists
            String mtlFilePath = null;
            for (String objFile1 : objFile) {
                if (objFile1.startsWith("mtllib ")) {
                    mtlFilePath = path + objFile1.split(" ")[1];
                }
            }
            
            //If mtl file exist, load it
            if(mtlFilePath != null){
                if(asResource) mtlFile = FileLoader.getResource(mtlFilePath, false);
                else mtlFile = FileLoader.readFile(mtlFilePath, false);
            }
            
            return loadModel(objFile, mtlFile, path, shape, asResource, meta);
        } catch (IOException ex) {
            Logger.warn("Error by loading model", "The modelfile '" + file + "' "
                    + "could not be loaded. Returning null!");
            return null;
        }
    }
    
    /**Construct model from obj formated string arrays with vertex data<br>
     * <br>
     * Vertex data format:<br>
     * usemtl   Next faces material<br>
     * v        Vertex position<br>
     * vt       Vertex texture coordinates<br>
     * vn       Vertex normal<br>
     * f        Indices of a face<br>
     * 
     * @param objFile String array with vertex data
     * @param materialFile String array with material data or null for default materials
     * @param texLoc Location of the material textures
     * @param shape Collision shape of the model
     * @param asResource Loading material textures from resources
     * @param meta Metamodel to store meta data in or null to dont store this data
     * @return Generated model
     */
    public static Model loadModel(String[] objFile, String[] materialFile, 
            String texLoc, CollisionShape shape, boolean asResource, MetaModel meta){
        
        //prepare meta data
        HashMap<String, MetaMaterial> metaMaterials = 
                meta == null ? null : new HashMap<>();
        MetaMaterial[] metaMatArr = meta == null ? null : new MetaMaterial[0];
        
        //Loading mtls
        HashMap<String, Material> mtlFileMaterials = 
                MtlLoader.loadMaterials(materialFile, texLoc, asResource, metaMaterials);
        
        //Raw modeldata from file
        List<Float> vertices = new LinkedList<>();
        List<Float> texCoords = new LinkedList<>();
        List<Float> normals = new LinkedList<>();
        Material[] materials = new Material[0];
        List<List<String>> objects = new LinkedList<>();
        
        //Read raw modeldata
        for(String line: objFile){
            String[] args = line.split(" ");
            
            switch (args[0]) {
                case "v":
                    vertices.add(Float.parseFloat(args[1]));
                    vertices.add(Float.parseFloat(args[2]));
                    vertices.add(Float.parseFloat(args[3]));
                    break;
                case "vt":
                    texCoords.add(Float.parseFloat(args[1]));
                    texCoords.add(1.0f -Float.parseFloat(args[2]));
                    break;
                case "vn":
                    normals.add(Float.parseFloat(args[1]));
                    normals.add(Float.parseFloat(args[2]));
                    normals.add(Float.parseFloat(args[3]));
                    break;
                case "usemtl":
                    materials = Toolbox.addElement(materials, 
                            mtlFileMaterials.get(args[1]));
                    if(metaMatArr != null) metaMatArr = 
                            Toolbox.addElement(metaMatArr, metaMaterials.get(args[1]));
                    objects.add(new LinkedList<>());
                    break;
                case "f":
                    objects.get(objects.size() -1).add(line);
                    break;
            }
        }
        
        return convertToModel(vertices, texCoords, normals, objects, 
                materials, shape, meta, metaMatArr);
    }
    
    /**Converting raw vertex data into model and calculating tangents.<br>
     * 
     * @param verticesRaw Raw vertices
     * @param texCoordsRaw Raw texture coordinates
     * @param normalsRaw Raw normals
     * @param objects Raw objects of the model with faces
     * @param materials Raw materials
     * @param shape Collision shape of the model
     * @param meta Metamodel to store meta data in or null to dont store this data
     * @param metaMaterials Meta material data
     * @return Converted model
     */
    private static Model convertToModel(List<Float> verticesRaw, List<Float> texCoordsRaw,
                                        List<Float> normalsRaw, List<List<String>> objects, Material[] materials,
                                        CollisionShape shape, MetaModel meta, MetaMaterial[] metaMaterials){
        
        //Preparing containers
        VertexArrayObject vao       = new VertexArrayObject();
        IndexBuffer[] indexBuffers  = new IndexBuffer[0];
        
        List<Float> verticesList = new LinkedList<>(), 
                texCoordsList = new LinkedList<>(), 
                normalsList = new LinkedList<>(), 
                tangentsList = new LinkedList<>();
        
        int[][] indices = new int[objects.size()][];
        
        //Iterate through objects
        int counter = 0, objectCounter = 0;
        for(List<String> faces: objects){
            
            int indicesCounter = 0;
            indices[objectCounter] = new int[faces.size() * 3];
            
            //Iterate through objects faces
            for(String face: faces){
                String[] args = face.split(" ");
                
                //Data for raw indices for the vertices
                String[] v0Args = args[1].split("/");
                String[] v1Args = args[2].split("/");
                String[] v2Args = args[3].split("/");
                
                int[] v0 = new int[3], v1 = new int[3], v2 = new int[3];
                
                //Get raw indices
                v0[0] = Integer.parseInt(v0Args[0]);
                v0[1] = Integer.parseInt(v0Args[1]);
                v0[2] = Integer.parseInt(v0Args[2]);
                
                v1[0] = Integer.parseInt(v1Args[0]);
                v1[1] = Integer.parseInt(v1Args[1]);
                v1[2] = Integer.parseInt(v1Args[2]);
                
                v2[0] = Integer.parseInt(v2Args[0]);
                v2[1] = Integer.parseInt(v2Args[1]);
                v2[2] = Integer.parseInt(v2Args[2]);
                
                //Convert raw indices
                indices[objectCounter][indicesCounter++] = counter * 3;
                indices[objectCounter][indicesCounter++] = counter * 3 +1;
                indices[objectCounter][indicesCounter++] = counter * 3 +2;
                
                //Convert raw vertices
                verticesList.add(verticesRaw.get((v0[0] -1) * 3));
                verticesList.add(verticesRaw.get((v0[0] -1) * 3 +1));
                verticesList.add(verticesRaw.get((v0[0] -1) * 3 +2));
                
                verticesList.add(verticesRaw.get((v1[0] -1) * 3));
                verticesList.add(verticesRaw.get((v1[0] -1) * 3 +1));
                verticesList.add(verticesRaw.get((v1[0] -1) * 3 +2));
                
                verticesList.add(verticesRaw.get((v2[0] -1) * 3));
                verticesList.add(verticesRaw.get((v2[0] -1) * 3 +1));
                verticesList.add(verticesRaw.get((v2[0] -1) * 3 +2));
                
                //Convert raw tex coords
                texCoordsList.add(texCoordsRaw.get((v0[1] -1) * 2));
                texCoordsList.add(texCoordsRaw.get((v0[1] -1) * 2 +1));
                
                texCoordsList.add(texCoordsRaw.get((v1[1] -1) * 2));
                texCoordsList.add(texCoordsRaw.get((v1[1] -1) * 2 +1));
                
                texCoordsList.add(texCoordsRaw.get((v2[1] -1) * 2));
                texCoordsList.add(texCoordsRaw.get((v2[1] -1) * 2 +1));
                
                //Convert raw normals
                normalsList.add(normalsRaw.get((v0[2] -1) * 3));
                normalsList.add(normalsRaw.get((v0[2] -1) * 3 +1));
                normalsList.add(normalsRaw.get((v0[2] -1) * 3 +2));
                
                normalsList.add(normalsRaw.get((v1[2] -1) * 3));
                normalsList.add(normalsRaw.get((v1[2] -1) * 3 +1));
                normalsList.add(normalsRaw.get((v1[2] -1) * 3 +2));
                
                normalsList.add(normalsRaw.get((v2[2] -1) * 3));
                normalsList.add(normalsRaw.get((v2[2] -1) * 3 +1));
                normalsList.add(normalsRaw.get((v2[2] -1) * 3 +2));
                
                //Calculate tangent for vertices
                float[] tangent = calcTangent(
                        verticesRaw.get((v0[0] -1) * 3), 
                        verticesRaw.get((v0[0] -1) * 3 +1), 
                        verticesRaw.get((v0[0] -1) * 3 +2), 
                        verticesRaw.get((v1[0] -1) * 3), 
                        verticesRaw.get((v1[0] -1) * 3 +1), 
                        verticesRaw.get((v1[0] -1) * 3 +2), 
                        verticesRaw.get((v2[0] -1) * 3), 
                        verticesRaw.get((v2[0] -1) * 3 +1), 
                        verticesRaw.get((v2[0] -1) * 3 +2), 
                        texCoordsRaw.get((v0[1] -1) * 2), 
                        texCoordsRaw.get((v0[1] -1) * 2 +1), 
                        texCoordsRaw.get((v1[1] -1) * 2), 
                        texCoordsRaw.get((v1[1] -1) * 2 +1), 
                        texCoordsRaw.get((v2[1] -1) * 2), 
                        texCoordsRaw.get((v2[1] -1) * 2 +1)
                );
                
                //Adding tangets to list
                tangentsList.add(tangent[0]);
                tangentsList.add(tangent[1]);
                tangentsList.add(tangent[2]);
                
                tangentsList.add(tangent[0]);
                tangentsList.add(tangent[1]);
                tangentsList.add(tangent[2]);
                
                tangentsList.add(tangent[0]);
                tangentsList.add(tangent[1]);
                tangentsList.add(tangent[2]);
                
                counter++;
            }
            
            //Adding index buffer/indices to vao
            indexBuffers = Toolbox.addElement(indexBuffers, 
                    vao.addIndexBuffer(indices[objectCounter++]));
        }
        
        //Convert lists to arrays
        float[] vertices    = Toolbox.toArrayf(verticesList);
        float[] texCoords   = Toolbox.toArrayf(texCoordsList);
        float[] normals     = Toolbox.toArrayf(normalsList);
        float[] tangents    = Toolbox.toArrayf(tangentsList);
        
        //Create collision shape
        if(shape instanceof ConvexHullShape) 
            shape = Physics.createConvexHullShape(vertices);
        else if(shape instanceof TriangleMeshShape) 
            shape = Physics.createTriangleMeshShape(vertices, indices);
        
        //Adding data to vao
        vao.addVertexBuffer(vertices, 3, 0);
        vao.addVertexBuffer(texCoords, 2, 1);
        vao.addVertexBuffer(normals, 3, 2);
        vao.addVertexBuffer(tangents, 3, 3);
        
        //Fill up meta model
        if(meta != null){
            meta.setVertices(vertices);
            meta.setTexCoords(texCoords);
            meta.setNormals(normals);
            meta.setTangents(tangents);
            meta.setIndices(indices);
            meta.setShape(shape);
            meta.setMaterials(metaMaterials);
        }
        
        return new Model(vao, indexBuffers, materials, shape);
    }
    
    /**Calculating tangent for a face
     * 
     * @param v0x X coordinate of vertex 0
     * @param v0y Y coordinate of vertex 0
     * @param v0z Z coordinate of vertex 0
     * @param v1x X coordinate of vertex 1
     * @param v1y Y coordinate of vertex 1
     * @param v1z Z coordinate of vertex 1
     * @param v2x X coordinate of vertex 2
     * @param v2y Y coordinate of vertex 2
     * @param v2z Z coordinate of vertex 2
     * @param v0u U coordinate of vertex 0
     * @param v0v V coordinate of vertex 0
     * @param v1u U coordinate of vertex 1
     * @param v1v V coordinate of vertex 1
     * @param v2u U coordinate of vertex 2
     * @param v2v V coordinate of vertex 2
     * @return Tangent as float array [x, y, z]
     */
    private static float[] calcTangent(float v0x, float v0y, float v0z, float v1x, 
            float v1y, float v1z, float v2x, float v2y, float v2z, float v0u, 
            float v0v, float v1u, float v1v, float v2u, float v2v){
        
        float[] tangent = new float[3];
        
        float dp1x = v1x -v0x; float dp1y = v1y -v0y; float dp1z = v1z -v0z;
        float dp2x = v2x -v0x; float dp2y = v2y -v0y; float dp2z = v2z -v0z;
        
        float duv1u = v1u -v0u; float duv1v = v1v -v0v;
        float duv2u = v2u -v0u; float duv2v = v2v -v0v;
        
        float r = 1.0f / (duv1u * duv2v - duv1v * duv2u);
        dp1x *= duv2v; dp1y *= duv2v; dp1z *= duv2v;
        dp2x *= duv1v; dp2y *= duv1v; dp2z *= duv1v;
        
        tangent[0] = dp1x -dp2x; tangent[1] = dp1y -dp2y; tangent[2] = dp1z -dp2z;
        tangent[0] *= r; tangent[1] *= r; tangent[2] *= r;
        
        return tangent;
    }
}
