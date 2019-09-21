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
import com.sun.deploy.xml.XMLNode;
import com.sun.deploy.xml.XMLParser;
import de.coreengine.asset.meta.MetaMaterial;
import de.coreengine.asset.meta.MetaModel;
import de.coreengine.rendering.model.Color;
import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.model.Model;
import de.coreengine.util.Logger;
import de.coreengine.util.Toolbox;
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

    public static Model loadModel(Document document, String path, CollisionShape shape,
                                  boolean asResource, MetaModel metaModel){

        //Extracted infos from nodes
        HashMap<String, Pair<String, Integer>> images = null;
        HashMap<String, Pair<MetaMaterial, Material>> effects = null;
        HashMap<String, Pair<MetaMaterial, Material>> materials = null;

        //Iterate through children of the document node
        NodeList nodes = document.getDocumentElement().getChildNodes();
        for(int i = 0; i < nodes.getLength(); i++){

            //Get current node
            Node node = nodes.item(i);

            //Switch node
            switch (node.getNodeName()) {
                case "#text":               continue;
                case "library_images":      images = loadImages(node, asResource, path);
                                            break;
                case "library_effects":     effects = loadEffects(node, images);
                                            break;
                case "library_materials":   materials = loadMaterials(node, effects);
                                            break;
            }
        }

        return null;
    }

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

    private static Color getColorfromText(String text){
        String[] values = text.split(" ");

        Color color = new Color();
        color.setRed(Float.parseFloat(values[0]));
        color.setGreen(Float.parseFloat(values[1]));
        color.setBlue(Float.parseFloat(values[2]));

        return color;
    }

    private static Node getSpecificFirstChild(Node parent, String tag){
        for(int i = 0; i < parent.getChildNodes().getLength(); i++){
            if(parent.getChildNodes().item(i).getNodeName().equals(tag)){
                return parent.getChildNodes().item(i);
            }
        }
        return null;
    }
}
