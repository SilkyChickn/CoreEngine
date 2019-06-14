/*
 * Copyright (c) 2019, Darius Dinger
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.coreengine.rendering.renderable.light;

import de.coreengine.util.Configuration;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**Class that represents a spot light, basical a point light with a specific
 * direction and light cone angle
 *
 * @author Darius Dinger
 */
public class SpotLight extends PointLight {
    private static final float DEFAULT_INNER_LIGHT_CONE = 
            Configuration.getValuef("LIGHT_DEFAULT_INNER_LIGHT_CONE");
    private static final float DEFAULT_OUTER_LIGHT_CONE = 
            Configuration.getValuef("LIGHT_DEFAULT_OUTER_LIGHT_CONE");
    
    //Direction, the light is pointing
    private Vector3f direction = new Vector3f();
    
    //Angle of the light cone
    private Vector2f lightCone = new Vector2f(DEFAULT_INNER_LIGHT_CONE, 
            DEFAULT_OUTER_LIGHT_CONE);
    
    /**Getting the direction, the light is pointing to as vector (x, y, z).<br>
     * f.e. (0, 1, 0) is pointing upwards.
     * 
     * @return Direction of the light
     */
    public Vector3f getDirection() {
        return direction;
    }
    
    /**Getting the light cone of the spotlight as vec2f for read and writing. 
     * The x value is the inner light cone and the y value is the outer light 
     * cone. Tha light fades between the inner and outer cone out.<br>
     * 90 degrees = 0.0 | 0 degrees = 1.0.
     * 
     * @return Getting the angle of the spotlights cone
     */
    public Vector2f getLightCone() {
        return lightCone;
    }
}
