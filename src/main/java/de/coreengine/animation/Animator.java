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

    /**Check if an skeleton can play an specific animation
     *
     * @return True if its fit, else false
     */
    public static boolean checkFit(Joint skeleton, Animation animation){
        if(skeleton == null || animation == null) return false;

        //Check fit for this joint
        if(skeleton.getIndex() < 0 || skeleton.getIndex() >= animation.getKeyFrames().size())
            return false;

        //Chek fit for all children recursively
        for(Joint children: skeleton.getChildren()){
            if(!checkFit(children, animation)) return false;
        }

        return true;
    }

    /**Setting a skeleton into the current pose of an animation
     *
     * @param skeleton Skeleton to animate
     * @param animation Animation to play
     * @param time Current time of the animation
     */
    public static void applyAnimation(Joint skeleton, Animation animation, float time){

        //Get relevant keyframes from list
        List<KeyFrame> keyFrames = animation.getKeyFrames().get(skeleton.getIndex());
        Pair<KeyFrame, KeyFrame> relevant = getRelevantKeyFrames(keyFrames, time);

        //Get interpolated matrix between the two keyframes and set as joints local matrix
        Matrix4f interpolatedMatrix = getInterpolatedMatrix(relevant, time);
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
        List<KeyFrame> keyFrames = animation.getKeyFrames().get(child.getIndex());
        Pair<KeyFrame, KeyFrame> relevant = getRelevantKeyFrames(keyFrames, time);

        //Get interpolated matrix between the two keyframes, mul with parent matrix and set as joints local matrix
        Matrix4f interpolatedMatrix = getInterpolatedMatrix(relevant, time);
        Matrix4f localPose = new Matrix4f(parentMatrix);
        localPose.mul(interpolatedMatrix);
        child.setLocalPose(localPose);

        //Animate all children
        for(Joint c: child.getChildren()) applyAnimation(c, interpolatedMatrix, animation, time);
    }

    /**Interpolate the matrices between two keyframes
     *
     * @param keyFrames Keyframes pair (Previous, Next)
     * @param time Time of the animation
     * @return Interpolated Matrix
     */
    private static Matrix4f getInterpolatedMatrix(Pair<KeyFrame, KeyFrame> keyFrames, float time){
        Matrix4f result = new Matrix4f();

        if(keyFrames.getValue() == null){

            //There is no next keyframe, so return the transform from the last keyframe
            result.set(keyFrames.getKey().getRotation(), keyFrames.getKey().getTranslation(), 1.0f);
        }else if(keyFrames.getKey() == null){

            //There is no previous keyframe, so return an identity matrix
            result.setIdentity();
        }else{

            //Calculate percentage progression between the two keyframes
            float diffrence = keyFrames.getValue().getTimestamp() -keyFrames.getKey().getTimestamp();
            float progression = 1.0f * (time -keyFrames.getKey().getTimestamp()) / diffrence;

            //Calculate interpolated rotation
            Quat4f interpolatedQuaternion = new Quat4f();
            interpolatedQuaternion.interpolate(keyFrames.getKey().getRotation(),
                    keyFrames.getValue().getRotation(), progression);

            //Calculate interpolated translation
            Vector3f interpolatedTranslation = new Vector3f();
            interpolatedTranslation.interpolate(keyFrames.getKey().getTranslation(),
                    keyFrames.getValue().getTranslation(), progression);

            //Set resulting matrix
            result.set(interpolatedQuaternion, interpolatedTranslation, 1.0f);
        }

        return result;
    }

    /**Finding previous and next keyframe for a specific time stamp. If no previous keyframe exist, the key of the
     * result is null. If no next keyframe exist, the value of the result is null.
     *
     * @param keyFrames List of keyframes to search in
     * @param time Current timestamp
     * @return Pair of the previous as key and the next keyframe as value
     */
    private static Pair<KeyFrame, KeyFrame> getRelevantKeyFrames(List<KeyFrame> keyFrames, float time){
        KeyFrame previous = null, next = null;

        //Iterate through key frames to find next and previous
        for(int i = 0; i < keyFrames.size(); i++){

            //Is greater, so next keyframe found
            if(keyFrames.get(i).getTimestamp() > time){
                next = keyFrames.get(i);

                //Check if next keyframe is first, so previous doesnt exist
                if(i == 0) previous = null;
                else previous = keyFrames.get(i -1);

                break;
            }
        }

        //If time is over last keyframe set last keyframe as previous
        if(next == null) previous = keyFrames.get(keyFrames.size() -1);

        return new Pair<>(previous, next);
    }
}
