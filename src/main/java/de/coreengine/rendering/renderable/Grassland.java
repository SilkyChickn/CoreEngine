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

import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.model.SimpleModel;
import de.coreengine.util.Configuration;

import javax.vecmath.Vector2f;

/**Class that represent a grassland for a terrain
 *
 * @author Darius Dinger
 */
public class Grassland {
    private static final int DEFAULT_DENSITY = 
            Configuration.getValuei("GRASSLAND_DEFAULT_DENSITY");
    private static final float DEFAULT_DISTANCE = 
            Configuration.getValuef("GRASSLAND_DEFAULT_DISTANCE");
    private static final float DEFAULT_RANGE = 
            Configuration.getValuef("GRASSLAND_DEFAULT_RANGE");
    private static final float DEFAULT_GRADIENT = 
            Configuration.getValuef("GRASSLAND_DEFAULT_GRADIENT");
    private static final float DEFAULT_WIND_INTENSITIVITY = 
            Configuration.getValuef("GRASSLAND_DEFAULT_WIND_INTENSITIVITY");
    
    //Mesh that contains the grass blade positions and its scale
    private SimpleModel mesh = null;
    private float tuftScale = 0.0f;
    
    //Intesitivity of the wind map
    private float windIntensitivity = DEFAULT_WIND_INTENSITIVITY;
    
    //Grass tufts in a row/column
    private int density = DEFAULT_DENSITY;
    
    //Distance between two gras tufts
    private float distance = DEFAULT_DISTANCE;
    
    //Area of the grassland around the player
    private final Vector2f area = new Vector2f(DEFAULT_RANGE, DEFAULT_GRADIENT);
    
    //Map that contains the locations, where grass to draw, and color
    private int densityMap = Material.TEXTURE_BLANK;
    
    //Map that contains the grass vector transformation by wind
    private int windMap = Material.TEXTURE_BLACK;
    
    //Tiling for the windmap
    private float windMapTiling = 1.0f;
    
    //Offset for the wind map
    private float windOffset = 0;
    
    /**Getting the area of the grassland around the player as 2d vector. 
     * The x value is the range and the y value is the gradient. The grassblade 
     * visibility will be calculated by this formula:<br>
     * g(x) = e^-(range * x)^gradient
     * 
     * @return Area of the grassland
     */
    public Vector2f getArea() {
        return area;
    }
    
    /**Returning the densitymap for this grassland. Darker means smaller grass
     * and lighter means bigger gras. Black is no gras.
     * 
     * @return Density of the grassland
     */
    public int getDensityMap() {
        return densityMap;
    }
    
    /**@return Mesh that contains the grass blade positions
     */
    public SimpleModel getMesh() {
        return mesh;
    }
    
    /**@return Scale of the tuft mesh
     */
    public float getTuftScale() {
        return tuftScale;
    }
    
    /**@param mesh New gras mesh
     * @param scale Scale of the tuft mesh
     */
    public void setMesh(SimpleModel mesh, float scale) {
        this.mesh = mesh;
        this.tuftScale = scale;
    }
    
    /**@return Map that contains the grass vector transformation by wind
     */
    public int getWindMap() {
        return windMap;
    }
    
    /**Setting the densitymap for this grassland. Darker means smaller grass
     * and lighter means bigger gras. Black is no gras.
     * 
     * @param density New densitymap
     */
    public void setDensityMap(int density) {
        this.densityMap = density;
    }
    
    /**Setting map that contains the grass vector transformation by wind
     * 
     * @param windMap New windmap
     */
    public void setWindMap(int windMap) {
        this.windMap = windMap;
    }
    
    /**Setting the area of the grassland around the player. 
     * The grassblade visibility will be calculated by this formula:<br>
     * g(x) = e^-(range * x)^gradient
     * 
     * @param range Range of the grassland
     * @param gradient Gradient at the grassland end
     */
    public void setArea(float range, float gradient) {
        this.area.x = (range);
        this.area.y = (gradient);
    }
    
    /**Setting the density for the grassland model. If the max range is increasing,
     * the density is decreasing and must be reconfigured.
     * 
     * @param density New mesh density
     */
    public void setDensity(int density){
        this.density = density;
    }
    
    /**@param windOffset New offset of the windmap over the terrain
     */
    public void setWindOffset(float windOffset) {
        this.windOffset = windOffset;
    }
    
    /**@return Offset of the windmap over the terrain
     */
    public float getWindOffset() {
        return windOffset;
    }
    
    /**@return Intensitivity of the windmap onto the blade vectors
     */
    public float getWindIntensitivity() {
        return windIntensitivity;
    }
    
    /**Setting the intensiveness, the windmap effects the grass blades
     * 
     * @param windIntensitivity New intensiveness
     */
    public void setWindIntensitivity(float windIntensitivity) {
        this.windIntensitivity = windIntensitivity;
    }
    
    /**@return Density of the gras (gras Per Row/Column)
     */
    public int getDensity() {
        return density;
    }
    
    /**@return Distance between two grass tufts
     */
    public float getDistance() {
        return distance;
    }
    
    /**@param distance Distance between two grass tufts
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }
    
    /**@return Tiling for the windmap
     */
    public float getWindMapTiling() {
        return windMapTiling;
    }
    
    /**@param windMapTiling New tiling for the windmap
     */
    public void setWindMapTiling(float windMapTiling) {
        this.windMapTiling = windMapTiling;
    }
}
