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
package de.coreengine.asset.meta;

import de.coreengine.rendering.model.Mesh;
import de.coreengine.rendering.model.Model;
import de.coreengine.util.ByteArrayUtils;
import de.coreengine.util.Logger;

import java.util.Arrays;

/**Meta model file that can be saved in a file
 *
 * @author Darius Dinger
 */
public class MetaModel{
    
    //Data
    public MetaMesh[] meshes = null;

    /**Constructing meta model from a byte array.<br>
     * <br>
     * Format:<br>
     * First Sector [MetaData]:<br>
     * MeshCount (int) | Mesh0Size (int) | Mesh1Size (int) | ...<br>
     * <br>
     * Second Sector [MeshData]:<br>
     * Mesh0 (MetaMesh) | Mesh1 (MetaMesh) | ...<br>
     *
     * @param data Data to construct meta model from
     */
    public void fromBytes(byte[] data){

        //Get mesh count
        byte[] meshCountB = Arrays.copyOfRange(data, 0, 4);
        int meshCount = ByteArrayUtils.fromBytesi(meshCountB)[0];

        //If no meshes return
        if(meshCount == 0){
            meshes = null;
            return;
        }

        //Get mesh sizes
        int counter = 4;
        byte[] meshSizesB = Arrays.copyOfRange(data, counter, counter += (meshCount*4));
        int[] meshSizes = ByteArrayUtils.fromBytesi(meshSizesB);

        //Get mesh data
        meshes = new MetaMesh[meshCount];
        for(int i = 0; i < meshCount; i++){
            meshes[i] = new MetaMesh();
            meshes[i].fromBytes(Arrays.copyOfRange(data, counter, counter += meshSizes[i]));
        }
    }

    /**Converting the meta model into a byte array.<br>
     * <br>
     * Format:<br>
     * First Sector [MetaData]:<br>
     * MeshCount (int) | Mesh0Size (int) | Mesh1Size (int) | ...<br>
     * <br>
     * Second Sector [MeshData]:<br>
     * mMesh0 (MetaMesh) | Mesh1 (MetaMesh) | ...<br>
     *
     * @return Converted byte array
     */
    public byte[] toBytes(){

        //Create mesh data
        byte[][] meshDataA;
        if(meshes != null){
            meshDataA = new byte[meshes.length][];
            for(int i = 0; i < meshes.length; i++){
                meshDataA[i] = meshes[i].toBytes();
            }
        }else meshDataA = new byte[0][];
        byte[] meshData = ByteArrayUtils.combine(meshDataA);

        //Get meta data
        int[] meshCountI = meshes == null ? new int[] {0} : new int[] { meshes.length };
        byte[] meshCount = ByteArrayUtils.toBytes(meshCountI);

        int[] meshSizesI;
        if(meshes != null){
            meshSizesI = new int[meshes.length];
            for(int i = 0; i < meshes.length; i++){
                meshSizesI[i] = meshDataA[i].length;
            }
        }else meshSizesI = new int[0];
        byte[] meshSizes = ByteArrayUtils.toBytes(meshSizesI);

        //Combine and return
        return ByteArrayUtils.combine(meshCount, meshSizes, meshData);
    }

    /**Creates new model instance of the meta model
     *
     * @param texPath Path to get models textures from
     * @param asResource Load model textures from resources
     * @return Create model instance
     */
    public Model getInstance(String texPath, boolean asResource){
        if(this.meshes == null){
            Logger.warn("Error by creating model instance",
                    "The meshes array of the meta model is null! Returning null");
            return null;
        }

        Mesh[] meshes = new Mesh[this.meshes.length];

        //Create all mesh instances
        for(int i = 0; i < this.meshes.length; i++){
            meshes[i] = this.meshes[i].getInstance(texPath, asResource, false);
        }

        return new Model(meshes);
    }
}
