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

package de.coreengine.system.gameObjects;

import de.coreengine.animation.Animation;
import de.coreengine.animation.Animator;
import de.coreengine.rendering.renderable.AnimatedEntity;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;
import de.coreengine.util.FrameTimer;

public class Animated extends GameObject {

    //Animated entity to play animation on
    private AnimatedEntity animatedEntity = null;

    //Animation to play on entity
    private Animation animation = null;

    //Current time of the animation
    private float currentTime = 0;

    //Should the animation looping
    private boolean loop = true;

    //Is the animation paused
    private boolean pause = false;

    /**Play / resume the animation
     */
    public void play(){
        pause = false;
    }

    /**Stop the animation and jump to beginning
     */
    public void stop(){
        currentTime = 0;
        pause();
        reposeSkeleton();
    }

    /**Pause the animation
     */
    public void pause(){
        pause = true;
    }

    /**Reposing the skeleton to the current animation pose
     */
    private void reposeSkeleton(){
        Animator.applyAnimation(animatedEntity.getSkeleton(), animation, currentTime);
    }

    @Override
    public void onUpdate() {

        //If animation is paused or no animation or entity set, update children and return
        if(pause || animatedEntity == null || animation == null) {
            super.onUpdate();
            return;
        }

        //Increase animation time
        currentTime += FrameTimer.getTslf();

        //Is animation finished
        if(currentTime >= animation.getLength()){
            if(loop) currentTime %= animation.getLength();
            else {
                currentTime = animation.getLength();
                pause();
            }
        }

        //Animate skeleton of the entity
        reposeSkeleton();

        super.onUpdate();
    }

    @Override
    public void onRender() {
        MasterRenderer.renderAnimatedEntity(animatedEntity);
        super.onRender();
    }

    /**Should the animation loop, when its over
     *
     * @param loop Should the animation loop, when its over
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    /**Setting the current time of the animation
     *
     * @param currentTime Current time of the animation
     */
    public void setCurrentTime(float currentTime) {
        this.currentTime = currentTime;
        reposeSkeleton();
    }

    /**Setting the entity, that should be animated. If the entity doesnt fit to the current set animation, the entity
     * will not be setted and this method returns false
     *
     * @param animatedEntity Entity to animate
     */
    public boolean setAnimatedEntity(AnimatedEntity animatedEntity) {
        if(animation == null || Animator.checkFit(animatedEntity.getSkeleton(), animation)){
            this.animatedEntity = animatedEntity;
            return true;
        }else return false;
    }

    /**Setting the animation, that should be played. If the animation doesnt fit to the current set entity, the
     * animation will not be setted and this method returns false
     *
     * @param animation Animation to play
     */
    public boolean setAnimation(Animation animation) {
        if(animatedEntity == null || Animator.checkFit(animatedEntity.getSkeleton(), animation)){
            this.animation = animation;
            stop();
            return true;
        }else return false;
    }
}
