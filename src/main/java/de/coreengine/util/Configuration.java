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
package de.coreengine.util;

import de.coreengine.asset.FileLoader;

import java.util.HashMap;

/**
 * Class that can load a config file with default values and stores them
 *
 * @author Darius Dinger
 */
public class Configuration {

    private static final String ARRAY_LIMITER = ";";

    // Configuration File location
    private static final String CONFIG_FILE = "res/config.ini";

    // Configurations loaded from config file
    private static HashMap<String, Object> config = null;

    /**
     * Initializing the configuration and (re)loading the config file
     */
    public static void loadConfig(String configFile) {

        // Creating config map
        config = new HashMap<>();

        // Load config file and split into lines
        String[] configLines = FileLoader.getResource(CONFIG_FILE, false);

        // Iterate lines
        for (String line : configLines) {
            // Check if line is empty or comment
            if (line != null && !line.equals("") && !line.startsWith("//") && !line.startsWith("#")) {

                // Get Setting key and value and put into configuration
                String[] pair = line.split("=");
                if (pair.length >= 2)
                    config.put(pair[0], pair[1]);
            }
        }
    }

    /**
     * Initializing the configuration and (re)loading the config file
     */
    public static void loadConfig() {
        loadConfig(CONFIG_FILE);
    }

    /**
     * Getting float value of setting Loggin an error and returning 1, if setting
     * not found
     * 
     * @param id Name or id of the Setting
     * @return Setting as float
     */
    public static float getValuef(String id) {

        // Check if config contains setting
        if (config.containsKey(id)) {
            return Float.parseFloat((String) config.get(id));
        } else {
            Logger.warn("Setting not found",
                    "The Setting '" + id + "' could not be found in the configuration file\n" + "returning 1");
            return 1.0f;
        }
    }

    /**
     * Getting int value of setting Loggin an error and returning 1, if setting not
     * found
     * 
     * @param id Name or id of the Setting
     * @return Setting as int
     */
    public static int getValuei(String id) {

        // Check if config contains setting
        if (config.containsKey(id)) {
            return Integer.parseInt((String) config.get(id));
        } else {
            Logger.warn("Setting not found",
                    "The Setting '" + id + "' could not be found in the configuration file\n" + "returning 1");
            return 1;
        }
    }

    /**
     * Getting string value of setting Loggin an error and returning "", if setting
     * not found
     * 
     * @param id Name or id of the Setting
     * @return Setting as string
     */
    public static String getValues(String id) {

        // Check if config contains setting
        if (config.containsKey(id)) {
            return (String) config.get(id);
        } else {
            Logger.warn("Setting not found",
                    "The Setting '" + id + "' could not be found in the configuration file\n" + "returning ''");
            return "";
        }
    }

    /**
     * Getting float array value of setting Loggin an error and returning {}, if
     * setting not found
     * 
     * @param id Name or id of the Setting
     * @return Setting as float array
     */
    public static float[] getValuefa(String id) {

        // Check if config contains setting
        if (config.containsKey(id)) {

            // Split array data and create result array
            String[] data = ((String) config.get(id)).split(ARRAY_LIMITER);
            float[] result = new float[data.length];

            // Parse data into result array
            for (int i = 0; i < result.length; i++) {
                result[i] = Float.parseFloat(data[i]);
            }

            return result;
        } else {
            Logger.warn("Setting not found",
                    "The Setting '" + id + "' could not be found in the configuration file\n" + "returning 1");
            return new float[] {};
        }
    }
}
