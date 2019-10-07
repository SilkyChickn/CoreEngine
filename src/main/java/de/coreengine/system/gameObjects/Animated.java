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
import de.coreengine.asset.AssetDatabase;
import de.coreengine.rendering.model.AnimatedModel;
import de.coreengine.rendering.renderable.AnimatedEntity;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;
import de.coreengine.util.FrameTimer;

import javax.vecmath.Matrix4f;
import java.util.HashSet;
import java.util.Set;

public class Animated extends GameObject {

    //Animated entity to play animation on
    private AnimatedEntity animatedEntity = null;

    //Current playing animation name
    private String currentAnimation = null;

    //Speed of the animation
    private float animationSpeed = 1.0f;

    //Current time of the animation
    private float currentTime = 0;

    //Should the animation looping
    private boolean loop = true;

    //Is the animation paused
    private boolean pause = true;

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
        if(currentAnimation == null) return;
        Animator.applyAnimation(animatedEntity.getSkeleton(), getCurrentAnimation(), currentTime);
    }

    @Override
    public void onUpdate() {

        //If animation is paused or no animation or entity set, update children and return
        if(pause || animatedEntity == null || currentAnimation == null) {
            super.onUpdate();
            return;
        }

        //Increase animation time
        currentTime += FrameTimer.getTslf() * animationSpeed;

        //Is animation finished
        Animation curAnimation = getCurrentAnimation();
        if(currentTime >= curAnimation.getLength()){
            if(loop) currentTime %= curAnimation.getLength();
            else {
                currentTime = curAnimation.getLength();
                pause();
            }
        }

        //Animate skeleton of the entity
        reposeSkeleton();

        super.onUpdate();
    }

    /**@return Current playing animation or null, if no animation selected
     */
    private Animation getCurrentAnimation(){
        return AssetDatabase.getAnimatedModel(animatedEntity.getModel()).getAnimations().get(currentAnimation);
    }

    /**Setting the speed of the animation.
     * (curFrame += frameTime * animationSpeed)
     *
     * @param animationSpeed New animation speed
     */
    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    /**@return All available animations
     */
    public Set<String> getAnimations(){
        if(animatedEntity == null) return new HashSet<>();
        else return AssetDatabase.getAnimatedModel(animatedEntity.getModel()).getAnimations().keySet();
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

    /**Setting the entity, that should be animated. Unset the current set animation.
     *
     * @param animatedEntity Entity to animate
     */
    public void setAnimatedEntity(AnimatedEntity animatedEntity) {
        this.animatedEntity = animatedEntity;
        this.currentAnimation = null;
        stop();
    }

    /**Setting the animation, that should be played. If the animation doesnt exist or the anmated entity isnt set,
     * animation will not be setted and this method returns false. Pass null to unset animation
     *
     * @param animation Animation to play
     */
    public boolean setAnimation(String animation) {
        AnimatedModel model = AssetDatabase.getAnimatedModel(animatedEntity.getModel());
        if(animatedEntity != null && model.getAnimations().containsKey(animation)){
            this.currentAnimation = animation;
            stop();
            return true;
        }else return false;
    }

    /**@return Current set entity
     */
    public AnimatedEntity getEntity() {
        return animatedEntity;
    }
}
