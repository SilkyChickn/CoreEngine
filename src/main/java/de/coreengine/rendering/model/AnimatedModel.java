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

package de.coreengine.rendering.model;

import de.coreengine.animation.Animation;
import de.coreengine.animation.Joint;

import java.util.HashMap;

public class AnimatedModel extends Model {

    // Skeleton blueprint for animated entities
    private final Joint skeleton;

    // Animations that can be played by this model
    private final HashMap<String, Animation> animations;

    /**
     * Creating new animated model
     *
     * @param meshes     Meshes of the model
     * @param skeleton   Skeleton blueprint for animated entities
     * @param animations Animations that can be played by this model
     */
    public AnimatedModel(Mesh[] meshes, Joint skeleton, HashMap<String, Animation> animations) {
        super(meshes);

        this.skeleton = skeleton;
        this.animations = animations;
    }

    /**
     * Creating a new instanceof the skeleton of this model. So every entity can
     * have its own skeleton, for individual animation poses
     *
     * @return New skeleton instance
     */
    public Joint getNewSkeletonInstance() {
        return new Joint(skeleton);
    }

    /**
     * @return Animations that can be played by this model
     */
    public HashMap<String, Animation> getAnimations() {
        return animations;
    }
}
