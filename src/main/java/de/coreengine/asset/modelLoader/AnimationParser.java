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

package de.coreengine.asset.modelLoader;

import de.coreengine.animation.Animation;
import de.coreengine.animation.KeyFrame;
import de.coreengine.animation.KeyFrameList;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIVectorKey;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class AnimationParser {

    //Input
    private final AIAnimation aiAnimation;

    //Output
    private Animation animation;
    private String name;

    /**Creates new animation data to parse an ai animation
     *
     * @param aiAnimation AIAnimation to parse
     */
    public AnimationParser(AIAnimation aiAnimation) {
        this.aiAnimation = aiAnimation;
    }

    /**Parsing animation data from ai animation
     *
     * @param bones Loaded bones
     */
    public void parse(List<BoneParser> bones){

        //Get animation name
        name = aiAnimation.mName().dataString();

        //Prepare animation data
        List<KeyFrameList<Vector3f>> positionKeys = new ArrayList<>();
        List<KeyFrameList<Quat4f>> rotationKeys = new ArrayList<>();
        List<KeyFrameList<Vector3f>> scaleKeys = new ArrayList<>();

        //Initialize lists
        for(int i = 0; i < bones.size(); i++){
            positionKeys.add(new KeyFrameList<>());
            rotationKeys.add(new KeyFrameList<>());
            scaleKeys.add(new KeyFrameList<>());
        }

        //Fill up keyframe lists
        int channelCount = aiAnimation.mNumChannels();
        for(int i = 0; i < channelCount; i++){
            AINodeAnim aiChannel = AINodeAnim.create(aiAnimation.mChannels().get(i));

            //Get bone id
            int id = 0;
            for(int b = 0; b < bones.size(); b++){
                if(bones.get(b).getName().equals(aiChannel.mNodeName().dataString())){
                    id = b;
                    break;
                }
            }

            //Add position keys
            for(int kid = 0; kid < aiChannel.mNumPositionKeys(); kid++){
                AIVectorKey key = aiChannel.mPositionKeys().get(kid);
                Vector3f vec = new Vector3f(key.mValue().x(), key.mValue().y(), key.mValue().z());
                positionKeys.get(id).addKeyFrame(new KeyFrame<>((float)key.mTime(), vec));
            }

            //Add rotation keys
            for(int kid = 0; kid < aiChannel.mNumRotationKeys(); kid++){
                AIQuatKey key = aiChannel.mRotationKeys().get(kid);
                Quat4f quat = new Quat4f(key.mValue().x(), key.mValue().y(), key.mValue().z(), key.mValue().w());
                rotationKeys.get(id).addKeyFrame(new KeyFrame<>((float)key.mTime(), quat));
            }

            //Add scale keys
            for(int kid = 0; kid < aiChannel.mNumScalingKeys(); kid++){
                AIVectorKey key = aiChannel.mScalingKeys().get(kid);
                Vector3f vec = new Vector3f(key.mValue().x(), key.mValue().y(), key.mValue().z());
                scaleKeys.get(id).addKeyFrame(new KeyFrame<>((float)key.mTime(), vec));
            }
        }

        //Create animation
        animation = new Animation(name, positionKeys, rotationKeys, scaleKeys);
    }

    /**@return Parsed animation name
     */
    public String getName() {
        return name;
    }

    /**@return Parsed animation
     */
    public Animation getAnimation() {
        return animation;
    }
}
