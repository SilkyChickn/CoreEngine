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

import de.coreengine.util.Logger;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.List;

/**Represents a animation that can be played by an animated model
 */
public class Animation {

    //Length of the animation (last keyframe)
    private float length;

    //List of all keyframe lists of the joints
    private List<KeyFrameList<Vector3f>> positionKeys;
    private List<KeyFrameList<Quat4f>> rotationKeys;
    private List<KeyFrameList<Vector3f>> scaleKeys;

    /**Creating animation and init values
     *
     * @param positionKeys Keyframe lists of the joints positions
     * @param rotationKeys Keyframe lists of the joints rotations
     * @param scaleKeys Keyframe lists of the joints scales
     */
    public Animation(List<KeyFrameList<Vector3f>> positionKeys, List<KeyFrameList<Quat4f>> rotationKeys,
                     List<KeyFrameList<Vector3f>> scaleKeys) {
        if(positionKeys.size() != rotationKeys.size() || positionKeys.size() != scaleKeys.size()){
            Logger.warn("Invalid animation data", "The passed keyframe lists, " +
                    "does hav not the same joint count!");
        }

        this.positionKeys = positionKeys;
        this.rotationKeys = rotationKeys;
        this.scaleKeys = scaleKeys;

        //Get last keyframe timestamp as animation length
        getLastKeyFrameTime();
    }

    /**Get the timestamp of the last keyframe and store it into length
     */
    private void getLastKeyFrameTime(){
        length = 0.0f;
        for(KeyFrameList<Vector3f> kfl: positionKeys){
            if(kfl.getLastTimeStamp() > length) length = kfl.getLastTimeStamp();
        }
        for(KeyFrameList<Quat4f> kfl: rotationKeys){
            if(kfl.getLastTimeStamp() > length) length = kfl.getLastTimeStamp();
        }
        for(KeyFrameList<Vector3f> kfl: scaleKeys){
            if(kfl.getLastTimeStamp() > length) length = kfl.getLastTimeStamp();
        }
    }

    /**@return Length of the animation in millis
     */
    public float getLength() {
        return length;
    }

    /**Getting the keyframe for the position keyframes of a specific joint
     *
     * @param jointId Id of the joint
     * @return Joints position keyframes
     */
    KeyFrameList<Vector3f> getPositionKeyFrames(int jointId){
        return positionKeys.get(jointId);
    }

    /**Getting the keyframe for the rotation keyframes of a specific joint
     *
     * @param jointId Id of the joint
     * @return Joints rotation keyframes
     */
    KeyFrameList<Quat4f> getRotationKeyFrames(int jointId){
        return rotationKeys.get(jointId);
    }

    /**Getting the keyframe for the scale keyframes of a specific joint
     *
     * @param jointId Id of the joint
     * @return Joints scale keyframes
     */
    KeyFrameList<Vector3f> getScaleKeyFrames(int jointId){
        return scaleKeys.get(jointId);
    }
}
