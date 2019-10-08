package de.coreengine.examples.rotatingCube.scenes;

import de.coreengine.examples.rotatingCube.gameObjects.Ambient;
import de.coreengine.examples.rotatingCube.gameObjects.Controls;
import de.coreengine.examples.rotatingCube.gameObjects.RotatingCube;
import de.coreengine.system.Scene;
import de.coreengine.system.gameObjects.TPCamera;

public class MainScene extends Scene {

    @Override
    public void init() {
        super.init();

        //Creating game objects and add them to the scene
        //Added game objects will be auto initialized, updated and rendered with the scene.

        addGameObject(new TPCamera());
        addGameObject(new Ambient());
        addGameObject(new RotatingCube());
        addGameObject(new Controls());
    }
}
