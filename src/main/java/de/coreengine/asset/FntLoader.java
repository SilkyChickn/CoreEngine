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

import de.coreengine.rendering.model.Character;
import de.coreengine.rendering.model.Font;
import de.coreengine.util.Logger;
import de.coreengine.util.Toolbox;
import de.coreengine.util.gl.IndexBuffer;
import de.coreengine.util.gl.VertexArrayObject;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**Class that can load a fnt file and its texture atlas
 *
 * @author Darius Dinger
 */
public class FntLoader {
    private static final float LINE_HEIGHT = 0.03f;
    
    /**Loading font file and its texture atlas from fnt file ad store it into database
     * 
     * @param file Fnt file name relative to application
     * @param asResource Load font from resources
     */
    public static void loadFont(String file, boolean asResource){
        if(AssetDatabase.fonts.containsKey(file)) return;

        try {
            
            //Load fnt file
            String[] data;
            if(asResource) data = FileLoader.getResource(file, false);
            else data = FileLoader.readFile(file, false);
            
            //Fnt data
            String textureAtlas = "";
            HashMap<Integer, Character> chars = new HashMap<>();
            VertexArrayObject vao = new VertexArrayObject();
            
            List<Float> vertices = new LinkedList<>();
            List<Float> offsets = new LinkedList<>();
            List<Float> texCoords = new LinkedList<>();
            
            int texWidth = 1, texHeight = 1;
            float lineHeight = 1, lineWidth = 1;
            
            //Get path of file
            int lastSlash = file.lastIndexOf("/") +1;
            String path;
            if(lastSlash == 0) path = "";
            else path = file.substring(0, lastSlash);
            
            //Iterate data
            int indicesCounter = 0;
            for(String line: data){
                String[] args = line.split(" ");
                
                switch(args[0]){
                    case "common":
                        
                        //Load texture atlas
                        for(String arg: args){
                            if(arg.startsWith("scaleW=")){
                                texWidth = Integer.parseInt(arg.split("=")[1]);
                            }else if(arg.startsWith("scaleH=")){
                                texHeight = Integer.parseInt(arg.split("=")[1]);
                            }else if(arg.startsWith("lineHeight=")){
                                lineHeight = Integer.parseInt(arg.split("=")[1]);
                            }
                        }
                        
                        //Bring line height in relation to image
                        lineHeight = LINE_HEIGHT / lineHeight;
                        lineWidth = lineHeight;
                        
                        break;
                    case "page":
                        
                        //Load texture atlas
                        for(String arg: args){
                            if(arg.startsWith("file=")){
                                textureAtlas = path + arg.split("=")[1].replace("\"", "");
                                TextureLoader.loadTextureFile(textureAtlas, false, GL11.GL_LINEAR, asResource);
                            }
                        }
                        
                        break;
                    case "char":
                        
                        int id = 0;
                        float x = 0, y = 0, w = 0, h = 0, ox = 0, oy = 0, ax = 0;
                        
                        //Load character
                        for(String arg: args){
                            if(arg.startsWith("id=")){
                                id = Integer.parseInt(arg.split("=")[1]);
                            }else if(arg.startsWith("x=")){
                                x = Float.parseFloat(arg.split("=")[1]);
                            }else if(arg.startsWith("y=")){
                                y = Float.parseFloat(arg.split("=")[1]);
                            }else if(arg.startsWith("width=")){
                                w = Float.parseFloat(arg.split("=")[1]);
                            }else if(arg.startsWith("height=")){
                                h = Float.parseFloat(arg.split("=")[1]);
                            }else if(arg.startsWith("xoffset=")){
                                ox = Float.parseFloat(arg.split("=")[1]) * lineWidth;
                            }else if(arg.startsWith("yoffset=")){
                                oy = Float.parseFloat(arg.split("=")[1]) * lineHeight;
                            }else if(arg.startsWith("xadvance=")){
                                ax = Float.parseFloat(arg.split("=")[1]) * lineWidth;
                            }
                        }
                        
                        //Create mesh
                        addVertexData(vertices, texCoords, ox, -oy, w * lineWidth, h * lineHeight, 
                                x / texWidth, y / texHeight, w / texWidth, h / texHeight);
                        
                        //Load indices into mesh
                        int[] indices = new int[6];
                        for(int i = 0; i < 6; i++){
                            indices[i] = indicesCounter++;
                        }
                        IndexBuffer index = vao.addIndexBuffer(indices);
                        
                        //Create new character an add to vao
                        Character newChar = new Character
                            (ox, oy, ax, index);
                        chars.put(id, newChar);
                        
                        break;
                }
            }
            
            float[] verticesArr = Toolbox.toArrayf(vertices);
            float[] offsetsArr = Toolbox.toArrayf(offsets);
            float[] texCoordsArr = Toolbox.toArrayf(texCoords);
            
            vao.addVertexBuffer(verticesArr, 2, 0);
            vao.addVertexBuffer(texCoordsArr, 2, 1);
            vao.addVertexBuffer(offsetsArr, 2, 2);

            AssetDatabase.fonts.put(file, new Font(textureAtlas, chars, vao, LINE_HEIGHT));
        } catch (IOException ex) {
            Logger.warn("Error by loading font", "The fnt file '" + file +
                    "' could not be loaded!");
        }
    }
    
    private static void addVertexData(List<Float> vertices, List<Float> texCoords, 
            float ox, float oy, float w, float h, float tx, float ty, float tw, float th){
        
        //Vertices
        vertices.add(ox +w);
        vertices.add(oy -h);
        
        vertices.add(ox +w);
        vertices.add(oy);
        
        vertices.add(ox);
        vertices.add(oy -h);
        
        vertices.add(ox);
        vertices.add(oy -h);
        
        vertices.add(ox +w);
        vertices.add(oy);
        
        vertices.add(ox);
        vertices.add(oy);
        
        //MetaTexture coords
        texCoords.add(tx +tw);
        texCoords.add(ty +th);
        
        texCoords.add(tx +tw);
        texCoords.add(ty);
        
        texCoords.add(tx);
        texCoords.add(ty +th);
        
        texCoords.add(tx);
        texCoords.add(ty +th);
        
        texCoords.add(tx +tw);
        texCoords.add(ty);
        
        texCoords.add(tx);
        texCoords.add(ty);
    }
}
