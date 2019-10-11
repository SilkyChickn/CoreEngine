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

package de.coreengine.asset.dataStructures;

import de.coreengine.animation.Animation;
import de.coreengine.animation.Joint;
import de.coreengine.rendering.model.AnimatedModel;
import de.coreengine.rendering.model.Mesh;
import de.coreengine.util.ByteArrayUtils;
import de.coreengine.util.Logger;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AnimatedModelData extends ModelData {

    //Data
    public Joint skeleton = null;
    public HashMap<String, Animation> animations = null;

    /**Constructing this animated model data from bytes.<br>
     * <br>
     * Format:<br>
     * First Sector [MetaData]:<br>
     * ModelSize (int) | SkeletonSize (int) | AnimationCount (int) |
     * Animation0Size (int) | Animation1Size (int) | ...<br>
     * <br>
     * Second Sector [ModelData]:<br>
     * Model (ModelData)<br>
     * <br>
     * Third Sector [Skeleton]:<br>
     * Skeleton (Joint)<br>
     * <br>
     * Fourth Sector [Animations]:<br>
     * Animation0 (Animation) | Animation1 (Animation) | ...<br>
     *
     * @param data Bytes to construct animated model data from
     */
    @Override
    public void fromBytes(byte[] data){

        //Get meta data
        int counter = 0;
        int[] modelSkeletonAnimationSizes = ByteArrayUtils.fromBytesi(Arrays.copyOfRange(data, counter, counter += 12));
        int[] animationSizes = ByteArrayUtils.fromBytesi(
                Arrays.copyOfRange(data, counter, counter += modelSkeletonAnimationSizes[2]*4));

        //Get model data
        super.fromBytes(Arrays.copyOfRange(data, counter, counter += modelSkeletonAnimationSizes[0]));

        //Get skeleton data
        if(modelSkeletonAnimationSizes[1] > 0){
            this.skeleton = new Joint(0, "", new Matrix4f(), new Matrix4f());
            this.skeleton.fromBytes(Arrays.copyOfRange(data, counter, counter += modelSkeletonAnimationSizes[1]));
            this.skeleton.calcBindPose(null);
            this.skeleton.calcAnimatedTransformAndPose(null);
        }else this.skeleton = null;

        //Get animation data
        if(animationSizes.length == 0) this.animations = null;
        else{
            this.animations = new HashMap<>();
            for(int i = 0; i < animationSizes.length; i++){
                Animation animation = new Animation("", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                animation.fromBytes(Arrays.copyOfRange(data, counter, counter += animationSizes[i]));
                this.animations.put(animation.getName(), animation);
            }
        }
    }

    /**Converting the dataStructure animated model into a byte array.<br>
     * <br>
     * Format:<br>
     * First Sector [MetaData]:<br>
     * ModelSize (int) | SkeletonSize (int) | AnimationCount (int) |
     * Animation0Size (int) | Animation1Size (int) | ...<br>
     * <br>
     * Second Sector [ModelData]:<br>
     * Model (ModelData)<br>
     * <br>
     * Third Sector [Skeleton]:<br>
     * Skeleton (Joint)<br>
     * <br>
     * Fourth Sector [Animations]:<br>
     * Animation0 (Animation) | Animation1 (Animation) | ...<br>
     *
     * @return Converted byte array
     */
    @Override
    public byte[] toBytes(){

        //Get model and skeleton bytes
        byte[] modelData = super.toBytes();
        byte[] skeletonData = skeleton == null ? new byte[0] : skeleton.toBytes();

        //Create meta data
        int[] modelSkeletonAnimationsSizesI = new int[] {
                modelData.length,
                skeleton == null ? 0 : skeletonData.length,
                animations == null ? 0 : animations.size()
        };
        byte[] modelSkeletonAnimationsSizes = ByteArrayUtils.toBytes(modelSkeletonAnimationsSizesI);

        //Create animations data
        byte[][] animationsA = new byte[modelSkeletonAnimationsSizesI[2]][];
        int[] animationsSizesI = new int[animationsA.length];
        if(animations != null){
            int c = 0;
            for(String animationName: animations.keySet()){
                Animation animation = animations.get(animationName);
                animationsA[c] = animation.toBytes();
                animationsSizesI[c] = animationsA[c].length;
            }
        }
        byte[] animationSizes = ByteArrayUtils.toBytes(animationsSizesI);
        byte[] animations = ByteArrayUtils.combine(animationsA);

        //Combine and return
        return ByteArrayUtils.combine(modelSkeletonAnimationsSizes,
                animationSizes, modelData, skeletonData, animations);
    }

    /**Creates new animated model instance of the dataStructure animated model
     *
     * @param texPath Path to get models textures from
     * @param asResource Load model textures from resources
     * @return Create model instance
     */
    @Override
    public AnimatedModel getInstance(String texPath, boolean asResource){
        if(this.meshes == null){
            Logger.warn("Error by creating animated model instance",
                    "The meshes array of the dataStructures animated model is null! Returning null");
            return null;
        }

        Mesh[] meshes = new Mesh[this.meshes.length];

        //Create all mesh instances
        for(int i = 0; i < this.meshes.length; i++){
            meshes[i] = this.meshes[i].getInstance(texPath, asResource, true);
        }

        return new AnimatedModel(meshes, skeleton, animations);
    }
}
