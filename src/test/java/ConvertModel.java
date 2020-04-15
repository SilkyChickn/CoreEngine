import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CylinderShape;

import de.coreengine.asset.CemLoader;
import de.coreengine.asset.ModelLoader;
import de.coreengine.asset.dataStructures.ModelData;
import de.coreengine.util.bullet.CollisionShapeParser;

public class ConvertModel {
    public static void main(String[] args) {

        // Edit this and run
        String in = "UmbrellaStand.obj";
        String out = "umbrellaStand.cem";
        String collisionshape = CollisionShapeParser.toString(new CylinderShape(new Vector3f(0.2f, 1.0f, 0.2f)));
        // ------------------

        ModelData data = ModelLoader.loadModelFileData(in, collisionshape);
        CemLoader.saveModelData(out, data);
    }
}