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
package io.github.suuirad.coreengine.rendering.renderer;

import io.github.suuirad.coreengine.framework.Keyboard;
import io.github.suuirad.coreengine.framework.Mouse;
import io.github.suuirad.coreengine.framework.Window;
import io.github.suuirad.coreengine.rendering.GBuffer;
import io.github.suuirad.coreengine.rendering.model.Color;
import io.github.suuirad.coreengine.rendering.renderable.*;
import io.github.suuirad.coreengine.rendering.renderable.gui.GUIPane;
import io.github.suuirad.coreengine.rendering.renderable.light.AmbientLight;
import io.github.suuirad.coreengine.rendering.renderable.light.DirectionalLight;
import io.github.suuirad.coreengine.rendering.renderable.light.PointLight;
import io.github.suuirad.coreengine.rendering.renderable.light.SpotLight;
import io.github.suuirad.coreengine.rendering.renderable.terrain.Terrain;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.vecmath.Vector4f;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**Class that manage the whole rendering system and is the contact class for 
 * rendering stuff onto the screen
 *
 * @author Darius Dinger
 */
public class MasterRenderer {
    private static Vector4f CLIP_PLANE_RENDER_ALL = new Vector4f(0, 1, 0, 999999);
    
    //GBuffer to render into
    private static GBuffer GBUFFER;
    private static GBuffer OUTPUT_GBUFFER;
    
    //Mouse picking
    private static final Color PICKED_COLOR = new Color();
    private static final FloatBuffer PICK_DATA = BufferUtils.createFloatBuffer(4);
    
    //All renderer instances
    private static final TerrainRenderer TERRAIN_RENDERER = new TerrainRenderer();
    private static final DeferredRenderer DEFFERED_RENDERER = new DeferredRenderer();
    private static final GrasslandRenderer GRASSLAND_RENDERER = new GrasslandRenderer();
    private static final WaterRenderer WATER_RENDERER = new WaterRenderer();
    private static final GUIRenderer GUI_RENDERER = new GUIRenderer();
    private static final SunMoonRenderer SUN_RENDERER = new SunMoonRenderer();
    private static final LensFlareRenderer LENS_FLARE_RENDERER = new LensFlareRenderer();
    private static final SkyboxRenderer SKYBOX_RENDERER = new SkyboxRenderer();
    private static final EntityRenderer ENTITY_RENDERER = new EntityRenderer();
    private static final FontRenderer FONT_RENDERER = new FontRenderer();
    private static final ParticleRenderer PARTICLE_RENDERER = new ParticleRenderer();
    
    //Singleton render stuff
    private static Camera camera = new Camera();
    private static Sun sun = null;
    private static Moon moon = null;
    private static LensFlare lensFlare = null;
    private static Skybox skybox = null;
    
    //Lists/maps that contains the stuff to render in the next frame
    private static final List<Entity> ENTITIES = new LinkedList<>();
    private static final HashMap<Integer, List<Particle>> PARTICLES = new HashMap<>();
    private static final List<Terrain> TERRAINS = new LinkedList<>();
    private static final List<Water> WATERS = new LinkedList<>();
    private static final List<GUIPane> GUIS_2D = new LinkedList<>();
    private static final List<GUIPane> GUIS_3D = new LinkedList<>();
    
    //Lists of lights to render in the next frame
    private static final List<PointLight> POINT_LIGHTS = new LinkedList<>();
    private static final List<SpotLight> SPOT_LIGHTS = new LinkedList<>();
    private static final List<AmbientLight> AMBIENT_LIGHTS = new LinkedList<>();
    private static final List<DirectionalLight> DIRECTIONAL_LIGHTS = new LinkedList<>();
    
    /**Initialize the master renderer
     */
    public static void init(){
        recreateGBuffers();
        Window.addWindowListener((int x, int y, float aspect) -> recreateGBuffers());
        
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
        
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glClearColor(0, 0, 0, 1);
    }
    
    /**(Re)creating the render gbuffers
     */
    private static void recreateGBuffers(){
        GBUFFER = new GBuffer();
        OUTPUT_GBUFFER = new GBuffer();
    }
    
    /**Rendering all from the renderlists and clear renderlists
     */
    public static void render(){
        
        //Adding sun light sources to lights
        if(sun != null)
            sun.addLights();
        
        //Adding moon light sources to lights
        if(moon != null)
            moon.addLights();
        
        preRender();
        
        //DEBUG - ENABLE LINE RENDER MODE
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_P)){
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }else{
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }
        
        render3D();
        
        //DEBUG - DISABLE LINE RENDER MODE
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL); //DEBUG - ENABLE LINE RENDER MODE
        
        postProcess();
        
        render2D();
        
        getPickColor();
    }
    
    /**Prerender stuff like water reflections and refractions, shadow maps, 
     * relfection cubemaps etc.
     */
    private static void preRender(){
        
        //Prerender waters reflection and refraction textures
        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
        WATERS.forEach((w) -> {
            
            w.getClipPlane().y = (1);
            float clipDistance = w.getClipPlane().w;
            w.getClipPlane().w = (-clipDistance +0.15f);
            float camMoveDistance = 2 * (camera.getPosition().y -w.getY());
            camera.setY(camera.getPosition().y -camMoveDistance);
            camera.setPitch(-camera.getPitch());
            camera.recalcViewMatrix();
            camera.recalcViewProjectionMatrix();
            
            w.getReflectionFbo().bind(GL30.GL_COLOR_ATTACHMENT0);
            clear();
            TERRAIN_RENDERER.render(TERRAINS, camera, w.getClipPlane()); 
            ENTITY_RENDERER.render(ENTITIES, camera, w.getClipPlane());
            w.getReflectionFbo().unbind();
            
            w.getClipPlane().y = (-1);
            w.getClipPlane().w = (clipDistance +0.15f);
            camera.setY(camera.getPosition().y +camMoveDistance);
            camera.setPitch(-camera.getPitch());
            camera.recalcViewMatrix();
            camera.recalcViewProjectionMatrix();
            
            w.getRefractionFbo().bind(GL30.GL_COLOR_ATTACHMENT0);
            clear();
            TERRAIN_RENDERER.render(TERRAINS, camera, w.getClipPlane()); 
            ENTITY_RENDERER.render(ENTITIES, camera, w.getClipPlane());
            w.getRefractionFbo().unbind();
            w.getClipPlane().w = (clipDistance);
        });
        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
    }
    
    /**Rendering all 3 dimensional elements into the gbuffer
     */
    private static void render3D(){
        
        //Binding gBuffer and starting lighted section
        //Render all what should be lighted
        GBUFFER.bind(GL30.GL_COLOR_ATTACHMENT0);
        clear();
            
            //Rendring skybox
            if(skybox != null)
                SKYBOX_RENDERER.render(skybox, camera);
            
            //Rendering terrains
            TERRAIN_RENDERER.render(TERRAINS, camera, CLIP_PLANE_RENDER_ALL); 
            GRASSLAND_RENDERER.renderGrassland(TERRAINS, camera);
            TERRAINS.clear();
            
            //Rendering waters
            WATER_RENDERER.render(WATERS, camera);
            WATERS.clear();
            
            //Rendering 3d guis
            GUI_RENDERER.render(GUIS_3D, camera, true);
            FONT_RENDERER.render(GUIS_3D, camera, true);
            GUIS_3D.clear();
            
            //Rendering entities
            ENTITY_RENDERER.render(ENTITIES, camera, CLIP_PLANE_RENDER_ALL);
            ENTITIES.clear();

            //Rendering particles
            PARTICLE_RENDERER.render(PARTICLES, camera);
            PARTICLES.clear();

            //Rendering sun
            if(sun != null)
                SUN_RENDERER.render(sun, camera);
            
            //Rendering moon
            if(moon != null)
                SUN_RENDERER.render(moon, camera);
            
        //Stop lighted section and restore rendermode
        GBUFFER.unbind();
    }
    
    /**Apply all post processing effects and lighting to the gbuffer and 
     * blitting into the output gbuffer
     */
    private static void postProcess(){
        
        //Bind postprocesser input fbo and clear
        PostProcesser.getInput().bind(GL30.GL_COLOR_ATTACHMENT0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            
            //Rendering lights into scene
            DEFFERED_RENDERER.render(GBUFFER, POINT_LIGHTS, SPOT_LIGHTS,
            AMBIENT_LIGHTS, DIRECTIONAL_LIGHTS, camera);
            
            //Clear light sources
            AMBIENT_LIGHTS.clear();
            DIRECTIONAL_LIGHTS.clear();
            POINT_LIGHTS.clear();
            SPOT_LIGHTS.clear();
            
        //Unbind postprocesser input fbo
        PostProcesser.getInput().unbind();
        GBUFFER.blitToFbo(PostProcesser.getInput(), GL11.GL_DEPTH_BUFFER_BIT);
        
        //Rendering post processed image to output fbo
        PostProcesser.render();
        
        //Clearing output gbuffer
        OUTPUT_GBUFFER.bind(GL30.GL_COLOR_ATTACHMENT0);
        clear();
        OUTPUT_GBUFFER.unbind();
        
        PostProcesser.getOutput().blitToFbo(OUTPUT_GBUFFER, GL11.GL_COLOR_BUFFER_BIT);
    }
    
    /**Rendering all 2 dimensional elements and blitting output of final
     * result onto the screen
     */
    private static void render2D(){
        
        //Rendering 2d guis into output gbuffer and blit to screen
        OUTPUT_GBUFFER.bind(GL30.GL_COLOR_ATTACHMENT0);
            
            if(lensFlare != null)
                LENS_FLARE_RENDERER.render(lensFlare);
            
            //Rendering 2d guis and texts
            GUI_RENDERER.render(GUIS_2D, camera, false);
            FONT_RENDERER.render(GUIS_2D, camera, false);
            GUIS_2D.clear();
            
        OUTPUT_GBUFFER.unbind();
        OUTPUT_GBUFFER.blitToScreen();
    }
    
    /**Getting color under mouse cursor of gbuffer and output gbuffer
     * and store into picked color
     */
    private static void getPickColor(){
        
        //Bind picking attachment of gbuffer
        GBUFFER.bind(GL30.GL_COLOR_ATTACHMENT5);
            
            //Get picked color of gbuffer
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            PICK_DATA.clear();
            GL11.glReadPixels((int)(Mouse.getPosx()),
            (int)((Window.getHeight() -Mouse.getPosy())),
            1, 1, GL11.GL_RGBA, GL11.GL_FLOAT, PICK_DATA);
            
            //Set picked color
            PICKED_COLOR.setRed(PICK_DATA.get(0));
            PICKED_COLOR.setGreen(PICK_DATA.get(1));
            PICKED_COLOR.setBlue(PICK_DATA.get(2));
            
        GBUFFER.unbind();
        
        //Bind picking attachment of output gbuffer
        OUTPUT_GBUFFER.bind(GL30.GL_COLOR_ATTACHMENT5);
            
            //Get picked color of output
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            PICK_DATA.clear();
            GL11.glReadPixels((int)(Mouse.getPosx()),
            (int)((Window.getHeight() -Mouse.getPosy())),
            1, 1, GL11.GL_RGBA, GL11.GL_FLOAT, PICK_DATA);
            
            //Set picked color, if not black
            if(PICK_DATA.get(0) != 0.0f && PICK_DATA.get(1) != 0.0f && PICK_DATA.get(2) != 0.0f){
                PICKED_COLOR.setRed(PICK_DATA.get(0));
                PICKED_COLOR.setGreen(PICK_DATA.get(1));
                PICKED_COLOR.setBlue(PICK_DATA.get(2));
            }
            
        OUTPUT_GBUFFER.unbind();
    }
    
    /**@return Color thats get picked by the mouse
     */
    public static Color getPickedColor(){
        return PICKED_COLOR;
    }
    
    /**Rendering 2 dimensional gui and all its components onto the screen
     * 
     * @param gui Gui to render
     */
    public static void renderGui2D(GUIPane gui){
        GUIS_2D.add(gui);
    }
    
    /**Rendering 3 dimensional gui and all its components into the world
     * 
     * @param gui Gui to render
     */
    public static void renderGui3D(GUIPane gui){
        GUIS_3D.add(gui);
    }
    
    /**Rendering lens flare effect onto the screen
     * 
     * @param lensFlare Lens flare to render or null to remove lens flare
     */
    public static void renderLensFlare(LensFlare lensFlare){
        MasterRenderer.lensFlare = lensFlare;
    }
    
    /**Setting the camera to render next frame from
     * 
     * @param cam Camera to render from
     */
    public static void setCamera(Camera cam){
         MasterRenderer.camera = cam;
    }
    
    /**Setting the sun to render in the next frame
     * 
     * @param sun Sun to render or null to remove sun
     */
    public static void setSun(Sun sun){
        MasterRenderer.sun = sun;
    }
    
    /**Setting the moon to render in the next frame
     * 
     * @param moon Moon to render or null to remove moon
     */
    public static void setMoon(Moon moon){
        MasterRenderer.moon = moon;
    }
    
    /**Setting the skybox to render in the next frame
     * 
     * @param skybox Skybox to render or null to remove skybox
     */
    public static void setSkybox(Skybox skybox){
        MasterRenderer.skybox = skybox;
    }
    
    /**Read and writeable getter for the current setted sun.
     * Sun can be set with the setter or this getter can be used to
     * modify the current sun. Contains null, if no moon is set!
     * 
     * @return Read/writeable current sun or null
     */
    public static Sun getSun() {
        return sun;
    }
    
    /**Read and writeable getter for the current setted moon.
     * Sun can be set with the setter or this getter can be used to
     * modify the current moon. Contains null, if no moon is set!
     * 
     * @return Read/writeable current moon or null
     */
    public static Moon getMoon() {
        return moon;
    }
    
    /**Adding a new terrain to the terrain renderlist. So it will be rendered in
     * the next frame.
     * 
     * @param terrain Terrain to add
     */
    public static void renderTerrain(Terrain terrain){
        TERRAINS.add(terrain);
    }
    
    /**Adding a new entity to the entity renderlist. So it will be rendered in
     * the next frame.
     * 
     * @param entity Entity to add
     */
    public static void renderEntity(Entity entity){
        ENTITIES.add(entity);
    }
    
    /**Adding a new water to the water renderlist. So it will be rendered in
     * the next frame.
     * 
     * @param water Water to add
     */
    public static void renderWater(Water water){
        WATERS.add(water);
    }
    
    /**Adding a new point light to the point light renderlist. So it will be rendered in
     * the next frame.<br>
     * Maximum lights is defined by the DeferredShader.MAX_LIGHTS variable. All
     * extra lights will not be rendered
     * 
     * @param light Point light to add
     */
    public static void renderPointLight(PointLight light){
        POINT_LIGHTS.add(light);
    }
    
    /**Adding a new spot light to the spot light renderlist. So it will be rendered in
     * the next frame.<br>
     * Maximum lights is defined by the DeferredShader.MAX_LIGHTS variable. All
     * extra lights will not be rendered
     * 
     * @param light Spot light to add
     */
    public static void renderSpotLight(SpotLight light){
        SPOT_LIGHTS.add(light);
    }
    
    /**Adding a new ambient light to the ambient light renderlist. So it will be rendered in
     * the next frame.<br>
     * Maximum lights is defined by the DeferredShader.MAX_LIGHTS variable. All
     * extra lights will not be rendered
     * 
     * @param light Ambient light to add
     */
    public static void renderAmbientLight(AmbientLight light){
        AMBIENT_LIGHTS.add(light);
    }
    
    /**Adding a new directional light to the directional light renderlist. So it will be rendered in
     * the next frame.<br>
     * Maximum lights is defined by the DeferredShader.MAX_LIGHTS variable. All
     * extra lights will not be rendered
     * 
     * @param light Directional light to add
     */
    public static void renderDirectionalLight(DirectionalLight light){
        DIRECTIONAL_LIGHTS.add(light);
    }

    /**Adding new particle to the particle renderlist. So it will be rendered in the next frame<br>
     *
     * @param particle Particle to add
     */
    public static void renderParticle(Particle particle){
        if(!PARTICLES.containsKey(particle.getTexture())){
            PARTICLES.put(particle.getTexture(), new ArrayList<>());
        }
        PARTICLES.get(particle.getTexture()).add(particle);
    }

    /**Clearing background of bound buffer
     */
    private static void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }
    
    /**@return GBuffer with the rendered scene
     */
    public static GBuffer getGBUFFER() {
        return GBUFFER;
    }
    
    /**@return Current camera, the scene gets rendered from
     */
    public static Camera getCamera() {
        return camera;
    }
}
