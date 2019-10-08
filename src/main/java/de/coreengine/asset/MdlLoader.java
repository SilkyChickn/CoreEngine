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

import de.coreengine.asset.meta.MetaModel;
import de.coreengine.util.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**Class that can load mdl files (Core engine file format, see de.coreengine.asset.meta)
 *
 * @author Darius
 */
public class MdlLoader {

    /**Loading a model from a file into the asset database. If the model already loaded, this method does nothing.
     *
     * @param file File to load
     * @param texPath Location of the models textures
     * @param asResource Loading models textures from resources
     */
    public static void loadModel(String file, String texPath, boolean asResource){
        if(AssetDatabase.models.containsKey(file)) return;
        MetaModel metaModel = loadMetaModel(file, asResource);
        AssetDatabase.models.put(file, metaModel.getInstance(texPath, asResource));
    }

    /**Saving meta model to a file
     *
     * @param file Filename to save
     * @param metaModel Meta model to save
     */
    public static void saveMetaModel(String file, MetaModel metaModel){

        try {

            //Construct data from meta model
            byte[] data = metaModel.toBytes();

            //Read bytes from file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();

        } catch (FileNotFoundException e0) {
            Logger.err("Error by saving meta model", "The meta model file " +
                    file + " could not be found! Returning null!");
        } catch (IOException e) {
            Logger.err("Error by saving meta model", "The meta model file " +
                    file + " could not be saved! Returning null!");
        }
    }

    /**Loading meta model from a file
     *
     * @param file File to load
     * @param asResource Loading meta model from resources
     * @return Loaded meta model
     */
    public static MetaModel loadMetaModel(String file, boolean asResource){

        try {

            //Read bytes from file
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();

            //Construct meta model from bytes
            MetaModel metaModel = new MetaModel();
            metaModel.fromBytes(data);
            return metaModel;

        } catch (FileNotFoundException e0) {
            Logger.warn("Error by loading meta model", "The meta model file " +
                    file + " could not be found! Returning null!");
        } catch (IOException e) {
            Logger.warn("Error by loading meta model", "The meta model file " +
                    file + " could not be loaded! Returning null!");
        }

        return null;
    }
}
