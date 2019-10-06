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

package de.coreengine.animation;

import javafx.util.Pair;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.List;

public class Animator {

    /**Setting a skeleton into the current pose of an animation
     *
     * @param skeleton Skeleton to animate
     * @param animation Animation to play
     * @param time Current time of the animation
     */
    public static void applyAnimation(Joint skeleton, Animation animation, float time){

        //Get relevant keyframes from list
        Pair<KeyFrame<Vector3f>, KeyFrame<Vector3f>> relevantPositionKfs =
                animation.getPositionKeyFrames(skeleton.getIndex()).getRelevantKeyFrames(time);
        Pair<KeyFrame<Quat4f>, KeyFrame<Quat4f>> relevantRotationKfs =
                animation.getRotationKeyFrames(skeleton.getIndex()).getRelevantKeyFrames(time);
        //Pair<KeyFrame<Vector3f>, KeyFrame<Vector3f>> relevantScaleKfs =
        //        animation.getScaleKeyFrames(child.getIndex()).getRelevantKeyFrames(time);

        //Get interpolated matrix between the two keyframes
        Vector3f position = getInterpolatedVector(relevantPositionKfs, time);
        Quat4f rotation = getInterpolatedQuaternion(relevantRotationKfs, time);
        //Vector3f scale = getInterpolatedVector(relevantScaleKfs, time);
        Matrix4f interpolatedMatrix = new Matrix4f(rotation, position, 1.0f);

        skeleton.setLocalPose(interpolatedMatrix);

        //Animate all children
        for(Joint child: skeleton.getChildren()) applyAnimation(child, interpolatedMatrix, animation, time);

        //Recalculate animation matrices from local poses
        skeleton.calcAnimatedTransform();
    }

    /**Setting a joint and all its children into the current pose of an animation
     *
     * @param child Joint to animate
     * @param animation Animation to play
     * @param time Current time of the animation
     */
    private static void applyAnimation(Joint child, Matrix4f parentMatrix, Animation animation, float time){

        //Get relevant keyframes from list
        Pair<KeyFrame<Vector3f>, KeyFrame<Vector3f>> relevantPositionKfs =
                animation.getPositionKeyFrames(child.getIndex()).getRelevantKeyFrames(time);
        Pair<KeyFrame<Quat4f>, KeyFrame<Quat4f>> relevantRotationKfs =
                animation.getRotationKeyFrames(child.getIndex()).getRelevantKeyFrames(time);
        //Pair<KeyFrame<Vector3f>, KeyFrame<Vector3f>> relevantScaleKfs =
        //        animation.getScaleKeyFrames(child.getIndex()).getRelevantKeyFrames(time);

        //Get interpolated matrix between the two keyframes
        Vector3f position = getInterpolatedVector(relevantPositionKfs, time);
        Quat4f rotation = getInterpolatedQuaternion(relevantRotationKfs, time);
        //Vector3f scale = getInterpolatedVector(relevantScaleKfs, time);
        Matrix4f interpolatedMatrix = new Matrix4f(rotation, position, 1.0f);

        //Mul with parent matrix and set as joints local matrix
        Matrix4f localPose = new Matrix4f(parentMatrix);
        localPose.mul(interpolatedMatrix);
        child.setLocalPose(localPose);

        //Animate all children
        for(Joint c: child.getChildren()) applyAnimation(c, interpolatedMatrix, animation, time);
    }

    /**Calculate the interpolated value of two vector3f keyframes
     *
     * @param keyFrames Previous and next keyframe
     * @param time Current time in the animation
     * @return Interpolated vector3f
     */
    private static Vector3f getInterpolatedVector(Pair<KeyFrame<Vector3f>, KeyFrame<Vector3f>> keyFrames, float time){
        Vector3f result = new Vector3f();

        if(keyFrames.getValue() == null && keyFrames.getKey() != null){

            //There is no next keyframe, so return the transform from the last keyframe
            result.set(keyFrames.getKey().getStatus());
        }else if(keyFrames.getKey() != null){

            //Calculate percentage progression between the two keyframes
            float diffrence = keyFrames.getValue().getTimestamp() -keyFrames.getKey().getTimestamp();
            float progression = 1.0f * (time -keyFrames.getKey().getTimestamp()) / diffrence;

            //Calculate interpolated translation
            result.interpolate(keyFrames.getKey().getStatus(), keyFrames.getValue().getStatus(), progression);
        }

        return result;
    }

    /**Calculate the interpolated value of two quaternion keyframes
     *
     * @param keyFrames Previous and next keyframe
     * @param time Current time in the animation
     * @return Interpolated quaternion
     */
    private static Quat4f getInterpolatedQuaternion(Pair<KeyFrame<Quat4f>, KeyFrame<Quat4f>> keyFrames, float time){
        Quat4f result = new Quat4f();

        if(keyFrames.getValue() == null && keyFrames.getKey() != null){

            //There is no next keyframe, so return the transform from the last keyframe
            result.set(keyFrames.getKey().getStatus());
        }else if(keyFrames.getKey() != null){

            //Calculate percentage progression between the two keyframes
            float diffrence = keyFrames.getValue().getTimestamp() -keyFrames.getKey().getTimestamp();
            float progression = 1.0f * (time -keyFrames.getKey().getTimestamp()) / diffrence;

            //Calculate interpolated translation
            result.interpolate(keyFrames.getKey().getStatus(), keyFrames.getValue().getStatus(), progression);
        }

        return result;
    }
}
