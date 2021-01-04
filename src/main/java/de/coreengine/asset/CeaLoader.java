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

import de.coreengine.asset.dataStructures.AnimatedModelData;
import de.coreengine.util.Logger;

import java.io.*;

/**
 * Class that can load cea (Core Engine Animated Model) files
 * 
 * @see de.coreengine.asset.dataStructures
 * @author Darius
 */
public class CeaLoader {

    /**
     * Loading an animated model from a file into the asset database. If the model
     * already loaded, this method does nothing.
     *
     * @param file       File to load
     * @param texPath    Location of the models textures
     * @param asResource Loading model and textures from resources
     */
    public static void loadAnimatedModel(String file, String texPath, boolean asResource) {
        if (AssetDatabase.getAnimatedModel(file) != null)
            return;
        AnimatedModelData modelData = loadAnimatedModelData(file, asResource);
        if (modelData != null)
            AssetDatabase.addAnimatedModel(file, modelData.getInstance(texPath, asResource));
    }

    /**
     * Saving dataStructures model to a file
     *
     * @param file      Filename to save
     * @param modelData Meta model to save
     */
    public static void saveAnimatedModelData(String file, AnimatedModelData modelData) {

        try {

            // Construct data from dataStructures model
            byte[] data = modelData.toBytes();

            // Read bytes from file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();

        } catch (FileNotFoundException e0) {
            Logger.err("Error by saving animated model data",
                    "The animated model data file " + file + " could not be found! Returning null!");
        } catch (IOException e) {
            Logger.err("Error by saving animated model data",
                    "The animated model data file " + file + " could not be saved! Returning null!");
        }
    }

    /**
     * Loading animated model data from a file
     *
     * @param file       File to load
     * @param asResource Loading animated model data from resources
     * @return Loaded animated model data
     */
    public static AnimatedModelData loadAnimatedModelData(String file, boolean asResource) {

        try {

            InputStream is;
            if (asResource)
                is = CeaLoader.class.getClassLoader().getResourceAsStream(file);
            else
                is = new FileInputStream(new File(file));

            // Read bytes from file
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();

            // Construct dataStructures model from bytes
            AnimatedModelData modelData = new AnimatedModelData();
            modelData.fromBytes(data);
            return modelData;

        } catch (FileNotFoundException e0) {
            Logger.warn("Error by loading animated model data",
                    "The animated model data file " + file + " could not be found! Returning null!");
        } catch (IOException e) {
            Logger.warn("Error by loading animated model data",
                    "The animated model data file " + file + " could not be loaded! Returning null!");
        }

        return null;
    }
}
