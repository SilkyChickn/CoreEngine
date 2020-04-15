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
package de.coreengine.rendering.model;

import de.coreengine.asset.TextureLoader;
import de.coreengine.util.Configuration;
import org.lwjgl.opengl.GL11;

/**
 * Class that represents a material with all its maps and parameters for a mesh
 *
 * @author Darius Dinger
 */
public class Material {
        /**
         * A pure black texture
         */
        public static final String TEXTURE_BLACK = "black";

        /**
         * A pure white texture
         */
        public static final String TEXTURE_WHITE = "res/white.png";

        /**
         * A pure blank/alpha texture
         */
        public static final String TEXTURE_BLANK = "res/blank.png";

        /**
         * A pure blue texture (default for normal maps)
         */
        public static final String DEFAULT_NORMAL_MAP = "res/defNormalMap.png";

        /**
         * The diffuse color describes additional color information for diffuse lighting
         */
        public Color diffuseColor = new Color();

        /**
         * Diffuse map, wich contains the color information for the diffuse lighting for
         * the specific pixels
         */
        public String diffuseMap = TEXTURE_WHITE;

        /**
         * Normal map, wich contains the normal clarification for the specific pixels
         */
        public String normalMap = DEFAULT_NORMAL_MAP;

        /**
         * Specular map, wich contains the specific specular lighting clarification for
         * the specific pixel
         */
        public String specularMap = TEXTURE_BLACK;

        /**
         * Displacement map, wich contains the parallax occlusion displacement
         * clarification for the specific pixel
         */
        public String displacementMap = TEXTURE_BLACK;

        /**
         * Ambient occlusion map, wich contains the ambient lighting clarification for
         * the specific pixel
         */
        public String ambientOcclusionMap = TEXTURE_WHITE;

        /**
         * Alpha map, wich contains additionally alpha information
         */
        public String alphaMap = TEXTURE_BLACK;

        /**
         * Reflection cube map, that contains the reflected environment
         */
        public String reflectionMap = TEXTURE_BLACK;

        /**
         * Objects glowing color (black for no glowing)
         */
        public Color glowColor = new Color(0, 0, 0);

        /**
         * Glowing map, wich contains, where the object glows
         */
        public String glowMap = TEXTURE_BLACK;

        /**
         * The displacement factor describes the intensity of the parallax occlusion
         * mapping
         */
        public float displacementFactor = Configuration.getValuef("MATERIAL_DEFAULT_DISPLACEMENT_FACTOR");

        /**
         * The texture tiling, that describes the repeat of the texture
         */
        public float tiling = Configuration.getValuef("MATERIAL_DEFAULT_TEXTURE_TILING");

        /**
         * The shininess describes the intensity of the reflecion for enviroment /*
         * reflection and specular lighting
         */
        public float shininess = Configuration.getValuef("MATERIAL_DEFAULT_SHININESS");

        /**
         * The shine damping describes the damping for specular lighting
         */
        public float shineDamping = Configuration.getValuef("MATERIAL_DEFAULT_SHINE_DAMPING");

        static {

                // Load default textures
                TextureLoader.loadTextureFile(TEXTURE_WHITE, true, GL11.GL_LINEAR, true);
                TextureLoader.loadTextureFile(TEXTURE_BLANK, true, GL11.GL_LINEAR, true);
                TextureLoader.loadTextureFile(DEFAULT_NORMAL_MAP, true, GL11.GL_LINEAR, true);
        }
}
