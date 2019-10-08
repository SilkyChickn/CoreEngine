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

import java.util.ArrayList;
import java.util.List;

/**Class that stores a list of keyframes for a specific component
 *
 * @param <Component> Component of the keyframes
 */
public class KeyFrameList<Component>{

    private List<KeyFrame<Component>> keyFrames = new ArrayList<>();

    /**Adding a keyframe to the end of the list
     *
     * @param keyFrame Keyframe to add
     */
    public void addKeyFrame(KeyFrame<Component> keyFrame){
        keyFrames.add(keyFrame);
    }

    /**@return Last timestamp of this list
     */
    public float getLastTimeStamp(){
        if(keyFrames.isEmpty()) return 0;
        return keyFrames.get(keyFrames.size() -1).getTimestamp();
    }

    /**Finding previous and next keyframe for a specific time stamp. If no previous keyframe exist, the key of the
     * result is null. If no next keyframe exist, the value of the result is null. If no keyframes exist, both
     * value and key are null.
     *
     * @param time Current timestamp
     * @return Pair of the previous as key and the next keyframe as value
     */
    public Pair<KeyFrame<Component>, KeyFrame<Component>> getRelevantKeyFrames(float time){
        KeyFrame previous = null, next = null;

        if(keyFrames.size() == 0) return new Pair<>(null, null);

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