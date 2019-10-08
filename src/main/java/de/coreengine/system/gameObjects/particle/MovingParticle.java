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

import de.coreengine.rendering.renderable.Particle;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;
import de.coreengine.util.Configuration;
import de.coreengine.util.FrameTimer;
import de.coreengine.util.bullet.Physics;

import javax.vecmath.Vector3f;

public class MovingParticle extends GameObject {
    private static final float DEFAULT_DAMPING =
            Configuration.getValuef("MOVING_PARTICLE_DEFAULT_DAMPING");
    private static final float DEFAULT_MASS =
            Configuration.getValuef("MOVING_PARTICLE_DEFAULT_MASS");
    private static final float DEFAULT_TTL =
            Configuration.getValuef("MOVING_PARTICLE_DEFAULT_TTL");
    private static final float[] DEFAULT_VELOCITY =
            Configuration.getValuefa("MOVING_PARTICLE_DEFAULT_VELOCITY");

    //Particles mass
    private float mass = DEFAULT_MASS;

    //Particles renderable
    private Particle particle = new Particle();

    //Particles velocity
    private Vector3f velocity = new Vector3f(DEFAULT_VELOCITY);

    //Particles time to live
    private float ttl = DEFAULT_TTL;

    //Particles damping factor per frame
    private float damping = DEFAULT_DAMPING;

    /**@return Read/Writeable particle renderable, that represents the particle in the scene
     */
    public Particle getParticle() {
        return particle;
    }

    /**Setting up particle behavior and physics<br>
     * Gravity formula: mass * Physics.GRAVITY_OF_EARTH * FrameTimer.getTslf()
     *
     * @param mass Particles mass
     * @param velocity Particles velocity/move direction
     * @param ttl Particles time to live (in seconds)
     */
    public void setup(float mass, Vector3f velocity, float ttl){
        this.mass = mass;
        this.velocity = velocity;
        this.ttl = ttl;
    }

    /**Setting damping of the particle. Will be multiplied every frame to the velocity.
     *
     * @param damping New damping factor
     */
    public void setDamping(float damping) {
        this.damping = damping;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        //Move particle
        particle.getPosition().add(velocity);
        velocity.scale(damping);
        particle.getPosition().y += mass * Physics.GRAVITY_OF_EARTH * FrameTimer.getTslf();

        //Decrease ttl
        this.ttl -= FrameTimer.getTslf();
    }

    /**If this method return true, the ttl of the particle is expired and it should be removed from the scene
     *
     * @return Should the particle be removed
     */
    public boolean shouldDie(){
        return ttl <= 0;
    }

    @Override
    public void onRender() {
        super.onRender();
        MasterRenderer.renderParticle(particle);
    }
}
