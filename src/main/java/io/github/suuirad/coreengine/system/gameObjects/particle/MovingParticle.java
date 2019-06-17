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

package io.github.suuirad.coreengine.system.gameObjects.particle;

import io.github.suuirad.coreengine.rendering.renderable.Particle;
import io.github.suuirad.coreengine.rendering.renderer.MasterRenderer;
import io.github.suuirad.coreengine.system.GameObject;
import io.github.suuirad.coreengine.util.FrameTimer;
import io.github.suuirad.coreengine.util.bullet.Physics;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class MovingParticle extends GameObject {

    //Particles mass
    private float mass;

    //Particles renderable
    protected Particle particle;

    //Particles velocity
    private Vector3f velocity;

    //Particles time to live
    private float ttl;

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
