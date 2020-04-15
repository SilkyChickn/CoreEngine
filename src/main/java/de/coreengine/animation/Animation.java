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

import de.coreengine.util.ByteArrayUtils;
import de.coreengine.util.Logger;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a animation that can be played by an animated model
 */
public class Animation {

    // Length of the animation (last keyframe)
    private float length;

    // Name of the animation
    private String name;

    // List of all keyframe lists of the joints
    private List<KeyFrameList<Vector3f>> positionKeys;
    private List<KeyFrameList<Quat4f>> rotationKeys;
    private List<KeyFrameList<Vector3f>> scaleKeys;

    /**
     * Creating animation and init values
     *
     * @param name         Name of the animation
     * @param positionKeys Keyframe lists of the joints positions
     * @param rotationKeys Keyframe lists of the joints rotations
     * @param scaleKeys    Keyframe lists of the joints scales
     */
    public Animation(String name, List<KeyFrameList<Vector3f>> positionKeys, List<KeyFrameList<Quat4f>> rotationKeys,
            List<KeyFrameList<Vector3f>> scaleKeys) {
        if (positionKeys.size() != rotationKeys.size() || positionKeys.size() != scaleKeys.size()) {
            Logger.warn("Invalid animation data", "The passed keyframe lists, " + "does hav not the same joint count!");
        }

        this.name = name;
        this.positionKeys = positionKeys;
        this.rotationKeys = rotationKeys;
        this.scaleKeys = scaleKeys;

        // Get last keyframe timestamp as animation length
        getLastKeyFrameTime();
    }

    /**
     * Converting the animation into a byte array.<br>
     * <br>
     * Format:<br>
     * First Sector [MetaData]:<br>
     * NameSize (int) | PositionKeyListCount (int) | RotationKeyListCount (int) |
     * ScaleKeyListCount (int) | PositionKeyList0KeyCount (int) |
     * PositionKeyList1KeyCount (int) | ... | RotationKeyList0KeyCount (int) |
     * RotationKeyList1KeyCount (int) | ... | ScaleKeyList0KeyCount (int) |
     * ScaleKeyList1KeyCount (int) | ...<br>
     * <br>
     * Second Sector [PositionKeys]:<br>
     * Name (String)<br>
     * <br>
     * Third Sector [PositionKeys]:<br>
     * PositionKeyList0Time0 (float) | PositionKeyList0Vec0 (float[]) |
     * PositionKeyList0Time1 (float) | PositionKeyList0Vec1 (float[]) | ... |
     * PositionKeyList1Time0 (float) | PositionKeyList1Vec0 (float[]) |
     * PositionKeyList1Time1 (float) | PositionKeyList1Vec1 (float[]) | ...<br>
     * <br>
     * Fourth Sector [RotationKeys]:<br>
     * RotationKeyList0Time0 (float) | RotationKeyList0Vec0 (float[]) |
     * RotationKeyList0Time1 (float) | RotationKeyList0Vec1 (float[]) | ... |
     * RotationKeyList1Time0 (float) | RotationKeyList1Vec0 (float[]) |
     * RotationKeyList1Time1 (float) | RotationKeyList1Vec1 (float[]) | ...<br>
     * <br>
     * Fifth Sector [ScaleKeys]:<br>
     * ScaleKeyList0Time0 (float) | ScaleKeyList0Vec0 (float[]) | ScaleKeyList0Time1
     * (float) | ScaleKeyList0Vec1 (float[]) | ... | ScaleKeyList1Time0 (float) |
     * ScaleKeyList1Vec0 (float[]) | ScaleKeyList1Time1 (float) | ScaleKeyList1Vec1
     * (float[]) | ...<br>
     *
     * @return Converted byte array
     */
    public byte[] toBytes() {

        // Create meta data
        byte[] prsListsCount = ByteArrayUtils
                .toBytes(new int[] { name.length(), positionKeys.size(), rotationKeys.size(), scaleKeys.size() });

        // Get position keys data
        int[] positionKeyListsKeyCountsI = new int[positionKeys.size()];
        byte[][] positionKeyListsKeys = new byte[positionKeys.size()][];
        for (int i = 0; i < positionKeys.size(); i++) {
            positionKeyListsKeyCountsI[i] = positionKeys.get(i).getKeyFrames().size();
            float[] keys = new float[positionKeys.get(i).getKeyFrames().size() * 4];
            for (int j = 0; j < positionKeys.get(i).getKeyFrames().size(); j++) {
                keys[j * 4] = positionKeys.get(i).getKeyFrames().get(j).getTimestamp();
                keys[j * 4 + 1] = positionKeys.get(i).getKeyFrames().get(j).getStatus().x;
                keys[j * 4 + 2] = positionKeys.get(i).getKeyFrames().get(j).getStatus().y;
                keys[j * 4 + 3] = positionKeys.get(i).getKeyFrames().get(j).getStatus().z;
            }
            positionKeyListsKeys[i] = ByteArrayUtils.toBytes(keys);
        }
        byte[] positionKeyListsKeyCounts = ByteArrayUtils.toBytes(positionKeyListsKeyCountsI);
        byte[] positionKeys = ByteArrayUtils.combine(positionKeyListsKeys);

        // Get rotation keys data
        int[] rotationKeyListsKeyCountsI = new int[rotationKeys.size()];
        byte[][] rotationKeyListsKeys = new byte[rotationKeys.size()][];
        for (int i = 0; i < rotationKeys.size(); i++) {
            rotationKeyListsKeyCountsI[i] = rotationKeys.get(i).getKeyFrames().size();
            float[] keys = new float[rotationKeys.get(i).getKeyFrames().size() * 5];
            for (int j = 0; j < rotationKeys.get(i).getKeyFrames().size(); j++) {
                keys[j * 5] = rotationKeys.get(i).getKeyFrames().get(j).getTimestamp();
                keys[j * 5 + 1] = rotationKeys.get(i).getKeyFrames().get(j).getStatus().x;
                keys[j * 5 + 2] = rotationKeys.get(i).getKeyFrames().get(j).getStatus().y;
                keys[j * 5 + 3] = rotationKeys.get(i).getKeyFrames().get(j).getStatus().z;
                keys[j * 5 + 4] = rotationKeys.get(i).getKeyFrames().get(j).getStatus().w;
            }
            rotationKeyListsKeys[i] = ByteArrayUtils.toBytes(keys);
        }
        byte[] rotationKeyListsKeyCounts = ByteArrayUtils.toBytes(rotationKeyListsKeyCountsI);
        byte[] rotationKeys = ByteArrayUtils.combine(rotationKeyListsKeys);

        // Get scale keys data
        int[] scaleKeyListsKeyCountsI = new int[scaleKeys.size()];
        byte[][] scaleKeyListsKeys = new byte[scaleKeys.size()][];
        for (int i = 0; i < scaleKeys.size(); i++) {
            scaleKeyListsKeyCountsI[i] = scaleKeys.get(i).getKeyFrames().size();
            float[] keys = new float[scaleKeys.get(i).getKeyFrames().size() * 4];
            for (int j = 0; j < scaleKeys.get(i).getKeyFrames().size(); j++) {
                keys[j * 4] = scaleKeys.get(i).getKeyFrames().get(j).getTimestamp();
                keys[j * 4 + 1] = scaleKeys.get(i).getKeyFrames().get(j).getStatus().x;
                keys[j * 4 + 2] = scaleKeys.get(i).getKeyFrames().get(j).getStatus().y;
                keys[j * 4 + 3] = scaleKeys.get(i).getKeyFrames().get(j).getStatus().z;
            }
            scaleKeyListsKeys[i] = ByteArrayUtils.toBytes(keys);
        }
        byte[] scaleKeyListsKeyCounts = ByteArrayUtils.toBytes(scaleKeyListsKeyCountsI);
        byte[] scaleKeys = ByteArrayUtils.combine(scaleKeyListsKeys);

        // Combine and return
        return ByteArrayUtils.combine(prsListsCount, positionKeyListsKeyCounts, rotationKeyListsKeyCounts,
                scaleKeyListsKeyCounts, name.getBytes(), positionKeys, rotationKeys, scaleKeys);
    }

    /**
     * Constructing animation from bytes.<br>
     * <br>
     * Format:<br>
     * First Sector [MetaData]:<br>
     * NameSize (int) | PositionKeyListCount (int) | RotationKeyListCount (int) |
     * ScaleKeyListCount (int) | PositionKeyList0KeyCount (int) |
     * PositionKeyList1KeyCount (int) | ... | RotationKeyList0KeyCount (int) |
     * RotationKeyList1KeyCount (int) | ... | ScaleKeyList0KeyCount (int) |
     * ScaleKeyList1KeyCount (int) | ...<br>
     * <br>
     * Second Sector [PositionKeys]:<br>
     * Name (String)<br>
     * <br>
     * Third Sector [PositionKeys]:<br>
     * PositionKeyList0Time0 (float) | PositionKeyList0Vec0 (float[]) |
     * PositionKeyList0Time1 (float) | PositionKeyList0Vec1 (float[]) | ... |
     * PositionKeyList1Time0 (float) | PositionKeyList1Vec0 (float[]) |
     * PositionKeyList1Time1 (float) | PositionKeyList1Vec1 (float[]) | ...<br>
     * <br>
     * Fourth Sector [RotationKeys]:<br>
     * RotationKeyList0Time0 (float) | RotationKeyList0Vec0 (float[]) |
     * RotationKeyList0Time1 (float) | RotationKeyList0Vec1 (float[]) | ... |
     * RotationKeyList1Time0 (float) | RotationKeyList1Vec0 (float[]) |
     * RotationKeyList1Time1 (float) | RotationKeyList1Vec1 (float[]) | ...<br>
     * <br>
     * Fifth Sector [ScaleKeys]:<br>
     * ScaleKeyList0Time0 (float) | ScaleKeyList0Vec0 (float[]) | ScaleKeyList0Time1
     * (float) | ScaleKeyList0Vec1 (float[]) | ... | ScaleKeyList1Time0 (float) |
     * ScaleKeyList1Vec0 (float[]) | ScaleKeyList1Time1 (float) | ScaleKeyList1Vec1
     * (float[]) | ...<br>
     *
     * @param data Bytes to construct animation from
     */
    public void fromBytes(byte[] data) {

        // Get meta data
        int counter = 0;
        int[] prsListCount = ByteArrayUtils.fromBytesi(Arrays.copyOfRange(data, counter, counter += 16));

        int[] positionKeyListsKeyCounts = ByteArrayUtils
                .fromBytesi(Arrays.copyOfRange(data, counter, counter += prsListCount[1] * 4));
        int[] rotationKeyListsKeyCounts = ByteArrayUtils
                .fromBytesi(Arrays.copyOfRange(data, counter, counter += prsListCount[2] * 4));
        int[] scaleKeyListsKeyCounts = ByteArrayUtils
                .fromBytesi(Arrays.copyOfRange(data, counter, counter += prsListCount[3] * 4));

        // Get name
        this.name = new String(Arrays.copyOfRange(data, counter, counter += prsListCount[0]));

        // Get position keys
        this.positionKeys.clear();
        for (int i = 0; i < prsListCount[1]; i++) {
            KeyFrameList<Vector3f> keys = new KeyFrameList<>();
            for (int j = 0; j < positionKeyListsKeyCounts[i]; j++) {
                float[] keyData = ByteArrayUtils.fromBytesf(Arrays.copyOfRange(data, counter, counter += 16));
                keys.addKeyFrame(new KeyFrame<>(keyData[0], new Vector3f(keyData[1], keyData[2], keyData[3])));
            }
            this.positionKeys.add(keys);
        }

        // Get rotation keys
        this.rotationKeys.clear();
        for (int i = 0; i < prsListCount[2]; i++) {
            KeyFrameList<Quat4f> keys = new KeyFrameList<>();
            for (int j = 0; j < rotationKeyListsKeyCounts[i]; j++) {
                float[] keyData = ByteArrayUtils.fromBytesf(Arrays.copyOfRange(data, counter, counter += 20));
                keys.addKeyFrame(
                        new KeyFrame<>(keyData[0], new Quat4f(keyData[1], keyData[2], keyData[3], keyData[4])));
            }
            this.rotationKeys.add(keys);
        }

        // Get scale keys
        this.scaleKeys.clear();
        for (int i = 0; i < prsListCount[3]; i++) {
            KeyFrameList<Vector3f> keys = new KeyFrameList<>();
            for (int j = 0; j < scaleKeyListsKeyCounts[i]; j++) {
                float[] keyData = ByteArrayUtils.fromBytesf(Arrays.copyOfRange(data, counter, counter += 16));
                keys.addKeyFrame(new KeyFrame<>(keyData[0], new Vector3f(keyData[1], keyData[2], keyData[3])));
            }
            this.scaleKeys.add(keys);
        }

        // Recalculate animation length
        getLastKeyFrameTime();
    }

    /**
     * Get the timestamp of the last keyframe and store it into length
     */
    private void getLastKeyFrameTime() {
        length = 0.0f;
        for (KeyFrameList<Vector3f> kfl : positionKeys) {
            if (kfl.getLastTimeStamp() > length)
                length = kfl.getLastTimeStamp();
        }
        for (KeyFrameList<Quat4f> kfl : rotationKeys) {
            if (kfl.getLastTimeStamp() > length)
                length = kfl.getLastTimeStamp();
        }
        for (KeyFrameList<Vector3f> kfl : scaleKeys) {
            if (kfl.getLastTimeStamp() > length)
                length = kfl.getLastTimeStamp();
        }
    }

    /**
     * @return Length of the animation in millis
     */
    public float getLength() {
        return length;
    }

    /**
     * Getting the keyframe for the position keyframes of a specific joint
     *
     * @param jointId Id of the joint
     * @return Joints position keyframes
     */
    KeyFrameList<Vector3f> getPositionKeyFrames(int jointId) {
        return positionKeys.get(jointId);
    }

    /**
     * Getting the keyframe for the rotation keyframes of a specific joint
     *
     * @param jointId Id of the joint
     * @return Joints rotation keyframes
     */
    KeyFrameList<Quat4f> getRotationKeyFrames(int jointId) {
        return rotationKeys.get(jointId);
    }

    /**
     * Getting the keyframe for the scale keyframes of a specific joint
     *
     * @param jointId Id of the joint
     * @return Joints scale keyframes
     */
    KeyFrameList<Vector3f> getScaleKeyFrames(int jointId) {
        return scaleKeys.get(jointId);
    }

    /**
     * @return Name of the animation
     */
    public String getName() {
        return name;
    }
}
