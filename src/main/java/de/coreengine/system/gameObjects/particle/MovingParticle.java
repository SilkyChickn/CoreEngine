package de.coreengine.system.gameObjects.particle;

import de.coreengine.rendering.renderable.Particle;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;
import de.coreengine.util.FrameTimer;
import de.coreengine.util.bullet.Physics;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class MovingParticle extends GameObject {

    //Particles mass
    protected float mass;

    //Particles renderable
    protected Particle particle;

    //Particles velocity
    protected Vector3f velocity;

    //Particles time to live
    protected float ttl;

    public MovingParticle(int texture, Vector3f pos, Vector2f size, Vector3f velocity, float mass, float ttl){
        this.velocity = velocity;
        this.mass = mass;

        //Create renderable particle
        this.particle = new Particle();
        this.particle.setTexture(texture);
        this.particle.getPosition().set(new Vector3f(0, 0, 0));
        this.particle.getSize().set(size);

        this.ttl = ttl;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        //Move particle
        particle.getPosition().add(velocity);
        velocity.scale(0.99f);
        particle.getPosition().y += mass * Physics.GRAVITY_OF_EARTH * FrameTimer.getTslf();

        //Decrease ttl
        this.ttl -= FrameTimer.getTslf();
    }

    public boolean shouldDie(){
        return ttl <= 0;
    }

    @Override
    public void onRender() {
        super.onRender();

        MasterRenderer.renderParticle(particle);
    }
}
