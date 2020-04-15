import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;

import de.coreengine.asset.AssetDatabase;
import de.coreengine.asset.CemLoader;
import de.coreengine.framework.Keyboard;
import de.coreengine.framework.Window;
import de.coreengine.rendering.renderable.Entity;
import de.coreengine.rendering.renderable.light.AmbientLight;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.Game;
import de.coreengine.system.GameObject;
import de.coreengine.system.Scene;
import de.coreengine.system.gameObjects.FPCamera;
import de.coreengine.system.gameObjects.TPCamera;
import de.coreengine.util.bullet.Physics;

public class Test0 {
    public static void main(String[] args) {
        Game.init(1024, 768, "Test0", false);

        Scene scene = new Scene();
        Game.registerScene(scene);

        // scene.addGameObject(new TPCamera());
        scene.addGameObject(new FPCamera(0.0f, 2.0f, 0.0f) {
            @Override
            public void onUpdate() {
                System.out.println(getCamera().getPosition());
                super.onUpdate();
            }
        });

        GameObject umrellaStand = new GameObject() {
            String model = "umbrellaStand.cem";
            RigidBody body = null;
            Entity entity = new Entity();

            @Override
            public void onInit() {
                CemLoader.loadModel(model, "", true);
                body = Physics.createRigidBody(0.1f, AssetDatabase.getModel(model).getMeshes()[0].getShape(), true);
                addRigidBodyToWorld(body);

                entity.setModel(model);
                entity.getTransform().setScaleX(0.01f);
                entity.getTransform().setScaleY(0.01f);
                entity.getTransform().setScaleZ(0.01f);
                super.onInit();
            }

            @Override
            public void onUpdate() {
                if (Keyboard.isKeyPressed(Keyboard.KEY_ENTER)) {
                    body.applyCentralForce(new Vector3f(0, 10, 0));
                }
                if (Keyboard.isKeyPressed(Keyboard.KEY_UP)) {
                    body.applyCentralForce(new Vector3f(0, 0, 2));
                }
                if (Keyboard.isKeyPressed(Keyboard.KEY_LEFT)) {
                    body.applyCentralForce(new Vector3f(2, 0, 0));
                }
                if (Keyboard.isKeyPressed(Keyboard.KEY_DOWN)) {
                    body.applyCentralForce(new Vector3f(0, 0, -2));
                }
                if (Keyboard.isKeyPressed(Keyboard.KEY_RIGHT)) {
                    body.applyCentralForce(new Vector3f(-2, 0, 0));
                }
                entity.getTransform().setFromRigidBody(body);
                super.onUpdate();
            }

            @Override
            public void onRender() {
                MasterRenderer.renderEntity(entity);
                super.onRender();
            }
        };
        scene.addGameObject(umrellaStand);

        GameObject lights = new GameObject() {
            AmbientLight ambient = new AmbientLight();

            @Override
            public void onInit() {
                ambient.setIntensity(1.25f);
                super.onInit();
            }

            @Override
            public void onRender() {
                MasterRenderer.renderAmbientLight(ambient);
                super.onRender();
            }
        };
        scene.addGameObject(lights);

        GameObject floor = new GameObject() {
            RigidBody body = Physics.createRigidBody(0, new StaticPlaneShape(new Vector3f(0, 1, 0), 0), false);
            Entity entity = new Entity();
            String model = "FloorT.cem";

            @Override
            public void onInit() {
                CemLoader.loadModel(model, "", true);
                entity.setModel(model);
                entity.getTransform().setScaleX(0.01f);
                entity.getTransform().setScaleY(0.01f);
                entity.getTransform().setScaleZ(0.01f);
                addRigidBodyToWorld(body);
                super.onInit();
            }

            @Override
            public void onRender() {
                MasterRenderer.renderEntity(entity);
                super.onRender();
            }
        };
        scene.addGameObject(floor);

        while (Window.keepAlive() && !Keyboard.isKeyPressed(Keyboard.KEY_ESCAPE)) {
            Game.tick();
        }

        Game.exit(0);
    }
}