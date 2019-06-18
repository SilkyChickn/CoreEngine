package de.coreengine.examples.rotatingCube.gameObjects;

import com.bulletphysics.collision.shapes.BoxShape;
import de.coreengine.asset.ObjLoader;
import de.coreengine.rendering.model.Model;
import de.coreengine.rendering.renderable.Entity;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;
import de.coreengine.util.FrameTimer;

import javax.vecmath.Vector3f;

public class RotatingCube extends GameObject {
    private static final float ROTATION_SPEED = 45.0f;

    private static final Model CUBE_MODEL = ObjLoader.loadModel(
        "Cube.obj", new BoxShape(new Vector3f(1, 1, 1)), true, null
    );

    //Entity to render
    private Entity cubeEntity;

    //Current entities rotation
    private float rotation;

    @Override
    public void onInit() {
        super.onInit();

        //Create entity and set its model
        cubeEntity = new Entity();
        cubeEntity.setModel(CUBE_MODEL);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        //Rotate animation by increasing rotation by rotationSpeed * tslf (Time since last frame) from FrameTimer
        //So the rotation is the same speed at every system
        rotation += ROTATION_SPEED * FrameTimer.getTslf();

        //Update entities transformation
        cubeEntity.getTransform().setRotY(rotation);
    }

    @Override
    public void onRender() {
        super.onRender();

        //Render stuff with MasterRenderer class
        MasterRenderer.renderEntity(cubeEntity);
    }
}
