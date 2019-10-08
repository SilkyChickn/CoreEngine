package de.coreengine.examples.rotatingCube;

import de.coreengine.examples.rotatingCube.scenes.MainScene;
import de.coreengine.framework.Window;
import de.coreengine.system.Game;

public class Main {

    public static void main(String[] args) {

        //Initialize engine
        Game.init(800, 600, "CE: Rotating Cube", false);

        //Create new main scene and add to game
        MainScene scenes = new MainScene();
        Game.registerScene(scenes);

        //Game loop
        //Loop until the user close the window
        while(Window.keepAlive()){

            //Update engine (also updates current scene!)
            Game.tick();
        }

        //Clean up memory and exit game with code 0 (no errors)
        Game.exit(0);
    }
}
