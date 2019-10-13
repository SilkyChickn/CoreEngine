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

import de.coreengine.rendering.model.AnimatedModel;
import de.coreengine.rendering.model.Font;
import de.coreengine.rendering.model.Model;

import java.util.HashMap;

/**Class to store loaded assets
 */
public class AssetDatabase {

    //Databases
    public static HashMap<String, Integer> textures = new HashMap<>();
    static HashMap<String, Font> fonts = new HashMap<>();
    static HashMap<String, Integer> sounds = new HashMap<>();
    static HashMap<String, Model> models = new HashMap<>();
    static HashMap<String, AnimatedModel> animatedModels = new HashMap<>();

    /**Getting texture by name from the database. Returns 0, if the texture could not be found
     * 
     * @param name Name of the texture in the database
     * @return Texture with this name
     */
    public static int getTexture(String name){
        Integer texture = textures.get(name);
        return texture == null ? 0: texture;
    }

    /**Getting font by name from the database. Returns null, if the font could not be found
     *
     * @param name Name of the font in the database
     * @return Font with this name
     */
    public static Font getFont(String name){
        return fonts.get(name);
    }

    /**Getting sound by name from the database. Returns 0, if the sound could not be found
     *
     * @param name Name of the sound in the database
     * @return Sound with this name
     */
    public static int getSound(String name){
        Integer sound = sounds.get(name);
        return sound == null ? 0: sound;
    }
    
    /**Getting model by name from the database. Returns null, if the model could not be found
     *
     * @param name Name of the model in the database
     * @return Model with this name
     */
    public static Model getModel(String name){
        return models.get(name);
    }
    
    /**Getting animated model by name from the database. Returns 0, if the animated model could not be found
     *
     * @param name Name of the animated model in the database
     * @return Animated model with this name
     */
    public static AnimatedModel getAnimatedModel(String name){
        return animatedModels.get(name);
    }

    static {
        //Store default black opengl texture
        textures.put("black", 0);
    }
}
