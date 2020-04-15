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

import com.bulletphysics.collision.shapes.CollisionShape;
import de.coreengine.rendering.renderable.Grassland;
import de.coreengine.util.Configuration;
import de.coreengine.util.bullet.TerrainShapeCreator;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**
 * Class that represents a terrain in the scene
 *
 * @author Darius Dinger
 */
public class Terrain {

    // Default terrain horizontal scale
    private static final float DEFAULT_SIZE = Configuration.getValuef("TERRAIN_DEFAULT_SIZE");

    // Terrain root nodes that defining the terrain quad tree
    private final TerrainNode terrainQuadtree;

    // Terrains transformation matrix for scale and position
    private final Matrix4f transMat;

    // Terrains configuration
    private TerrainConfig config = new TerrainConfig();

    // Grassland of the terrain
    private Grassland grassland = new Grassland();

    // Should grassland be rendered/updated
    private boolean grasslandEnabled = false;

    // Collision shape of the terrain
    private CollisionShape shape;

    /**
     * Creates a new terrain with default values, defined in the configuration file
     */
    public Terrain() {

        // Prepare transformation matrix and set default size
        transMat = new Matrix4f();
        transMat.setIdentity();
        transMat.setScale(DEFAULT_SIZE);

        // Create quadtree/root node
        terrainQuadtree = new TerrainNode(null, null, new Vector2f(0, 0), 1.0f, 0, this);
    }

    /**
     * Setting the new configuration of the terrain
     * 
     * @param config New configuration
     */
    public void setConfig(TerrainConfig config) {
        this.config = config;
    }

    /**
     * @return Currents terrain configuration
     */
    public TerrainConfig getConfig() {
        return config;
    }

    /**
     * Returning the terrain quadtree (the root nodes of the tree)
     * 
     * @return Terrain root nodes
     */
    public TerrainNode getTerrainQuadtree() {
        return terrainQuadtree;
    }

    /**
     * Aligning the terrain quadtree to the position pos. At pos the terrain has the
     * highest lod. From pos the lod decreases.
     * 
     * @param pos Position to align to
     */
    public void alignTo(Vector3f pos) {
        terrainQuadtree.alignTo(pos);
    }

    /**
     * Sets the terrain horizontal scale/size
     * 
     * @param scale New scale of the terrain
     */
    public void setScale(float scale) {
        transMat.setScale(scale);
    }

    /**
     * Sets the x position of the terrains (0, 0) point
     * 
     * @param x New x position
     */
    public void setX(float x) {
        transMat.m03 = x;
    }

    /**
     * Sets the y position of the terrains (0, 0) point
     * 
     * @param y New y position
     */
    public void setY(float y) {
        transMat.m13 = y;
    }

    /**
     * Sets the z position of the terrains (0, 0) point
     * 
     * @param z New z position
     */
    public void setZ(float z) {
        transMat.m23 = z;
    }

    /**
     * @return Terrains current transformation matrix
     */
    public Matrix4f getTransMat() {
        return transMat;
    }

    /**
     * @return Horizontal scale/size of the terrain
     */
    public float getScale() {
        return transMat.m00;
    }

    /**
     * @return X position of the terrain
     */
    public float getX() {
        return transMat.m03;
    }

    /**
     * @return Y position of the terrain
     */
    public float getY() {
        return transMat.m13;
    }

    /**
     * @return Z position of the terrain
     */
    public float getZ() {
        return transMat.m23;
    }

    /**
     * @return Grassland of the terrain
     */
    public Grassland getGrassland() {
        return grassland;
    }

    /**
     * @return Should grassland be rendered
     */
    public boolean isGrasslandEnabled() {
        return grasslandEnabled;
    }

    /**
     * @param grasslandEnabled Should grassland be rendered/updated
     */
    public void setGrasslandEnabled(boolean grasslandEnabled) {
        this.grasslandEnabled = grasslandEnabled;
    }

    /**
     * @return Collision shpe of the terrain. Only use in static rigid bodys!
     */
    public CollisionShape getShape() {
        return shape;
    }

    /**
     * Recalculate the terrain collision shape
     * 
     * @param resolution Number of quads per row/column of the shape
     */
    public void recalcCollisionShape(int resolution) {
        shape = TerrainShapeCreator.createTerrainShape(this, resolution);
    }
}
