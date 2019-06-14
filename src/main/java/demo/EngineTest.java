package demo;

import com.bulletphysics.collision.shapes.SphereShape;
import de.coreengine.asset.FntLoader;
import de.coreengine.asset.ImageLoader;
import de.coreengine.asset.ObjLoader;
import de.coreengine.asset.meta.Font;
import de.coreengine.asset.meta.Image;
import de.coreengine.framework.Keyboard;
import de.coreengine.framework.Window;
import de.coreengine.rendering.model.Color;
import de.coreengine.rendering.model.SimpleModel;
import de.coreengine.rendering.renderable.LensFlare;
import de.coreengine.rendering.renderable.Particle;
import de.coreengine.rendering.renderable.Water;
import de.coreengine.rendering.renderable.gui.GUIPane;
import de.coreengine.rendering.renderable.terrain.Terrain;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.rendering.renderer.PostProcesser;
import de.coreengine.rendering.renderer.ppeffects.HsbEffect;
import de.coreengine.rendering.renderer.ppeffects.LightScatteringEffect;
import de.coreengine.system.Game;
import de.coreengine.system.Scene;
import de.coreengine.system.gameObjects.DayNightCycle;
import de.coreengine.system.gameObjects.particle.ParticleFountain;
import de.coreengine.util.bullet.Physics;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3f;

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

/**
 *
 * @author Darius Dinger
 */
public class EngineTest {
    
    public static void main(String[] args){
        
        Game.init(1920, 1080, "CoreEngine pre.A.", false);
        Window.setVsyncInterval(0);
        
        Image windowIcon = ImageLoader.loadImageFile("res/icon.png",
                false, GL11.GL_LINEAR, true);
        Window.setIcon(windowIcon);
        
        Scene scene = new Scene();
        
        //FPCamera fpc = new FPCamera(50, 27, 50);
        FlyCam fpc = new FlyCam();
        scene.addGameObject(fpc);
        
        int sunTexture = ImageLoader.loadImageFileGl("res/textures/sun.png", 
                false, GL11.GL_LINEAR, false);
        
        int moonTexture = ImageLoader.loadImageFileGl("res/textures/moon.png", 
                false, GL11.GL_LINEAR, false);
        
        int skyboxTexDay = ImageLoader.loadCubeMap
            ("res/textures/skybox/miramar/miramar", "png", false);
        
        int skyboxTexEvening = ImageLoader.loadCubeMap
            ("res/textures/skybox/miramar/violentdays", "png", false);
        
        int skyboxTexNight = ImageLoader.loadCubeMap
            ("res/textures/skybox/sor_cwd/cwd", "JPG", false);
        
        Color dayColor = new Color(0.5f, 0.5f, 0.5f);
        Color eveningColor = new Color(1.0f, 0.6f, 0.6f);
        Color nightColor = new Color(0.075f, 0.075f, 0.125f);
        
        DayNightCycle dayNightCycle = new DayNightCycle(skyboxTexDay, dayColor, 
                skyboxTexEvening, eveningColor, skyboxTexNight, nightColor, 
                sunTexture, moonTexture);
        dayNightCycle.setCenter(fpc.getCamera().getPosition());
        dayNightCycle.getSun().getColor().setGreen(0.75f);
        dayNightCycle.getSun().getColor().setBlue(0.75f);
        dayNightCycle.getSun().setIntensity(0.75f);
        dayNightCycle.getMoon().getColor().setRed(0.25f);
        dayNightCycle.getMoon().getColor().setGreen(0.25f);
        dayNightCycle.getMoon().getColor().setBlue(0.75f);
        dayNightCycle.getMoon().setIntensity(1.5f);
        scene.addGameObject(dayNightCycle);
        
        int blendMap = ImageLoader.loadImageFileGl(
                "res/textures/blendMap.png", false, GL11.GL_LINEAR, false);
        Image heightMap = ImageLoader.loadImageFile(
                "res/textures/heightMap.png", false, GL11.GL_LINEAR, false);
        int lightMap = ImageLoader.loadImageFileGl(
                "res/textures/lightMap.png", false, GL11.GL_LINEAR, false);
        int windMap = ImageLoader.loadImageFileGl(
                "res/textures/windMap.png", false, GL11.GL_LINEAR, false);
        
        int grassTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/grass0_DIF.png", true, GL11.GL_LINEAR, false);
        int groundTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/Ground_11_DIF.png", true, GL11.GL_LINEAR, false);
        int oldGrassTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/Ground_17_DIF.png", true, GL11.GL_LINEAR, false);
        int cliffTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/Ground_18_DIF.png", true, GL11.GL_LINEAR, false);
        
        int grassNormalTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/grass0_NRM.png", true, GL11.GL_LINEAR, false);
        int groundNormalTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/Ground_11_NRM.png", true, GL11.GL_LINEAR, false);
        int oldGrassNormalTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/Ground_17_NRM.png", true, GL11.GL_LINEAR, false);
        int cliffNormalTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/Ground_18_NRM.png", true, GL11.GL_LINEAR, false);
        
        int grassDispTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/grass0_DISP.png", true, GL11.GL_LINEAR, false);
        int groundDispTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/Ground_11_DISP.png", true, GL11.GL_LINEAR, false);
        int oldGrassDispTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/Ground_17_DISP.png", true, GL11.GL_LINEAR, false);
        int cliffDispTexture = ImageLoader.loadImageFileGl(
                "res/textures/oreon/Ground_18_DISP.png", true, GL11.GL_LINEAR, false);
        
        Terrain terrain = new Terrain();
        terrain.getConfig().setBlendMap(blendMap);
        terrain.getConfig().setHeightMap(heightMap);
        terrain.getConfig().setLightMap(lightMap);
        
        terrain.getConfig().getTexturePack().getMaterial().diffuseMap = oldGrassTexture;
        terrain.getConfig().getTexturePack().getRedMaterial().diffuseMap = cliffTexture;
        terrain.getConfig().getTexturePack().getGreenMaterial().diffuseMap = grassTexture;
        terrain.getConfig().getTexturePack().getBlueMaterial().diffuseMap = groundTexture;
        
        terrain.getConfig().getTexturePack().getMaterial().normalMap = oldGrassNormalTexture;
        terrain.getConfig().getTexturePack().getRedMaterial().normalMap = cliffNormalTexture;
        terrain.getConfig().getTexturePack().getGreenMaterial().normalMap = grassNormalTexture;
        terrain.getConfig().getTexturePack().getBlueMaterial().normalMap = groundNormalTexture;
        
        terrain.getConfig().getTexturePack().getMaterial().displacementMap = oldGrassDispTexture;
        terrain.getConfig().getTexturePack().getRedMaterial().displacementMap = cliffDispTexture;
        terrain.getConfig().getTexturePack().getGreenMaterial().displacementMap = grassDispTexture;
        terrain.getConfig().getTexturePack().getBlueMaterial().displacementMap = groundDispTexture;
        
        terrain.getConfig().getTexturePack().getMaterial().tiling = 200.0f;
        terrain.getConfig().getTexturePack().getRedMaterial().tiling = 150.0f;
        terrain.getConfig().getTexturePack().getGreenMaterial().tiling = 400.0f;
        terrain.getConfig().getTexturePack().getBlueMaterial().tiling = 100.0f;
        
        terrain.getConfig().getTexturePack().getMaterial().displacementFactor = 0.05f;
        terrain.getConfig().getTexturePack().getRedMaterial().displacementFactor = 0.05f;
        terrain.getConfig().getTexturePack().getGreenMaterial().displacementFactor = 0.05f;
        terrain.getConfig().getTexturePack().getBlueMaterial().displacementFactor = 0.05f;
        
        terrain.getConfig().getTexturePack().getMaterial().reflectivity = 0.0f;
        terrain.getConfig().getTexturePack().getRedMaterial().reflectivity = 0.0f;
        terrain.getConfig().getTexturePack().getGreenMaterial().reflectivity = 0.0f;
        terrain.getConfig().getTexturePack().getBlueMaterial().reflectivity = 0.0f;

        terrain.recalcCollisionShape(100);
        scene.getPhysicWorld().addRigidBody(Physics.createRigidBody(0, terrain.getShape(), false));

        int densityMap = ImageLoader.loadImageFileGl(
                "res/textures/densityMap.png", false, GL11.GL_LINEAR, false);
        
        SimpleModel grasModel = ObjLoader.loadSimpleModel
            ("res/models/Gras/Gras.obj", new SphereShape(1.0f), false);
        
        terrain.getGrassland().setDensityMap(densityMap);
        terrain.getGrassland().setMesh(grasModel, 0.01f);
        terrain.getGrassland().setWindMap(windMap);
        terrain.getGrassland().setWindMapTiling(1.0f);
        terrain.setGrasslandEnabled(true);
        
        int waterDudvMap = ImageLoader.loadImageFileGl(
                "res/textures/waterDudv.png", true, GL11.GL_LINEAR, false);
        
        int waterNormalMap = ImageLoader.loadImageFileGl(
                "res/textures/waterNormal.png", true, GL11.GL_LINEAR, false);
        
        Water water = new Water();
        water.setDudvMap(waterDudvMap);
        water.setNormalMap(waterNormalMap);
        water.setTiling(10.0f);
        water.setScale(250);
        water.setY(21.5f);
        
        LightScatteringEffect lightScattering = new LightScatteringEffect();
        
        int lfTexture0 = ImageLoader.loadImageFileGl("res/textures/lensFlare/lensFlare0.png", 
                false, GL11.GL_LINEAR, false);
        int lfTexture1 = ImageLoader.loadImageFileGl("res/textures/lensFlare/lensFlare1.png", 
                false, GL11.GL_LINEAR, false);
        int lfTexture2 = ImageLoader.loadImageFileGl("res/textures/lensFlare/lensFlare2.png", 
                false, GL11.GL_LINEAR, false);
        int lfTexture3 = ImageLoader.loadImageFileGl("res/textures/lensFlare/lensFlare3.png", 
                false, GL11.GL_LINEAR, false);
        int lfTexture4 = ImageLoader.loadImageFileGl("res/textures/lensFlare/lensFlare4.png", 
                false, GL11.GL_LINEAR, false);
        
        LensFlare lensFlare = new LensFlare();
        lensFlare.addTexture(lfTexture0);
        lensFlare.addTexture(lfTexture1);
        lensFlare.addTexture(lfTexture2);
        lensFlare.addTexture(lfTexture3);
        lensFlare.addTexture(lfTexture4);
        
        /*
        Model logModel = ObjLoader.loadModel("res/models/Cube/Cube.obj", false);
        Entity logEntity = new Entity(logModel);
        logEntity.getTransform().setPosX(39.0f);
        logEntity.getTransform().setPosY(24.0f);
        logEntity.getTransform().setPosZ(60.0f);
        logEntity.getTransform().setScaleX(0.01f);
        logEntity.getTransform().setScaleY(0.01f);
        logEntity.getTransform().setScaleZ(0.01f);
        
        Model treeModel = ObjLoader.loadModel("res/models/Tree/Tree.obj", false);
        Entity treeEntity = new Entity(treeModel);
        treeEntity.getTransform().setPosX(39.0f);
        treeEntity.getTransform().setPosY(24.0f);
        treeEntity.getTransform().setPosZ(60.0f);
        treeEntity.getTransform().setScaleX(0.2f);
        treeEntity.getTransform().setScaleY(0.2f);
        treeEntity.getTransform().setScaleZ(0.2f);
        */
        
        Font testFont = FntLoader.loadFont("res/fonts/Arial.fnt", false);
        
        int shieldImage = ImageLoader.loadImageFileGl(
                "res/textures/gui/shield.png", true, GL11.GL_LINEAR, false);
        
        GUIPane pane = new GUIPane(null);
        pane.setTexture(shieldImage);
        pane.setPosX(100.0f);
        pane.setPosY(30.0f);
        pane.setPosZ(50.0f);
        pane.setScaleX(6.0f);
        pane.setScaleY(5.0f);
        pane.setRotY(270.0f);
        pane.getText().setFont(testFont);
        pane.getText().setFontSize(30.0f);
        pane.getText().setPadding(0.0f);
        pane.getText().setText("\nCore Engine\n-\nBetter than\nReallife!");
        pane.enableText();
        
        //int testSound = OggLoader.loadSound("res/sounds/Test.ogg");
        //AudioSource source = new AudioSource();
        //source.setPosition(100, 30, 100);
        //source.setVolume(5);
        //source.play(testSound);
        //source.setLoop(true);
        
        SimpleCube c1 = new SimpleCube(100, 50, 100), c2 = new SimpleCube(100, 50, 150);
        
        scene.addGameObject(new SimplePlane());
        scene.addGameObject(c1);
        scene.addGameObject(c2);
        
        HsbEffect hsbEffect = new HsbEffect();
        hsbEffect.setSaturation(0.75f);

        ParticleFountain fountain = new ParticleFountain();
        scene.addGameObject(fountain);

        Game.registerScene(scene);

        while(!Window.shouldWindowClose()){
            
            if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_T)){
                c1.getPhysicBody().applyCentralImpulse(new Vector3f(100, 0, 0));
            }
            if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_G)){
                c1.getPhysicBody().applyCentralImpulse(new Vector3f(-100, 0, 0));
            }
            if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_F)){
                c1.getPhysicBody().applyCentralImpulse(new Vector3f(0, 0, -100));
            }
            if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_H)){
                c1.getPhysicBody().applyCentralImpulse(new Vector3f(0, 0, 100));
            }
            
            if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_J)) dayNightCycle.setSpeed(25.0f);
            else dayNightCycle.setSpeed(0.1f);
            
            //Update system
            terrain.alignTo(fpc.getCamera().getPosition());
            terrain.getGrassland().setWindOffset(terrain.getGrassland().getWindOffset() +0.00025f);
            water.setOffset(water.getOffset() +0.0002f);
            
            pane.setRotY(-fpc.getCamera().getYaw());
            
            MasterRenderer.renderTerrain(terrain);
            MasterRenderer.renderWater(water);
            MasterRenderer.renderLensFlare(lensFlare);
            
            MasterRenderer.renderGui3D(pane);
            
            //MasterRenderer.renderEntity(treeEntity);
            //MasterRenderer.renderEntity(logEntity);
            
            if(dayNightCycle.isDay())
                PostProcesser.addEffect(lightScattering);
            
            PostProcesser.addEffect(hsbEffect);
            
            Game.tick();
        }
        Game.exit(0);
    }
}