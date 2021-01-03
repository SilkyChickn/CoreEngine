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
package de.coreengine.asset.dataStructures;

import de.coreengine.asset.TextureLoader;
import de.coreengine.rendering.model.Color;
import de.coreengine.rendering.model.Material;
import de.coreengine.util.ByteArrayUtils;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

/**
 * Material data that can be stored into a file<br>
 * A value of null means the default value of a material
 *
 * @author Darius Dinger
 */
public class MaterialData {

        // Data
        public Color diffuseColor = null, glowColor = null;
        public String diffuseMap = null, normalMap = null, specularMap = null, displacementMap = null,
                        ambientOcclusionMap = null, alphaMap = null, reflectionMap = null, glowMap = null;
        public Float displacementFactor = null, tiling = null, shininess = null, shineDamping = null;

        /**
         * Constructing dataStructure material from a byte array.<br>
         * <br>
         * Format:<br>
         * First Sector [MetaData]:<br>
         * DefaultDiffuseColor (byte) | DefaultGlowColor (byte) | DiffuseMap size in
         * bytes (short) | NormalMap size in bytes (short) | SpecularMap size in bytes
         * (short) | DisplacementMap size in bytes (short) | AmbientOcclusionMap size in
         * bytes (short) | AlphaMap size in bytes (short) | ReflectionMap size in bytes
         * (short) | GlowMap size in bytes (short) | DefaultDisplacementFactor (byte) |
         * DefaultTiling (byte) | DefaultShininess (byte) | DefaultShineDamping
         * (byte)<br>
         * <br>
         * Second Sector [Colors]:<br>
         * DiffuseColor (3 floats) | GlowColor (3 floats)<br>
         * <br>
         * Third Sector [Textures]:<br>
         * DiffuseMapPath (string) | NormalMapPath (string) | SpecularMapPath (string) |
         * DisplacementMapPath (string) | AmbientOcclusionMapPath (string) |
         * AlphaMapPath (string) | ReflectionMapPath (string) | GlowMapPath (string)<br>
         * <br>
         * Fourth Sector [Floats]:<br>
         * DisplacementFactor (float) | Tiling (float) | Shininess (float) |
         * ShineDamping (float)<br>
         *
         * @param data Byte array to construct dataStructure material from
         */
        public void fromBytes(byte[] data) {

                // Get dataStructures data
                byte[] mapSizesB = Arrays.copyOfRange(data, 2, 18);
                short[] mapSizes = ByteArrayUtils.fromBytess(mapSizesB);

                // Get colors
                byte[] colorsB = Arrays.copyOfRange(data, 22, 46);
                float[] colors = ByteArrayUtils.fromBytesf(colorsB);
                diffuseColor = data[0] > 0 ? null : new Color(colors[0], colors[1], colors[2]);
                glowColor = data[1] > 0 ? null : new Color(colors[3], colors[4], colors[5]);

                // Get textures
                int counter = 46;
                diffuseMap = mapSizes[0] == 0 ? null
                                : new String(Arrays.copyOfRange(data, counter, counter += mapSizes[0]));
                normalMap = mapSizes[1] == 0 ? null
                                : new String(Arrays.copyOfRange(data, counter, counter += mapSizes[1]));
                specularMap = mapSizes[2] == 0 ? null
                                : new String(Arrays.copyOfRange(data, counter, counter += mapSizes[2]));
                displacementMap = mapSizes[3] == 0 ? null
                                : new String(Arrays.copyOfRange(data, counter, counter += mapSizes[3]));
                ambientOcclusionMap = mapSizes[4] == 0 ? null
                                : new String(Arrays.copyOfRange(data, counter, counter += mapSizes[4]));
                alphaMap = mapSizes[5] == 0 ? null
                                : new String(Arrays.copyOfRange(data, counter, counter += mapSizes[5]));
                reflectionMap = mapSizes[6] == 0 ? null
                                : new String(Arrays.copyOfRange(data, counter, counter += mapSizes[6]));
                glowMap = mapSizes[7] == 0 ? null
                                : new String(Arrays.copyOfRange(data, counter, counter += mapSizes[7]));

                // Get floats
                byte[] floatsB = Arrays.copyOfRange(data, counter, counter + 16);
                float[] floats = ByteArrayUtils.fromBytesf(floatsB);
                displacementFactor = data[18] > 0 ? null : floats[0];
                tiling = data[19] > 0 ? null : floats[1];
                shininess = data[20] > 0 ? null : floats[2];
                shineDamping = data[21] > 0 ? null : floats[3];
        }

        /**
         * Converting the dataStructure material into a byte array.<br>
         * <br>
         * Format:<br>
         * First Sector [MetaData]:<br>
         * DefaultDiffuseColor (byte) | DefaultGlowColor (byte) | DiffuseMap size in
         * bytes (short) | NormalMap size in bytes (short) | SpecularMap size in bytes
         * (short) | DisplacementMap size in bytes (short) | AmbientOcclusionMap size in
         * bytes (short) | AlphaMap size in bytes (short) | ReflectionMap size in bytes
         * (short) | GlowMap size in bytes (short) | DefaultDisplacementFactor (byte) |
         * DefaultTiling (byte) | DefaultShininess (byte) | DefaultShineDamping
         * (byte)<br>
         * <br>
         * Second Sector [Colors]:<br>
         * DiffuseColor (3 floats) | GlowColor (3 floats)<br>
         * <br>
         * Third Sector [Textures]:<br>
         * DiffuseMapPath (String) | NormalMapPath (String) | SpecularMapPath (String) |
         * DisplacementMapPath (String) | AmbientOcclusionMapPath (String) |
         * AlphaMapPath (String) | ReflectionMapPath (String) | GlowMapPath (String)<br>
         * <br>
         * Fourth Sector [Floats]:<br>
         * DisplacementFactor (float) | Tiling (float) | Shininess (float) |
         * ShineDamping (float)<br>
         * 
         * @return Converted byte array
         */
        public byte[] toBytes() {

                // Define dataStructures data
                byte[] defaultColors = new byte[] { diffuseColor == null ? (byte) 1 : (byte) 0,
                                glowColor == null ? (byte) 1 : (byte) 0, };

                short[] mapSizesS = new short[] { diffuseMap == null ? 0 : (short) diffuseMap.length(),
                                normalMap == null ? 0 : (short) normalMap.length(),
                                specularMap == null ? 0 : (short) specularMap.length(),
                                displacementMap == null ? 0 : (short) displacementMap.length(),
                                ambientOcclusionMap == null ? 0 : (short) ambientOcclusionMap.length(),
                                alphaMap == null ? 0 : (short) alphaMap.length(),
                                reflectionMap == null ? 0 : (short) reflectionMap.length(),
                                glowMap == null ? 0 : (short) glowMap.length() };
                byte[] mapSizes = ByteArrayUtils.toBytes(mapSizesS);

                byte[] defaultFloats = new byte[] { displacementFactor == null ? (byte) 1 : (byte) 0,
                                tiling == null ? (byte) 1 : (byte) 0, shininess == null ? (byte) 1 : (byte) 0,
                                shineDamping == null ? (byte) 1 : (byte) 0, };

                // Define data
                float[] colorsF = new float[] { diffuseColor == null ? 1.0f : diffuseColor.getRed(),
                                diffuseColor == null ? 1.0f : diffuseColor.getGreen(),
                                diffuseColor == null ? 1.0f : diffuseColor.getBlue(),
                                glowColor == null ? 1.0f : glowColor.getRed(),
                                glowColor == null ? 1.0f : glowColor.getGreen(),
                                glowColor == null ? 1.0f : glowColor.getBlue(), };
                byte[] colors = ByteArrayUtils.toBytes(colorsF);

                byte[] diffuseMapBytes = diffuseMap == null ? new byte[0] : diffuseMap.getBytes();
                byte[] normalMapBytes = normalMap == null ? new byte[0] : normalMap.getBytes();
                byte[] specularMapBytes = specularMap == null ? new byte[0] : specularMap.getBytes();
                byte[] displacementMapBytes = displacementMap == null ? new byte[0] : displacementMap.getBytes();
                byte[] ambientOcclusionMapBytes = ambientOcclusionMap == null ? new byte[0]
                                : ambientOcclusionMap.getBytes();
                byte[] alphaMapBytes = alphaMap == null ? new byte[0] : alphaMap.getBytes();
                byte[] reflectionMapBytes = reflectionMap == null ? new byte[0] : reflectionMap.getBytes();
                byte[] glowMapBytes = glowMap == null ? new byte[0] : glowMap.getBytes();

                float[] floatsF = new float[] { displacementFactor == null ? 0.0f : displacementFactor,
                                tiling == null ? 0.0f : tiling, shininess == null ? 0.0f : shininess,
                                shineDamping == null ? 0.0f : shineDamping };
                byte[] floats = ByteArrayUtils.toBytes(floatsF);

                // Create and return final array
                return ByteArrayUtils.combine(defaultColors, mapSizes, defaultFloats, colors, diffuseMapBytes,
                                normalMapBytes, specularMapBytes, displacementMapBytes, ambientOcclusionMapBytes,
                                alphaMapBytes, reflectionMapBytes, glowMapBytes, floats);
        }

        /**
         * Getting a new instance of the dataStructure material
         *
         * @param texPath    Location of the texture files
         * @param asResource Load from resources
         * 
         * @return New material instance
         */
        public Material getInstance(String texPath, boolean asResource) {
                Material instance = new Material();

                // Copy colors
                if (diffuseColor != null)
                        instance.diffuseColor.set(diffuseColor);
                if (glowColor != null)
                        instance.glowColor.set(glowColor);

                // Loading textures
                if (diffuseMap != null)
                        instance.diffuseMap = loadTexture(diffuseMap, texPath, asResource);
                if (normalMap != null)
                        instance.normalMap = loadTexture(normalMap, texPath, asResource);
                if (specularMap != null)
                        instance.specularMap = loadTexture(specularMap, texPath, asResource);
                if (displacementMap != null)
                        instance.displacementMap = loadTexture(displacementMap, texPath, asResource);
                if (ambientOcclusionMap != null)
                        instance.ambientOcclusionMap = loadTexture(ambientOcclusionMap, texPath, asResource);
                if (alphaMap != null)
                        instance.alphaMap = loadTexture(alphaMap, texPath, asResource);
                if (reflectionMap != null)
                        instance.reflectionMap = loadTexture(reflectionMap, texPath, asResource);
                if (glowMap != null)
                        instance.glowMap = loadTexture(glowMap, texPath, asResource);

                // Copy floats
                if (displacementFactor != null)
                        instance.displacementFactor = displacementFactor;
                if (tiling != null)
                        instance.tiling = tiling;
                if (shininess != null)
                        instance.shininess = shininess;
                if (shineDamping != null)
                        instance.shineDamping = shineDamping;

                return instance;
        }

        /**
         * Loading texture from material
         *
         * @param file       File to load
         * @param texPath    Location of the materials textures
         * @param asResource Load textures from resource
         * @return Loaded texture key
         */
        private String loadTexture(String file, String texPath, boolean asResource) {
                TextureLoader.loadTextureFile(texPath + file, true, GL11.GL_LINEAR, asResource);
                return texPath + file;
        }
}
