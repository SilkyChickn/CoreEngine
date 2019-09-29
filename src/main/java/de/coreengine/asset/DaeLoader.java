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
import de.coreengine.rendering.model.Color;
import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.model.Model;
import de.coreengine.util.Logger;
import de.coreengine.util.Toolbox;
import de.coreengine.util.bullet.Physics;
import de.coreengine.util.gl.IndexBuffer;
import de.coreengine.util.gl.VertexArrayObject;
import javafx.util.Pair;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class DaeLoader {

    /**Loading dae file model and material, if exist.<br>
     *
     * @param file File to load relative to application or resource path
     * @param shape Collision shape of the model
     * @param asResource Load model from resources
     * @param meta Metamodel to store meta data in or null to dont store this data
     * @return Loaded model
     */
    public static Model loadModel(String file, CollisionShape shape, boolean asResource, MetaModel meta){

        try {

            //Get path of file
            int lastSlash = file.lastIndexOf("/") +1;
            String path;
            if(lastSlash == 0) path = "";
            else path = file.substring(0, lastSlash);

            //Create parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            //Parse XML
            Document document;
            if(asResource) document = builder.parse(
                    Objects.requireNonNull(DaeLoader.class.getClassLoader().getResource(file)).getFile()
            );
            else document = builder.parse(new File(file));

            document.getDocumentElement().normalize();

            return loadModel(document, path, shape, asResource, meta);
        } catch (IOException ex) {
            Logger.warn("Error by loading model", "The modelfile '" + file + "' "
                    + "could not be loaded. Returning null!");
            return null;
        } catch (SAXException ex) {
            Logger.warn("Error by parsing dae file", "The dae modelfile '" + file + "' "
                    + "could not be parsed, Returning null!");
            return null;
        } catch (ParserConfigurationException ex) {
            Logger.warn("Error by parsing model", "The modelfile '" + file + "' "
                    + "could not be parsed, because the parser could not be created. Returning null!");
            return null;
        }
    }

    /**Loading dae document and material, if exist.<br>
     *
     * @param document Document of the dae data
     * @param path Path of the images
     * @param shape Collision shape of the model
     * @param asResource Load images from resources
     * @param metaModel Metamodel to store meta data in or null to dont store this data
     * @return Loaded model
     */
    public static Model loadModel(Document document, String path, CollisionShape shape,
                                  boolean asResource, MetaModel metaModel){

        //Extracted infos from nodes
        HashMap<String, Pair<String, Integer>> images = null;
        HashMap<String, Pair<MetaMaterial, Material>> effects = null;
        HashMap<String, Pair<MetaMaterial, Material>> materials = null;
        List<Pair<String, IndexBuffer>> indexBuffers = new ArrayList<>();
        VertexArrayObject geometry = null;
        Model model = null;

        //Iterate through children of the document node
        NodeList nodes = document.getDocumentElement().getChildNodes();
        for(int i = 0; i < nodes.getLength(); i++){

            //Get current node
            Node node = nodes.item(i);

            //Switch node
            switch (node.getNodeName()) {
                case "#text":                   continue;
                case "library_images":          images = loadImages(node, asResource, path);
                                                break;
                case "library_effects":         effects = loadEffects(node, images);
                                                break;
                case "library_materials":       materials = loadMaterials(node, effects);
                                                break;
                case "library_geometries":      geometry = loadGeometry(node, indexBuffers, shape, metaModel);
                                                break;
                case "library_visual_scenes":   model = loadVisualScene(node, materials, geometry, indexBuffers,
                                                            shape, metaModel);
                                                break;
            }
        }

        return model;
    }

    /**Loading images from dae images node
     *
     * @param library_images Dae images node
     * @param asResource Load images from resources?
     * @param path Path of the images
     * @return Loaded images
     */
    private static HashMap<String, Pair<String, Integer>> loadImages(Node library_images, boolean asResource, String path){
        HashMap<String, Pair<String, Integer>> images = new HashMap<>();

        //Iterate through children
        for(int i = 0; i < library_images.getChildNodes().getLength(); i++){
            Node child = library_images.getChildNodes().item(i);

            //Check if child is an image
            if(child.getNodeName().equals("image")){

                //Get image id
                String id = child.getAttributes().getNamedItem("id").getNodeValue();

                //Get image path
                Node init_from = getSpecificFirstChild(child, "init_from");
                Node ref = getSpecificFirstChild(init_from, "ref");
                String imagePath = ref.getTextContent();

                //Load image file
                int imageGl = ImageLoader.loadImageFileGl(path + imagePath, true, GL11.GL_LINEAR, asResource);

                //Push image into list
                images.put(id, new Pair<>(imagePath, imageGl));
            }
        }
        return images;
    }

    /**Loading effect from dae effects node
     *
     * @param library_effects Dae effect node
     * @param images Loaded images
     * @return Loaded effects
     */
    private static HashMap<String, Pair<MetaMaterial, Material>> loadEffects (
            Node library_effects, HashMap<String, Pair<String, Integer>> images){
        HashMap<String, Pair<MetaMaterial, Material>> effects = new HashMap<>();

        //Iterate through children
        for(int i = 0; i < library_effects.getChildNodes().getLength(); i++){
            Node child = library_effects.getChildNodes().item(i);

            //Check if child is an effect
            if(child.getNodeName().equals("effect")){
                Material material = new Material();
                MetaMaterial metaMaterial = new MetaMaterial();

                //Get effect id
                String id = child.getAttributes().getNamedItem("id").getNodeValue();

                //Get image redirects
                HashMap<String, String> imageParams = new HashMap<>();
                Node profile_COMMON = getSpecificFirstChild(child, "profile_COMMON");
                for(int j = 0; j < profile_COMMON.getChildNodes().getLength(); j++){
                    Node param = profile_COMMON.getChildNodes().item(j);
                    if(param.getNodeName().equals("newparam")){

                        //Id of the param
                        String sid = param.getAttributes().getNamedItem("sid").getNodeValue();

                        //Image id of the param
                        Node sampler2D = getSpecificFirstChild(param, "sampler2D");
                        Node instance_image = getSpecificFirstChild(sampler2D, "instance_image");
                        String imageId = instance_image.getAttributes().getNamedItem("url").getNodeValue();

                        imageParams.put(sid, imageId.substring(1));
                    }
                }

                //Get blinn tag
                Node technique = getSpecificFirstChild(profile_COMMON, "technique");
                Node blinn = getSpecificFirstChild(technique, "blinn");

                //Iterate data tags
                for(int k = 0; k < blinn.getChildNodes().getLength(); k++){
                    Node dataTag = blinn.getChildNodes().item(k);

                    switch (dataTag.getNodeName()) {
                        case "diffuse":     Node diffuse;
                                            if((diffuse = getSpecificFirstChild(dataTag, "texture")) != null){
                                                material.diffuseMap = images.get(imageParams.get(
                                                        diffuse.getAttributes().getNamedItem("texture").getNodeValue())
                                                ).getValue();
                                                metaMaterial.diffuseMap = images.get(imageParams.get(
                                                        diffuse.getAttributes().getNamedItem("texture").getNodeValue())
                                                ).getKey();
                                            }else if((diffuse = getSpecificFirstChild(dataTag, "color")) != null){
                                                material.diffuseColor = getColorfromText(diffuse.getTextContent());
                                                metaMaterial.diffuseColor = new Color();
                                                metaMaterial.diffuseColor.set(material.diffuseColor);
                                            }
                                            break;
                        case "specular":    Node specular;
                                            if((specular = getSpecificFirstChild(dataTag, "texture")) != null){
                                                material.specularMap = images.get(imageParams.get(
                                                        specular.getAttributes().getNamedItem("texture").getNodeValue())
                                                ).getValue();
                                                metaMaterial.specularMap = images.get(imageParams.get(
                                                        specular.getAttributes().getNamedItem("texture").getNodeValue())
                                                ).getKey();
                                            }
                                            break;
                        case "shininess":   Node shininess;
                                            if((shininess = getSpecificFirstChild(dataTag, "float")) != null){
                                                material.reflectivity = Float.parseFloat(shininess.getTextContent());
                                                metaMaterial.reflectivity = material.reflectivity;
                                            }
                                            break;
                    }
                }

                //Add new effect
                effects.put(id, new Pair<>(metaMaterial, material));
            }
        }

        return effects;
    }

    /**Loading materials from dae material node
     *
     * @param library_materials Dae materials node
     * @param effects Loaded effects
     * @return Loaded materials
     */
    private static HashMap<String, Pair<MetaMaterial, Material>> loadMaterials (
            Node library_materials, HashMap<String, Pair<MetaMaterial, Material>> effects){
        HashMap<String, Pair<MetaMaterial, Material>> materials = new HashMap<>();

        //Iterate through children
        for(int i = 0; i < library_materials.getChildNodes().getLength(); i++){
            Node child = library_materials.getChildNodes().item(i);

            //Check if child is an material
            if(child.getNodeName().equals("material")){

                //Get material id
                String id = child.getAttributes().getNamedItem("id").getNodeValue();

                //Get effect
                Node instance_effect = getSpecificFirstChild(child, "instance_effect");
                String effectId = instance_effect.getAttributes().getNamedItem("url").getNodeValue();

                //Add material to materials
                materials.put(id, effects.get(effectId.substring(1)));
            }
        }

        return materials;
    }

    /**Loading geometry from dae geometry node
     *
     * @param library_geometries Geometry node
     * @param indexBuffers List to store index buffers in, tagged by material
     * @param shape Shape to store collision data in
     * @param meta Meta model to store meta data in (or null)
     * @return Generated vao
     */
    private static VertexArrayObject loadGeometry(Node library_geometries, List<Pair<String, IndexBuffer>> indexBuffers,
                                                  CollisionShape shape, MetaModel meta){

        //Get mesh node for first geometry
        Node geometry = getSpecificFirstChild(library_geometries, "geometry");
        Node mesh = getSpecificFirstChild(geometry, "mesh");

        //Mesh data
        HashMap<String, float[]> meshData = new HashMap<>();
        List<Pair<String, int[]>> rawIndexBuffers = new LinkedList<>();
        float[] rawVertices = null, rawTexCoords = null, rawNormals = null;
        int vertexOffset = 0, texCoordsOffset = 0, normalsOffset = 0;

        //Iterate mesh's children
        for(int i = 0; i < mesh.getChildNodes().getLength(); i++){
            Node node = mesh.getChildNodes().item(i);

            switch (node.getNodeName()) {
                case "source":      String id = node.getAttributes().getNamedItem("id").getNodeValue();
                                    Node array = getSpecificFirstChild(node, "float_array");
                                    meshData.put(id, Toolbox.stringToArrayf(array.getTextContent(), " "));
                                    break;

                case "vertices":    String newId = node.getAttributes().getNamedItem("id").getNodeValue();
                                    Node input = getSpecificFirstChild(node, "input");
                                    String oldId = input.getAttributes().getNamedItem("source").getNodeValue().substring(1);
                                    meshData.put(newId, meshData.get(oldId));
                                    break;

                case "triangles":   for(int j = 0; j < node.getChildNodes().getLength(); j++){
                                        Node child = node.getChildNodes().item(j);

                                        switch(child.getNodeName()) {
                                            case "input":   switch(child.getAttributes().getNamedItem("semantic").getNodeValue()){
                                                                case "VERTEX":  vertexOffset =
                                                                                Integer.parseInt(child.getAttributes().
                                                                                getNamedItem("offset").getNodeValue());
                                                                                rawVertices =
                                                                                meshData.get(child.getAttributes().
                                                                                getNamedItem("source").getNodeValue().
                                                                                substring(1));
                                                                                break;

                                                                case "NORMAL":  normalsOffset =
                                                                                Integer.parseInt(child.getAttributes().
                                                                                getNamedItem("offset").getNodeValue());
                                                                                rawNormals =
                                                                                meshData.get(child.getAttributes().
                                                                                getNamedItem("source").getNodeValue().
                                                                                substring(1));
                                                                                break;

                                                                case "TEXCOORD":texCoordsOffset =
                                                                                Integer.parseInt(child.getAttributes().
                                                                                getNamedItem("offset").getNodeValue());
                                                                                rawTexCoords =
                                                                                meshData.get(child.getAttributes().
                                                                                getNamedItem("source").getNodeValue().
                                                                                substring(1));
                                                                                break;
                                                            }
                                                            break;

                                            case "p":       int[] rawIndices =
                                                            Toolbox.stringToArrayi(child.getTextContent(), " ");
                                                            rawIndexBuffers.add(new Pair<>(
                                                            node.getAttributes().getNamedItem("material").
                                                            getNodeValue(), rawIndices));
                                                            break;
                                        }
                                    }
                                    break;
            }
        }

        //Convert raw data to model
        VertexArrayObject result = convertMeshData(rawVertices, rawTexCoords, rawNormals, rawIndexBuffers,
                vertexOffset, texCoordsOffset, normalsOffset, shape, meta, indexBuffers);

        return result;
    }

    /**Loading visual scene from dae node
     *
     * @param library_visual_scenes Dae node
     * @param materials Loaded materials
     * @param geometry Loaded geometry
     * @param indexBuffers Loaded index buffers
     * @param shape Loaded collision shape
     * @param metaModel Loaded meta data
     * @return Final model
     */
    private static Model loadVisualScene(Node library_visual_scenes, HashMap<String, Pair<MetaMaterial, Material>> materials,
                                         VertexArrayObject geometry, List<Pair<String, IndexBuffer>> indexBuffers,
                                         CollisionShape shape, MetaModel metaModel){

        //Get material bindings node
        Node visual_scene = getSpecificFirstChild(library_visual_scenes, "visual_scene");
        Node node = getSpecificFirstChild(visual_scene, "node");
        Node instance_geometry = getSpecificFirstChild(node, "instance_geometry");
        Node bind_material = getSpecificFirstChild(instance_geometry, "bind_material");
        Node technique_common = getSpecificFirstChild(bind_material, "technique_common");

        //Final material map
        HashMap<String, Pair<MetaMaterial, Material>> finalMaterialMap = new HashMap<>();

        //Iterate through material bindings
        for(int i = 0; i < technique_common.getChildNodes().getLength(); i++){
            Node binding = technique_common.getChildNodes().item(i);

            //Check if child is a material binding
            if(binding.getNodeName().equals("instance_material")){

                //Get data from node
                String targetMaterial = binding.getAttributes().getNamedItem("target").getNodeValue().substring(1);
                String symbolMaterial = binding.getAttributes().getNamedItem("symbol").getNodeValue();

                //Put into final map
                finalMaterialMap.put(symbolMaterial, materials.get(targetMaterial));
            }
        }

        //Create final data structures
        IndexBuffer[] indexBuffersFinal = new IndexBuffer[indexBuffers.size()];
        Material[] materialsFinal = new Material[indexBuffers.size()];
        MetaMaterial[] metaMaterialsFinal = new MetaMaterial[indexBuffers.size()];

        //Order materials and index buffers
        for(int i = 0; i < indexBuffers.size(); i++){
            indexBuffersFinal[i] = indexBuffers.get(i).getValue();

            //check if material for this mesh exist
            String material = indexBuffers.get(i).getKey();
            if(finalMaterialMap.containsKey(material)){

                materialsFinal[i] = finalMaterialMap.get(material).getValue();
                metaMaterialsFinal[i] = finalMaterialMap.get(material).getKey();
            }
        }

        //Fill meta model if exist
        if(metaModel != null)
            metaModel.setMaterials(metaMaterialsFinal);

        return new Model(geometry, indexBuffersFinal, materialsFinal, shape);
    }

    /**Converting raw dae model data into core engine / opengl format
     *
     * @param verticesRaw Raw dae vertices
     * @param texCoordsRaw Raw dae texture coordinates
     * @param normalsRaw Raw dae normals
     * @param indexBuffersRaw Raw dae index buffers
     * @param vertexOffset Offset of the vertex ids in the raw dae indices
     * @param texCoordsOffset Offset of the tex coords ids in the raw dae indices
     * @param normalsOffset Offset of the normal ids in the raw dae indices
     * @param shape Collision shape to write in data
     * @param meta Meta model to write in meta data
     * @param indexBuffers List to return index buffers in (tagged by material name)
     * @return Generated vao
     */
    private static VertexArrayObject convertMeshData(float[] verticesRaw, float[] texCoordsRaw, float[] normalsRaw,
                                                    List<Pair<String, int[]>> indexBuffersRaw, int vertexOffset,
                                                    int texCoordsOffset, int normalsOffset, CollisionShape shape,
                                                    MetaModel meta, List<Pair<String, IndexBuffer>> indexBuffers){

        //Create result
       VertexArrayObject result = new VertexArrayObject();

        //New vertex data
        List<Float> vertices = new LinkedList<>(), normals = new LinkedList<>(), texCoords = new LinkedList<>(),
            tangents = new LinkedList<>();
        int[][] indices = new int[indexBuffersRaw.size()][];

        //List with all already processed vertices
        List<Vertex> alreadyProcessedVertices = new LinkedList<>();

        //Iterate through objects
        int counter = 0;
        for(Pair<String, int[]> object: indexBuffersRaw){

            //New converted index list
            List<Integer> indicesList = new LinkedList<>();

            //Iterate through vertices
            for(int i = 0; i < object.getValue().length; i += 9){

                //Create current vertices
                Vertex vertex0 = new Vertex();
                vertex0.positionId = object.getValue()[i +vertexOffset];
                vertex0.texCoordId = object.getValue()[i +texCoordsOffset];
                vertex0.normalId = object.getValue()[i +normalsOffset];

                Vertex vertex1 = new Vertex();
                vertex1.positionId = object.getValue()[i +vertexOffset +3];
                vertex1.texCoordId = object.getValue()[i +texCoordsOffset +3];
                vertex1.normalId = object.getValue()[i +normalsOffset +3];

                Vertex vertex2 = new Vertex();
                vertex2.positionId = object.getValue()[i +vertexOffset +6];
                vertex2.texCoordId = object.getValue()[i +texCoordsOffset +6];
                vertex2.normalId = object.getValue()[i +normalsOffset +6];

                //Process vertices
                int index0 = processVertex(vertex0, verticesRaw, texCoordsRaw, normalsRaw, vertices, texCoords,
                        normals, tangents, indicesList, alreadyProcessedVertices);
                int index1 = processVertex(vertex1, verticesRaw, texCoordsRaw, normalsRaw, vertices, texCoords,
                        normals, tangents, indicesList, alreadyProcessedVertices);
                int index2 = processVertex(vertex2, verticesRaw, texCoordsRaw, normalsRaw, vertices, texCoords,
                        normals, tangents, indicesList, alreadyProcessedVertices);

                //Calculate tangent
                float[] tangent = Toolbox.calcTangent(
                        vertices.get(index0 * 3),
                        vertices.get(index0 * 3 +1),
                        vertices.get(index0 * 3 +2),
                        vertices.get(index1 * 3),
                        vertices.get(index1 * 3 +1),
                        vertices.get(index1 * 3 +2),
                        vertices.get(index2 * 3),
                        vertices.get(index2 * 3 +1),
                        vertices.get(index2 * 3 +2),
                        texCoords.get(index0 * 2),
                        texCoords.get(index0 * 2 +1),
                        texCoords.get(index1 * 2),
                        texCoords.get(index1 * 2 +1),
                        texCoords.get(index2 * 2),
                        texCoords.get(index2 * 2 +1)
                );

                //Set new tangent to vertices
                tangents.set(index0 * 3, tangent[0]);
                tangents.set(index0 * 3 +1, tangent[1]);
                tangents.set(index0 * 3 +2, tangent[2]);
                tangents.set(index1 * 3, tangent[0]);
                tangents.set(index1 * 3 +1, tangent[1]);
                tangents.set(index1 * 3 +2, tangent[2]);
                tangents.set(index2 * 3, tangent[0]);
                tangents.set(index2 * 3 +1, tangent[1]);
                tangents.set(index2 * 3 +2, tangent[2]);
            }

            //Add indices to vao
            indices[counter] = Toolbox.toArrayi(indicesList);
            IndexBuffer indexBuffer = result.addIndexBuffer(indices[counter]);
            indexBuffers.add(new Pair<>(object.getKey(), indexBuffer));

            counter++;
        }

        //Convert lists to arrays
        float[] v   = Toolbox.toArrayf(vertices);
        float[] tc  = Toolbox.toArrayf(texCoords);
        float[] n   = Toolbox.toArrayf(normals);
        float[] t   = Toolbox.toArrayf(tangents);

        //Create collision shape
        if(shape instanceof ConvexHullShape)
            shape = Physics.createConvexHullShape(v);
        else if(shape instanceof TriangleMeshShape)
            shape = Physics.createTriangleMeshShape(v, indices);

        //Adding data to vao
        result.addVertexBuffer(v, 3, 0);
        result.addVertexBuffer(tc, 2, 1);
        result.addVertexBuffer(n, 3, 2);
        result.addVertexBuffer(t, 3, 3);

        //Fill up meta model
        if(meta != null){
            meta.setVertices(v);
            meta.setTexCoords(tc);
            meta.setNormals(n);
            meta.setTangents(t);
            meta.setIndices(indices);
            meta.setShape(shape);
        }

        return result;
    }

    /**Processing vertex from vertex data
     *
     * @param vertex Raw vertex indices
     * @param verticesRaw Raw vertex data of the model
     * @param texCoordsRaw Raw tex coords data of the model
     * @param normalsRaw Raw normal data of the model
     * @param verticesList Converted vertex data
     * @param texCoordsList Converted tex coords data
     * @param normalsList Converted normals data
     * @param tangentList Calculated tangents
     * @param indicesList Generated indices
     * @param alreadyProcessedVertices List of all vertices, that where already processed
     * @return Index of the processed vertex
     */
    private static int processVertex(Vertex vertex, float[] verticesRaw, float[] texCoordsRaw,
                                     float[] normalsRaw, List<Float> verticesList, List<Float> texCoordsList,
                                     List<Float> normalsList, List<Float> tangentList, List<Integer> indicesList,
                                     List<Vertex> alreadyProcessedVertices){

        int index = alreadyProcessedVertices.indexOf(vertex);

        //Check if similar vertex was already processed
        if(index == -1) {

            //Convert raw vertices
            verticesList.add(verticesRaw[(vertex.positionId) * 3]);
            verticesList.add(verticesRaw[(vertex.positionId) * 3 + 1]);
            verticesList.add(verticesRaw[(vertex.positionId) * 3 + 2]);

            //Convert raw tex coords
            texCoordsList.add(texCoordsRaw[(vertex.texCoordId) * 2]);
            texCoordsList.add(texCoordsRaw[(vertex.texCoordId) * 2 + 1]);

            //Convert raw normals
            normalsList.add(normalsRaw[(vertex.normalId) * 3]);
            normalsList.add(normalsRaw[(vertex.normalId) * 3 + 1]);
            normalsList.add(normalsRaw[(vertex.normalId) * 3 + 2]);

            //Add placeholder tangents
            tangentList.add(0.0f);
            tangentList.add(0.0f);
            tangentList.add(0.0f);

            //Convert raw indices
            alreadyProcessedVertices.add(vertex);
            index = alreadyProcessedVertices.size() -1;
        }

        indicesList.add(index);
        return index;
    }

    /**Converting a color from text representation into a Color object
     *
     * @param text Color as text
     * @return Converted Color object
     */
    private static Color getColorfromText(String text){
        String[] values = text.split(" ");

        Color color = new Color();
        color.setRed(Float.parseFloat(values[0]));
        color.setGreen(Float.parseFloat(values[1]));
        color.setBlue(Float.parseFloat(values[2]));

        return color;
    }

    /**Getting first child of a node, that has a specific name
     *
     * @param parent Parent to get child from
     * @param tag Name of the child
     * @return First child with this name
     */
    private static Node getSpecificFirstChild(Node parent, String tag){
        for(int i = 0; i < parent.getChildNodes().getLength(); i++){
            if(parent.getChildNodes().item(i).getNodeName().equals(tag)){
                return parent.getChildNodes().item(i);
            }
        }
        return null;
    }

    /**Simple data class, that stores vertex data for comparison
     */
    private static class Vertex {
        int positionId, texCoordId, normalId;

        @Override
        public boolean equals(Object obj) {

            if(!(obj instanceof Vertex))
                return false;

            return normalId == ((Vertex) obj).normalId &&
                    positionId == ((Vertex) obj).positionId &&
                    texCoordId == ((Vertex) obj).texCoordId;
        }
    }
}
