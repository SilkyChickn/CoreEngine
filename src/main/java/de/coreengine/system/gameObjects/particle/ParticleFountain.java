package de.coreengine.system.gameObjects.particle;

import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.renderable.Particle;
import de.coreengine.system.GameObject;
import de.coreengine.util.Configuration;
import de.coreengine.util.FrameTimer;
import de.coreengine.util.Logger;
import de.coreengine.util.Toolbox;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class ParticleFountain extends GameObject {
    private static final float DEFAULT_GENERATION_SPEED =
            Configuration.getValuef("PARTICLE_FOUNTAIN_DEFAULT_GENERATION_TIME");
    private static final float[] DEFAULT_TTL_RANGE =
            Configuration.getValuefa("PARTICLE_FOUNTAIN_DEFAULT_TTL_RANGE");
    private static final float[] DEFAULT_MASS_RANGE =
            Configuration.getValuefa("PARTICLE_FOUNTAIN_DEFAULT_MASS_RANGE");
    private static final float[] DEFAULT_SIZE_RANGE =
            Configuration.getValuefa("PARTICLE_FOUNTAIN_DEFAULT_SIZE_RANGE");
    private static final float[] DEFAULT_VELOCITY_RANGE =
            Configuration.getValuefa("PARTICLE_FOUNTAIN_DEFAULT_VELOCITY_RANGE");

    //Texture for the particles
    public int texture = Material.TEXTURE_WHITE;

    //Particle generation ranges settings
    public Vector2f particleMinSize = new Vector2f(DEFAULT_SIZE_RANGE[0], DEFAULT_SIZE_RANGE[1]);
    public Vector2f particleMaxSize = new Vector2f(DEFAULT_SIZE_RANGE[2], DEFAULT_SIZE_RANGE[3]);
    public Vector3f velocityMin = new Vector3f(
            DEFAULT_VELOCITY_RANGE[0], DEFAULT_VELOCITY_RANGE[1], DEFAULT_VELOCITY_RANGE[2]);
    public Vector3f velocityMax = new Vector3f(
            DEFAULT_VELOCITY_RANGE[3], DEFAULT_VELOCITY_RANGE[4], DEFAULT_VELOCITY_RANGE[5]);
    public float particleMinTTL = DEFAULT_TTL_RANGE[0], particleMaxTTL = DEFAULT_TTL_RANGE[1];
    public float particleMinMass = DEFAULT_MASS_RANGE[0], particleMaxMass = DEFAULT_MASS_RANGE[1];

    //Position of the fountain
    private Vector3f position = new Vector3f();

    //Particle generation speed
    private float generationCounter = 0.0f;
    public float generationSpeed = DEFAULT_GENERATION_SPEED;

    @Override
    public void onUpdate() {
        super.onUpdate();

        //Check if its time to generate new particle
        generationCounter += FrameTimer.getTslf();
        if(generationCounter >= generationSpeed){
            generationCounter = 0.0f;
            generateParticle();
        }
    }

    /**Generating new random particle
     */
    private void generateParticle(){
        Logger.info("New Particle", "YEEAAH!");

        MovingParticle movingParticle = new MovingParticle(
                texture, position,
                Toolbox.randomVector(particleMinSize, particleMaxSize),
                Toolbox.randomVector(velocityMin, velocityMax),
                Toolbox.randomFloat(particleMinMass, particleMaxMass),
                Toolbox.randomFloat(particleMinTTL, particleMaxTTL)
        );

        addChild(movingParticle);
    }
}
