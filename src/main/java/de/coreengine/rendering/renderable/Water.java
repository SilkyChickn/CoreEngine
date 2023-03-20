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
package de.coreengine.rendering.renderable;

import de.coreengine.framework.Window;
import de.coreengine.rendering.FrameBufferObject;
import de.coreengine.rendering.model.Color;
import de.coreengine.rendering.model.Material;
import de.coreengine.util.Configuration;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

/**
 * Class that represents a renderable waterplate
 *
 * @author Darius Dinger
 */
public class Water {
    private static final float DEFAULT_WAVE_STRENGTH = Configuration.getValuef("WATER_DEFAULT_WAVE_STRENGTH");
    private static final float[] DEFAULT_COLOR = Configuration.getValuefa("WATER_DEFAULT_COLOR");
    private static final float DEFAULT_SOFT_EDGE_DEPTH = Configuration.getValuef("WATER_DEFAULT_SOFT_EDGE_DEPTH");
    private static final float DEFAULT_QUALITY = Configuration.getValuef("WATER_DEFAULT_QUALITY");
    private static final float DEFAULT_TRANSPARENCY = Configuration.getValuef("WATER_DEFAULT_TRANSPARENCY");
    private static final float DEFAULT_SHININESS = Configuration.getValuef("WATER_DEFAULT_SHININESS");
    private static final float DEFAULT_SHINE_DAMPER = Configuration.getValuef("WATER_DEFAULT_SHINE_DAMPER");

    // Transformation matrix of the water
    private Matrix4f transMat = new Matrix4f();

    // Clip plane for rendering refraction/reflection texture
    private Vector4f clipPlane = new Vector4f(0, 1, 0, 0);

    // The transparency (reflection/refraction ratio) of the water
    private float transparency = DEFAULT_TRANSPARENCY;

    // Tiling of the maps
    private float tiling = 1.0f;

    // Map for the distortion calculations
    private String dudvMap = Material.TEXTURE_BLACK;

    // Map for the water normals
    private String normalMap = Material.DEFAULT_NORMAL_MAP;

    // Offset of the dudv/normal map
    private float offset = 0.0f;

    // Strength, the dudv map effects the water
    private float waveStrength = DEFAULT_WAVE_STRENGTH;

    // Transparency of the water (ratio reflection/refraction)
    private float softEdgeDepth = DEFAULT_SOFT_EDGE_DEPTH;

    // Shininess of the water for specular lighting
    private float shininess = DEFAULT_SHININESS;

    // Shine damping of the water for specular lighting
    private float shineDamper = DEFAULT_SHINE_DAMPER;

    // Color of the water
    private final Color multiplicativeColor = new Color();
    private final Color additiveColor = new Color(0, 0, 0);

    // Water Quality (0 - 1)
    private float quality = DEFAULT_QUALITY;

    // Fbos to render reflection and refraction texture in
    private FrameBufferObject reflectionFbo;
    private FrameBufferObject refractionFbo;

    // Enable disable reflection or refraction
    private boolean reflectionEnabled = true;
    private boolean refractionEnabled = true;

    /**
     * Creating new water
     */
    public Water() {
        multiplicativeColor.setRed(DEFAULT_COLOR[0]);
        multiplicativeColor.setGreen(DEFAULT_COLOR[1]);
        multiplicativeColor.setBlue(DEFAULT_COLOR[2]);

        recreateFbos();
        Window.addWindowListener((x, y, aspect) -> recreateFbos());

        transMat.setIdentity();
    }

    /**
     * (Re)creating water reflection/refraction fbos
     */
    private void recreateFbos() {
        reflectionFbo = new FrameBufferObject((int) (Window.getWidth() * quality), (int) (Window.getHeight() * quality),
                false);
        refractionFbo = new FrameBufferObject((int) (Window.getWidth() * quality), (int) (Window.getHeight() * quality),
                false);
    }

    /**
     * Setting waters quality be rescaling the fraction and reflection texture. The
     * rescaling is the WindowResolution * quality. So 1.0f is the best quality and
     * to 0.0f it gets more pixelated.<br>
     * This action take performance so dont call it in the game loop!
     * 
     * @param quality New water quality
     */
    public void setQuality(float quality) {
        this.quality = quality;
        recreateFbos();
    }

    /**
     * @return Waters dudv map for distortion
     */
    public String getDudvMap() {
        return dudvMap;
    }

    /**
     * @return Waters normal map
     */
    public String getNormalMap() {
        return normalMap;
    }

    /**
     * @return Tiling for the maps (dudv/normal)
     */
    public float getTiling() {
        return tiling;
    }

    /**
     * @param offset New offset of the dudv/normal map for wave animation
     */
    public void setOffset(float offset) {
        this.offset = offset;
    }

    /**
     * @return Offset of the dudv/normal map for wave animation
     */
    public float getOffset() {
        return offset;
    }

    /**
     * @return Fbo to render reflection into
     */
    public FrameBufferObject getReflectionFbo() {
        return reflectionFbo;
    }

    /**
     * @return Fbo to render refraction into
     */
    public FrameBufferObject getRefractionFbo() {
        return refractionFbo;
    }

    /**
     * @return Read/writeable multiplicative color part of the water.
     *         (watercolor = multiplicativeColor * color + additiveColor)
     */
    public Color getMultiplicativeColor() {
        return multiplicativeColor;
    }

    /**
     * @return Read/writeable additive color part of the water.
     *         (color(x) = multiplicativeColor * x + additiveColor)
     */
    public Color getAdditiveColor() {
        return additiveColor;
    }

    /**
     * @return Depth to begin softing edge
     */
    public float getSoftEdgeDepth() {
        return softEdgeDepth;
    }

    /**
     * @return Strength, the dudv map effects the water
     */
    public float getWaveStrength() {
        return waveStrength;
    }

    /**
     * @param dudvMap New dudv map of the water
     */
    public void setDudvMap(String dudvMap) {
        this.dudvMap = dudvMap;
    }

    /**
     * @param normalMap New normal map of the water
     */
    public void setNormalMap(String normalMap) {
        this.normalMap = normalMap;
    }

    /**
     * @param tiling New tiling of the waters dudv/normal map
     */
    public void setTiling(float tiling) {
        this.tiling = tiling;
    }

    /**
     * @param sed New depth to begin softing edge
     */
    public void setSoftEdgeDepth(float sed) {
        this.softEdgeDepth = sed;
    }

    /**
     * @param waveStrength New strength, the dudv map effects the water
     */
    public void setWaveStrength(float waveStrength) {
        this.waveStrength = waveStrength;
    }

    /**
     * Sets the water horizontal scale/size
     * 
     * @param scale New scale of the water
     */
    public void setScale(float scale) {
        transMat.setScale(scale);
    }

    /**
     * Sets the x position of the waters (0, 0) point
     * 
     * @param x New x position
     */
    public void setX(float x) {
        transMat.m03 = x;
    }

    /**
     * Sets the y position of the waters (0, 0) point (sea level)
     * 
     * @param y New y position
     */
    public void setY(float y) {
        transMat.m13 = y;
        clipPlane.w = y;
    }

    /**
     * Sets the z position of the waters (0, 0) point
     * 
     * @param z New z position
     */
    public void setZ(float z) {
        transMat.m23 = z;
    }

    /**
     * @return Waters current transformation matrix
     */
    public Matrix4f getTransMat() {
        return transMat;
    }

    /**
     * @return Horizontal scale/size of the water
     */
    public float getScale() {
        return transMat.m00;
    }

    /**
     * @return X position of the water
     */
    public float getX() {
        return transMat.m03;
    }

    /**
     * @return Y position of the water
     */
    public float getY() {
        return transMat.m13;
    }

    /**
     * @return Z position of the water
     */
    public float getZ() {
        return transMat.m23;
    }

    /**
     * @return Clip plane for rendering refraction/reflection texture
     */
    public Vector4f getClipPlane() {
        return clipPlane;
    }

    /**
     * Setting the waters transparency (ration of reflectiona dn refraction). If the
     * value is 1.0 the mix is 50/50. Under 1 is more transparent, over 1 is more
     * reflective.
     * 
     * @return Current waters transparency
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Setting the waters transparency (ration of reflectiona dn refraction). If the
     * value is 1.0 the mix is 50/50. Under 1 is more transparent, over 1 is more
     * reflective.
     * 
     * @param transparency New transparency
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    /**
     * Get shine damper for specular lighting
     * 
     * @return Water shine damper
     */
    public float getShineDamper() {
        return shineDamper;
    }

    /**
     * Set shine damper for specular lighting
     * 
     * @param shineDamper Water shine damper
     */
    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    /**
     * Get shininess for specular lighting
     * 
     * @return Water shininess
     */
    public float getShininess() {
        return shininess;
    }

    /**
     * Set shininess for specular lighting
     * 
     * @param shininess Water shininess
     */
    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    /**
     * Enable or disable reflection. So the water will reflect the environment. This
     * will have effect on the performance.
     * 
     * @param reflectionEnabled Should reflection be enabled
     */
    public void setReflectionEnabled(boolean reflectionEnabled) {
        this.reflectionEnabled = reflectionEnabled;
    }

    /**
     * Enable or disable refraction. So the water will refract theenvironment under
     * the water. This
     * will have effect on the performance.
     * 
     * @param reflectionEnabled Should refraction be enabled
     */
    public void setRefractionEnabled(boolean refractionEnabled) {
        this.refractionEnabled = refractionEnabled;
    }

    /**
     * @return Is reflection of the water enabled
     */
    public boolean isReflectionEnabled() {
        return reflectionEnabled;
    }

    /**
     * @return Is refraction of the water enabled
     */
    public boolean isRefractionEnabled() {
        return refractionEnabled;
    }
}
