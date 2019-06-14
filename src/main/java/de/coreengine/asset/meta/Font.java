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
package de.coreengine.asset.meta;

import de.coreengine.util.gl.VertexArrayObject;
import java.util.HashMap;

/**Class that represent a font to render text
 *
 * @author Darius Dinger
 */
public class Font {
    
    //Texture atlas image of the font
    private final int textureAtlas;
    
    //Characters of the font sirtet by ascii
    private final HashMap<Integer, Character> characters;
    
    //Vertex array object of the character models
    private final VertexArrayObject vao;
    
    //Height of a line of text
    private final float lineHeight;
    
    /**Creating new font
     * 
     * @param textureAtlas Texture atlas image of the font
     * @param characters Characters of the font sirtet by ascii
     * @param vao Vertex array object of the character models
     * @param lineHeight Height of a line of text
     */
    public Font(int textureAtlas, HashMap<Integer, Character> characters, 
            VertexArrayObject vao, float lineHeight) {
        this.textureAtlas = textureAtlas;
        this.characters = characters;
        this.vao = vao;
        this.lineHeight = lineHeight;
    }
    
    /**@return Texture atlas image of the font
     */
    public int getTextureAtlas() {
        return textureAtlas;
    }
    
    /**Getting character for specific ascii
     * 
     * @param ascii Ascii of the char
     * @return Character of ascii
     */
    public Character getCharacter(int ascii){
        return characters.get(ascii);
    }
    
    /**@return VAO of the font chars
     */
    public VertexArrayObject getVao() {
        return vao;
    }
    
    /**@return Height of a line of text
     */
    public float getLineHeight() {
        return lineHeight;
    }
}
