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
package io.github.suuirad.coreengine.system.gameObjects;

import io.github.suuirad.coreengine.rendering.model.Color;
import io.github.suuirad.coreengine.rendering.renderable.Moon;
import io.github.suuirad.coreengine.rendering.renderable.Skybox;
import io.github.suuirad.coreengine.rendering.renderable.Sun;
import io.github.suuirad.coreengine.rendering.renderable.light.AmbientLight;
import io.github.suuirad.coreengine.rendering.renderer.MasterRenderer;
import io.github.suuirad.coreengine.rendering.renderer.PostProcesser;
import io.github.suuirad.coreengine.rendering.renderer.ppeffects.FogEffect;
import io.github.suuirad.coreengine.system.GameObject;
import io.github.suuirad.coreengine.util.Configuration;
import io.github.suuirad.coreengine.util.FrameTimer;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

/**Class that can simulate a day night cycle with skybox, fog and sun
 *
 * @author Darius Dinger
 */
public class DayNightCycle extends GameObject{
    private static final float[] DEFAULT_CAPS = 
            Configuration.getValuefa("DAY_NIGHT_DEFAULT_CAPS");
    private static final float DEFAULT_RADIUS = 
            Configuration.getValuef("DAY_NIGHT_DEFAULT_RADIUS");
    private static final float DEFAULT_SPEED = 
            Configuration.getValuef("DAY_NIGHT_DEFAULT_SPEED");
    private static final float DEFAULT_SKYBOX_ROTATION_SPEED = 
            Configuration.getValuef("DAY_NIGHT_DEFAULT_SKYBOX_ROTATION_SPEED");
    
    //Rotation of the skybox
    private float skyboxRotation = 0.0f;
    private float skyboxRotSpeed = DEFAULT_SKYBOX_ROTATION_SPEED;
    
    //Radius, speed and center of the sun circle
    private float radius = DEFAULT_RADIUS, Speed = DEFAULT_SPEED;
    private Vector3f center = new Vector3f();
    
    //Current sun and moon position in the circle in degrees
    private float curDegreesSun = 0.0f;
    private float curDegreesMoon = 180.0f;
    
    //Components of the dy night cycle environment
    private Sun sun = new Sun();
    private Moon moon = new Moon();
    private Skybox skybox = new Skybox();
    private AmbientLight ambientLight = new AmbientLight();
    private FogEffect fog = new FogEffect();
    
    //Skybox unit ids
    private int skyDayId, skyEveId, skyNigId;
    
    //Colors of the time
    private final Color dayColor;
    private final Color eveningColor;
    private final Color nightColor;
    
    //Caps, when to switch time
    private Vector4f caps = new Vector4f(DEFAULT_CAPS);
    
    /**Creating new day night cycle
     * 
     * @param dayTex Day skybox cube map texture
     * @param dayColor Color of the Sun and fog at day
     * @param eveTex Evening skybox cube map texture
     * @param eveColor Color of the Sun and fog at evening
     * @param nigTex Night skybox cube map texture
     * @param nigColor Color of the Sun and fog at night
     * @param sunTexture Texture of the sun
     * @param moonTexture Texture of the moon
     */
    public DayNightCycle(int dayTex, Color dayColor, int eveTex, Color eveColor, 
            int nigTex, Color nigColor, int sunTexture, int moonTexture) {
        
        skyDayId = skybox.addCubeMapTexture(dayTex, 0.0f);
        skyEveId = skybox.addCubeMapTexture(eveTex, 0.0f);
        skyNigId = skybox.addCubeMapTexture(nigTex, 0.0f);
        
        this.dayColor = dayColor;
        this.eveningColor = eveColor;
        this.nightColor = nigColor;
        
        sun.setTexture(sunTexture);
        moon.setTexture(moonTexture);
    }
    
    @Override
    public void onUpdate() {
        
        //Rotate skybox
        if(skyboxRotSpeed != 0){
            skyboxRotation = (skyboxRotation +(skyboxRotSpeed * FrameTimer.getTslf())) % 360.0f;
            skybox.setRotation(skyboxRotation);
        }
        
        //Moving sun andmoon
        curDegreesSun = (curDegreesSun + Speed * FrameTimer.getTslf()) % 360.0f;
        curDegreesMoon = (curDegreesSun +180.0f) % 360.0f;
        
        //Calc sun position on sun circle
        float rad = (float) Math.toRadians(curDegreesSun);
        float x = center.x +(radius * (float) Math.cos(rad));
        float y = center.y +(radius * (float) Math.sin(rad));
        float z = center.z;
        sun.getPosition().set(x, y, z);
        
        rad = (float) Math.toRadians(curDegreesMoon);
        x = center.x +(radius * (float) Math.cos(rad));
        y = center.y +(radius * (float) Math.sin(rad));
        z = center.z;
        moon.getPosition().set(x, y, z);
        
        float dayFactor = 0, eveningFactor = 0, nightFactor = 0;
        
        //Calculate factors (wich day time)
        float suny = (sun.getPosition().y -center.y) / radius * 2.0f;
        if(suny > caps.x){
            dayFactor = 1.0f;
            eveningFactor = 0.0f;
            nightFactor = 0.0f;
        }else if(suny < caps.w){
            dayFactor = 0.0f;
            eveningFactor = 0.0f;
            nightFactor = 1.0f;
        }else if(suny < caps.y && suny > caps.z){
            dayFactor = 0.0f;
            eveningFactor = 1.0f;
            nightFactor = 0.0f;
        }else if(suny < caps.x && suny > caps.y){
            float blend = (suny -caps.y) / (caps.x -caps.y);
            dayFactor = blend;
            eveningFactor = 1.0f -blend;
            nightFactor = 0.0f;
        }else if(suny < caps.z && suny > caps.w){
            float blend = (suny -caps.w) / (caps.z -caps.w);
            dayFactor = 0;
            eveningFactor = blend;
            nightFactor = 1.0f -blend;
        }
        
        //Setting fog color
        fog.getColor().setRed(
                dayFactor * dayColor.getRed()+
                eveningFactor * eveningColor.getRed() +
                nightFactor * nightColor.getRed()
        );
        fog.getColor().setGreen(
                dayFactor * dayColor.getGreen()+
                eveningFactor * eveningColor.getGreen() +
                nightFactor * nightColor.getGreen()
        );
        fog.getColor().setBlue(
                dayFactor * dayColor.getBlue()+
                eveningFactor * eveningColor.getBlue() +
                nightFactor * nightColor.getBlue()
        );
        
        //Setting skybox textures
        skybox.setBlendingFactor(skyDayId, dayFactor);
        skybox.setBlendingFactor(skyEveId, eveningFactor);
        skybox.setBlendingFactor(skyNigId, nightFactor);
        
        //Setting ambient light
        ambientLight.getColor().setRed(fog.getColor().getRed());
        ambientLight.getColor().setGreen(fog.getColor().getGreen());
        ambientLight.getColor().setBlue(fog.getColor().getBlue());
        
        //Setting suns visibility
        sun.setLensFlareEnabled(sun.getPosition().y > center.y);
        if(!sun.isLensFlareEnabled()) sun.setIntensity(10.0f);
        else sun.setIntensity(0.75f);
    }
    
    @Override
    public void onRender() {
        MasterRenderer.setSun(sun);
        MasterRenderer.setMoon(moon);
        MasterRenderer.setSkybox(skybox);
        MasterRenderer.renderAmbientLight(ambientLight);
        PostProcesser.addEffect(fog);
    }
    
    /**Getting read/writeable vec4f with the day/night caps. The values are
     * between 0.0f and 1.0f.<br>
     * 
     * X value = Lower day<br>
     * Y value = Upper evening<br>
     * Z value = Lower evening<br>
     * W value = Upper night<br>
     * 
     * @return Read/writeable vec4f with the day/night caps
     */
    public Vector4f getCaps() {
        return caps;
    }
    
    /**@return Read/Writeable sun of the day night cycle
     */
    public Sun getSun() {
        return sun;
    }
    
    /**@return Read/Writeable moon of the day night cycle
     */
    public Moon getMoon() {
        return moon;
    }
    
    /**@return Read/Writeable fog of the day night cycle
     */
    public FogEffect getFog() {
        return fog;
    }
    
    /**@return Read/Writeable skybox of the day night cycle
     */
    public Skybox getSkybox() {
        return skybox;
    }
    
    /**@param Speed New movement speed of the sun
     */
    public void setSpeed(float Speed) {
        this.Speed = Speed;
    }
    
    /**Setting the center of the sun cycle. It makes sense to set this to the
     * camera position, sothe sun rotates around the camera.
     * 
     * @param center Center position to rotate around
     */
    public void setCenter(Vector3f center) {
        this.center = center;
    }
    
    /**@return Write/readable center of the sun as 3d vector
     */
    public Vector3f getCenter() {
        return center;
    }
    
    /**@return Is currently day or night
     */
    public boolean isDay(){
        return sun.isLensFlareEnabled();
    }
}
