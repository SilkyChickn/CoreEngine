package de.coreengine.rendering.renderable;

import de.coreengine.rendering.model.Material;
import de.coreengine.util.Configuration;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class Particle {
    private static final float[] DEFAULT_SIZE = Configuration.getValuefa("PARTICLE_DEFAULT_SIZE");

    //Particle transformation
    private Vector2f size = new Vector2f(DEFAULT_SIZE[0], DEFAULT_SIZE[1]);
    private Vector3f position = new Vector3f();

    //Particles current texture
    private int texture = Material.TEXTURE_WHITE;

    /**@return Particles current texture
     */
    public int getTexture() {
        return texture;
    }

    /**Setting texture of the particle
     *
     * @param texture New texture of the particle
     */
    public void setTexture(int texture) {
        this.texture = texture;
    }

    /**@return Read/Writeable 2d size of the particle
     */
    public Vector2f getSize() {
        return size;
    }

    /**@return Read/Writeable 3d position of the particle
     */
    public Vector3f getPosition() {
        return position;
    }
}
