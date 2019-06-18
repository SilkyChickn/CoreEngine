package de.coreengine.examples.rotatingCube.gameObjects;

import de.coreengine.rendering.renderable.light.AmbientLight;
import de.coreengine.rendering.renderable.light.DirectionalLight;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;

public class Ambient extends GameObject {

    //Ambient light
    private AmbientLight light;
    private DirectionalLight light2;

    @Override
    public void onInit() {
        super.onInit();

        //Creating lights
        light = new AmbientLight();
        light.setIntensity(2.0f);

        light2 = new DirectionalLight();
        light2.getDirection().set(1, -1, 1);
        light2.setIntensity(0.5f);
    }

    @Override
    public void onRender() {
        super.onRender();

        //Render ambient light to see something in the scene
        MasterRenderer.renderAmbientLight(light);

        //render directional light to see some shadows
        MasterRenderer.renderDirectionalLight(light2);
    }
}
