/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2019, Suuirad
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.coreengine.system.gameObjects.particle;

import de.coreengine.rendering.model.Material;
import de.coreengine.system.GameObject;
import de.coreengine.util.Configuration;
import de.coreengine.util.FrameTimer;
import de.coreengine.util.Logger;
import de.coreengine.util.Toolbox;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ParticleSystem extends GameObject {
    private static final float DEFAULT_GENERATION_SPEED =
            Configuration.getValuef("PARTICLE_SYSTEM_DEFAULT_GENERATION_TIME");
    private static final float[] DEFAULT_TTL_RANGE =
            Configuration.getValuefa("PARTICLE_SYSTEM_DEFAULT_TTL_RANGE");
    private static final float[] DEFAULT_MASS_RANGE =
            Configuration.getValuefa("PARTICLE_SYSTEM_DEFAULT_MASS_RANGE");
    private static final float[] DEFAULT_SIZE_RANGE =
            Configuration.getValuefa("PARTICLE_SYSTEM_DEFAULT_SIZE_RANGE");
    private static final float[] DEFAULT_VELOCITY_RANGE =
            Configuration.getValuefa("PARTICLE_SYSTEM_DEFAULT_VELOCITY_RANGE");
    private static final float[] DEFAULT_SPAWN_RANGE =
            Configuration.getValuefa("PARTICLE_SYSTEM_DEFAULT_SPAWN_RANGE");
    private static final float[] DEFAULT_GENERATION_COUNT_RANGE =
            Configuration.getValuefa("PARTICLE_SYSTEM_DEFAULT_GENERATION_COUNT_RANGE");
    
    //TextureData for the particles
    private String texture = Material.TEXTURE_WHITE;

    //Particle generation ranges settings
    private float particleMinSize = DEFAULT_SIZE_RANGE[0];
    private float particleMaxSize = DEFAULT_SIZE_RANGE[1];
    private Vector3f spawnRangeMin = new Vector3f(
            DEFAULT_SPAWN_RANGE[0], DEFAULT_SPAWN_RANGE[1], DEFAULT_SPAWN_RANGE[2]);
    private Vector3f spawnRangeMax = new Vector3f(
            DEFAULT_SPAWN_RANGE[3], DEFAULT_SPAWN_RANGE[4], DEFAULT_SPAWN_RANGE[5]);
    private Vector3f velocityMin = new Vector3f(
            DEFAULT_VELOCITY_RANGE[0], DEFAULT_VELOCITY_RANGE[1], DEFAULT_VELOCITY_RANGE[2]);
    private Vector3f velocityMax = new Vector3f(
            DEFAULT_VELOCITY_RANGE[3], DEFAULT_VELOCITY_RANGE[4], DEFAULT_VELOCITY_RANGE[5]);
    private float particleMinTTL = DEFAULT_TTL_RANGE[0], particleMaxTTL = DEFAULT_TTL_RANGE[1];
    private float particleMinMass = DEFAULT_MASS_RANGE[0], particleMaxMass = DEFAULT_MASS_RANGE[1];
    private int generationCountMin = (int) DEFAULT_GENERATION_COUNT_RANGE[0];
    private int generationCountMax = (int) DEFAULT_GENERATION_COUNT_RANGE[1];
    
    //Position of the fountain
    private Vector3f position = new Vector3f();

    //Particle generation speed
    private float generationCounter = 0.0f;
    private float generationSpeed = DEFAULT_GENERATION_SPEED;

    //List that contains all alive particles
    private List<MovingParticle> particles = new ArrayList<>();

    //Class to instantiate when creating new particle game objects
    private Class<? extends MovingParticle> particleClass = MovingParticle.class;

    @Override
    public void onUpdate() {
        super.onUpdate();

        //Check if its time to generate new particle
        generationCounter += FrameTimer.getTslf();
        if(generationCounter >= generationSpeed){
            generationCounter = 0.0f;

            //Generate random count of particles
            int count = Toolbox.randomInt(generationCountMin, generationCountMax);
            for(int i = 0; i < count; i++){
                generateParticle();
            }
        }

        //Remove dead particles from scene
        ListIterator<? extends MovingParticle> pIt = particles.listIterator();
        while(pIt.hasNext()){
            MovingParticle cur = pIt.next();
            if(cur.shouldDie()){
                pIt.remove();
                removeChild(cur);
            }
        }
    }

    /**Generating new random particle
     * The initial values of the particle will be between the ranges
     */
    private void generateParticle(){

        //Generate random values for next particle
        Vector3f spawnPosition = Toolbox.randomVector(spawnRangeMin, spawnRangeMax);
        spawnPosition.add(position);
        float size = Toolbox.randomFloat(particleMinSize, particleMaxSize);
        Vector3f velocity = Toolbox.randomVector(velocityMin, velocityMax);
        float mass = Toolbox.randomFloat(particleMinMass, particleMaxMass);
        float ttl = Toolbox.randomFloat(particleMinTTL, particleMaxTTL);

        //Generate particle
        try {

            //Create particle
            MovingParticle particle = particleClass.newInstance();

            //Setup particle
            particle.setup(mass, velocity, ttl);
            particle.getParticle().getSize().set(size, size);
            particle.getParticle().getPosition().set(spawnPosition);
            particle.getParticle().setTexture(texture);

            //Add particle to scene
            addChild(particle);
            particles.add(particle);

        } catch (InstantiationException | IllegalAccessException e) {
            Logger.warn("Particle instantiation error!",
                    "Check, if the particle game object constructor that is used in the particle system, is empty");
        }
    }

    /**@return Read/writeable vector of the particle system center position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**@param texture TextureData for next generated particles
     */
    public void setTexture(String texture) {
        this.texture = texture;
    }

    /**Setting the range of new particles time to live (ttl) in seconds
     *
     * @param min Minimum ttl
     * @param max Maximum ttl
     */
    public void setTtlRange(float min, float max){
        this.particleMinTTL = min;
        this.particleMaxTTL = max;
    }

    /**Setting the range of new particles mass<br>
     * Gravity formula: mass * Physics.GRAVITY_OF_EARTH * FrameTimer.getTslf()
     *
     * @param min Minimum mass
     * @param max Maximum mass
     */
    public void setMassRange(float min, float max){
        this.particleMinMass = min;
        this.particleMaxMass = max;
    }

    /**Setting the range of new particles size<br>
     *
     * @param min Minimum size
     * @param max Maximum size
     */
    public void setSizeRange(float min, float max){
        this.particleMinSize = min;
        this.particleMaxSize = max;
    }

    /**@param generationSpeed Speed to generate new particles in seconds
     */
    public void setGenerationSpeed(float generationSpeed) {
        this.generationSpeed = generationSpeed;
    }

    /**Setting class to instantiate when creating new particles. The class must expand from the
     * MovingParticle class and must have an empty constructor.
     *
     * @param particleClass Class to instantiate when creating new particles
     */
    public void setParticleClass(Class<? extends MovingParticle> particleClass) {
        this.particleClass = particleClass;
    }

    /**Setting the range of new particles velocity (direction and speed).<br>
     * The velocity will be added every frame to the position and then multiplied by particles damping.<br>
     * Formula: position + (velocity * damping)
     *
     * @param min Minimum velocity
     * @param max Maximum velocity
     */
    public void setVelocityRange(Vector3f min, Vector3f max){
        this.velocityMin = min;
        this.velocityMax = max;
    }

    /**Setting the range of new particles spawn offset.<br>
     * The offset will be added to the system position
     *
     * @param min Minimum offset
     * @param max Maximum offset
     */
    public void setSpawnOffsetRange(Vector3f min, Vector3f max){
        this.spawnRangeMin = min;
        this.spawnRangeMax = max;
    }

    /**Setting the range of new particles count<br>
     * How much particles should be generated, if generation speed expired?
     *
     * @param min Minimum count
     * @param max Maximum count
     */
    public void setGenerationCountRange(int min, int max){
        this.generationCountMin = min;
        this.generationCountMax = max;
    }
}
