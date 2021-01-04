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

/**
 * Class to store loaded assets
 */
public class AssetDatabase {

    // Databases
    private static HashMap<String, Integer> textures = new HashMap<>();
    private static HashMap<String, Font> fonts = new HashMap<>();
    private static HashMap<String, Integer> sounds = new HashMap<>();
    private static HashMap<String, Model> models = new HashMap<>();
    private static HashMap<String, AnimatedModel> animatedModels = new HashMap<>();

    /**
     * Getting texture by name from the database. Returns 0, if the texture could
     * not be found
     * 
     * @param name Name of the texture in the database
     * @return Texture with this name
     */
    public static int getTexture(String name) {
        Integer texture = textures.get(name);
        return texture == null ? 0 : texture;
    }

    /**
     * Manually store a texture into the database. If a texture with this name
     * already exist, it will be overwritten!
     * 
     * @param name  Name of the texture
     * @param model Texture to store
     */
    public static void addTexture(String name, Integer texture) {
        textures.put(name, texture);
    }

    /**
     * Getting font by name from the database. Returns null, if the font could not
     * be found
     *
     * @param name Name of the font in the database
     * @return Font with this name
     */
    public static Font getFont(String name) {
        return fonts.get(name);
    }

    /**
     * Manually store a font into the database. If a font with this name already
     * exist, it will be overwritten!
     * 
     * @param name  Name of the font
     * @param model Font to store
     */
    public static void addFont(String name, Font font) {
        fonts.put(name, font);
    }

    /**
     * Getting sound by name from the database. Returns 0, if the sound could not be
     * found
     *
     * @param name Name of the sound in the database
     * @return Sound with this name
     */
    public static int getSound(String name) {
        Integer sound = sounds.get(name);
        return sound == null ? 0 : sound;
    }

    /**
     * Manually store a sound into the database. If a sound with this name already
     * exist, it will be overwritten!
     * 
     * @param name  Name of the sound
     * @param model Sound to store
     */
    public static void addSound(String name, Integer sound) {
        sounds.put(name, sound);
    }

    /**
     * Getting model by name from the database. Returns null, if the model could not
     * be found
     *
     * @param name Name of the model in the database
     * @return Model with this name
     */
    public static Model getModel(String name) {
        return models.get(name);
    }

    /**
     * Manually store a model into the database. If a model with this name already
     * exist, it will be overwritten!
     * 
     * @param name  Name of the model
     * @param model Model to store
     */
    public static void addModel(String name, Model model) {
        models.put(name, model);
    }

    /**
     * Getting animated model by name from the database. Returns null, if the
     * animated model could not be found
     *
     * @param name Name of the animated model in the database
     * @return Animated model with this name
     */
    public static AnimatedModel getAnimatedModel(String name) {
        return animatedModels.get(name);
    }

    /**
     * Manually store an animated model into the database. If an animated model with
     * this name already exist, it will be overwritten!
     * 
     * @param name  Name of the model
     * @param model Model to store
     */
    public static void addAnimatedModel(String name, AnimatedModel model) {
        animatedModels.put(name, model);
    }

    static {
        // Store default black opengl texture
        textures.put("black", 0);
    }
}
