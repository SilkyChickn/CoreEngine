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
package de.coreengine.rendering.renderable.terrain;

import de.coreengine.asset.TextureData;
import de.coreengine.rendering.model.Material;
import de.coreengine.util.Configuration;

import javax.vecmath.Vector3f;

/**
 * Class that represent a configuration for a terrain
 *
 * @author Darius Dinger
 */
public class TerrainConfig {

    // Default terrain lod stage areas
    private static final float[] DEFAULT_LOD_RANGES = Configuration.getValuefa("TERRAIN_DEFAULT_LOD_RANGES");

    // Default height scaling of the terrain
    private static final float DEFAULT_AMPLITUDE = Configuration.getValuef("TERRAIN_DEFAULT_AMPLITUDE");

    // Default tesselation settings
    private static final float DEFAULT_TESS_FACTOR = Configuration.getValuef("TERRAIN_DEFAULT_TESS_FACTOR");

    private static final float DEFAULT_TESS_RANGE = Configuration.getValuef("TERRAIN_DEFAULT_TESS_RANGE");

    private static final float DEFAULT_TESS_GRADIENT = Configuration.getValuef("TERRAIN_DEFAULT_TESS_GRADIENT");

    // Terrain texturepack that contains all materials of the terrain
    private TerrainTexturePack texturePack = new TerrainTexturePack();

    // Lightmap of the terrain that contains the normals for faster lighting
    // calculation
    private String lightMap = Material.DEFAULT_NORMAL_MAP;

    // The heightmap contains the height at the specific points
    private TextureData heightMap = new TextureData();

    // The blend map contains, wich texture should mapped on wich point on the
    // terrain
    private String blendMap = Material.TEXTURE_BLACK;

    // Terrains lod stage area sizes
    private float[] lodRanges = DEFAULT_LOD_RANGES.clone();

    // Terrains vertical scaling in terrain space
    private float amplitude = DEFAULT_AMPLITUDE;

    // Tesselation area (x = tessFactor, y = tessRange, z = tessGradient)
    private Vector3f tesselationArea = new Vector3f(DEFAULT_TESS_FACTOR, DEFAULT_TESS_RANGE, DEFAULT_TESS_GRADIENT);

    /**
     * Creates new default terrain config
     */
    public TerrainConfig() {
        heightMap.width = 0;
        heightMap.height = 0;
        heightMap.data = null;
        heightMap.key = Material.TEXTURE_BLACK;
    }

    /**
     * Getting this terrain lod stages as float[]
     * 
     * @return Terrains lod stage area sizes
     */
    float[] getLodRanges() {
        return lodRanges;
    }

    /**
     * Setting this terrains lod stage area ranges.<br>
     * lodRanges does not have to be null or empty, else it will rejected
     * 
     * @param lodRanges New ranges of the lod stages
     */
    public void setLodRanges(float[] lodRanges) {
        if (lodRanges == null || lodRanges.length < 1)
            return;
        this.lodRanges = lodRanges;
    }

    /**
     * @return Terrain texturepack that contains all materials of the terrain
     */
    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    /**
     * Setting the new texture pack of the terrain
     * 
     * @param texturePack New texture pack
     */
    public void setTexturePack(TerrainTexturePack texturePack) {
        this.texturePack = texturePack;
    }

    /**
     * @return Lightmap of the terrain that contains the normals for faster lighting
     *         calculation
     */
    public String getLightMap() {
        return lightMap;
    }

    /**
     * Setting the new light map of the terrain
     * 
     * @param lightMap New light map
     */
    public void setLightMap(String lightMap) {
        this.lightMap = lightMap;
    }

    /**
     * @return The heightmap contains the height at the specific points
     */
    public TextureData getHeightMap() {
        return heightMap;
    }

    /**
     * Setting the new height map of the terrain
     * 
     * @param heightMap New height map
     */
    public void setHeightMap(TextureData heightMap) {
        this.heightMap = heightMap;
    }

    /**
     * @return The blend map contains, wich texture should mapped on wich point on
     *         the terrain
     */
    public String getBlendMap() {
        return blendMap;
    }

    /**
     * Setting the new blend map of the terrain
     * 
     * @param blendMap New blend map
     */
    public void setBlendMap(String blendMap) {
        this.blendMap = blendMap;
    }

    /**
     * Setting the vertical scaling of the terrain in world space.<br>
     * The terrain max height in world space will be scale * amplitude + y. Can be
     * calculated by Amplitude = (TerrainMaxHeight -TerrainY) / TerrainSize.
     * 
     * @param amplitude New terrain vertical scaling in terrain space
     */
    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    /**
     * Returning the current vertical scaling of the terrain in terrain space.<br>
     * The terrain max height in world space will be scale * amplitude + y.
     * 
     * @return Terrain vertical scaling in terrain space
     */
    public float getAmplitude() {
        return amplitude;
    }

    /**
     * Getting the terrains current tesselation area as 3d vector. The x value
     * describes the maximum tesselation, the y value describes the range of the
     * tesselation and the z value describes the tesselation gradient. The formula
     * for the terrain tesselation is:<br>
     * t(x) = tessFactor * e^-(tessDensity * x)^tessGradient
     * 
     * @return Terrains current tesselation area as 3d vector
     */
    public Vector3f getTesselationArea() {
        return tesselationArea;
    }

    /**
     * Setting the terrains current tesselation attenuations. They must be greater
     * than 0, else the change will rejected. The formula for the terrain
     * tesselation is:<br>
     * t(x) = tessFactor * e^-(tessDensity * x)^tessGradient
     * 
     * @param tessFactor   New tesselation Factor (Max tesselation)
     * @param tessRange    New tesselation density (Range)
     * @param dessGradient New tesselation gradient (Attenuation)
     */
    public void setTesselationAttenuation(float tessFactor, float tessRange, float dessGradient) {
        if (tessFactor > 0)
            this.tesselationArea.x = (tessFactor);
        if (tessRange > 0)
            this.tesselationArea.y = (tessRange);
        if (dessGradient > 0)
            this.tesselationArea.z = (dessGradient);
    }
}
