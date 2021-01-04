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

package de.coreengine.asset;

import de.coreengine.animation.Animation;
import de.coreengine.animation.Joint;
import de.coreengine.asset.dataStructures.AnimatedModelData;
import de.coreengine.asset.dataStructures.MaterialData;
import de.coreengine.asset.dataStructures.MeshData;
import de.coreengine.asset.dataStructures.ModelData;
import de.coreengine.asset.modelLoader.*;
import de.coreengine.util.Logger;
import org.lwjgl.assimp.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

/**
 * Class for loading model files<br>
 * <br>
 * Supported Formats (From Assimp):<br>
 * <br>
 * COMMON INTERCHANGE FORMATS (An asterisk indicates limited support)<br>
 * Autodesk ( .fbx )<br>
 * Collada ( .dae )<br>
 * glTF ( .gltf, .glb )<br>
 * Blender 3D ( .blend )<br>
 * 3ds Max 3DS ( .3ds )<br>
 * 3ds Max ASE ( .ase )<br>
 * Wavefront Object ( .obj )<br>
 * Industry Foundation Classes (IFC/Step) ( .ifc )<br>
 * XGL ( .xgl,.zgl )<br>
 * Stanford Polygon Library ( .ply )<br>
 * *AutoCAD DXF ( .dxf )<br>
 * LightWave ( .lwo )<br>
 * LightWave Scene ( .lws )<br>
 * Modo ( .lxo )<br>
 * Stereolithography ( .stl )<br>
 * DirectX X ( .x )<br>
 * AC3D ( .ac )<br>
 * Milkshape 3D ( .ms3d )<br>
 * * TrueSpace ( .cob,.scn )<br>
 * *OpenGEX ( .ogex )<br>
 * *X3D ( .x3d )<br>
 * *3MF ( .3mf )<br>
 * <br>
 * MOTION CAPTURE FORMATS<br>
 * Biovision BVH ( .bvh )<br>
 * * CharacterStudio Motion ( .csm )<br>
 * <br>
 * GRAPHICS ENGINE FORMATS<br>
 * Ogre XML ( .xml )<br>
 * Irrlicht Mesh ( .irrmesh )<br>
 * * Irrlicht Scene ( .irr )<br>
 * <br>
 * GAME FILE FORMATS<br>
 * Quake I ( .mdl )<br>
 * Quake II ( .md2 )<br>
 * Quake III Mesh ( .md3 )<br>
 * Quake III Map/BSP ( .pk3 )<br>
 * * Return to Castle Wolfenstein ( .mdc )<br>
 * Doom 3 ( .md5* )<br>
 * *Valve Model ( .smd,.vta )<br>
 * *Open Game Engine Exchange ( .ogex )<br>
 * *Unreal ( .3d )<br>
 * <br>
 * OTHER FILE FORMATS<br>
 * BlitzBasic 3D ( .b3d )<br>
 * Quick3D ( .q3d,.q3s )<br>
 * Neutral File Format ( .nff )<br>
 * Sense8 WorldToolKit ( .nff )<br>
 * Object File Format ( .off )<br>
 * PovRAY Raw ( .raw )<br>
 * Terragen Terrain ( .ter )<br>
 * 3D GameStudio (3DGS) ( .mdl )<br>
 * 3D GameStudio (3DGS) Terrain ( .hmp )<br>
 * Izware Nendo ( .ndo )<br>
 * <br>
 * 
 * @author Darius
 */
public class ModelLoader {

    /**
     * Loading a model from a file into asset database
     *
     * @param file       Model file to load
     * @param texPath    Location of the texture files
     * @param shape      Collision shape, or "convex" / "triangleMesh" / null to
     *                   auto generate
     * @param asResource Load textures from resources
     */
    public static void loadModelFile(String file, String texPath, boolean asResource, String shape) {
        if (AssetDatabase.getModel(file) != null)
            return;
        ModelData modelData = loadModelFileData(file, shape);
        if (modelData != null)
            AssetDatabase.addModel(file, modelData.getInstance(texPath, asResource));
    }

    /**
     * Loading an animated model from a file into asset database
     *
     * @param file       Model file to load
     * @param texPath    Location of the texture files
     * @param shape      Collision shape, or "convex" / "triangleMesh" / null to
     *                   auto generate
     * @param asResource Load textures from resources
     */
    public static void loadAnimatedModelFile(String file, String texPath, boolean asResource, String shape) {
        if (AssetDatabase.getAnimatedModel(file) != null)
            return;
        AnimatedModelData animatedModelData = loadAnimatedModelFileData(file, shape);
        if (animatedModelData != null)
            AssetDatabase.addAnimatedModel(file, animatedModelData.getInstance(texPath, asResource));
    }

    /**
     * Loading a dataStructures model from a file
     *
     * @param file  Model file to load
     * @param shape Collision shape, or "convex" / "triangleMesh" / null to auto
     *              generate
     * @return Meta model with raw model data
     */
    public static ModelData loadModelFileData(String file, String shape) {

        // Load scene
        AIScene aiScene = getScene(file);
        if (aiScene == null)
            return null;

        // Static data
        MaterialData[] materials = getMaterials(aiScene);
        MeshData[] meshes = getMeshs(aiScene, materials, shape, null);

        // Create and store model data
        ModelData modelData = new ModelData();
        modelData.meshes = meshes;

        return modelData;
    }

    /**
     * Loading an dataStructures animated model from a file
     *
     * @param file  Model file to load
     * @param shape Collision shape, or "convex" / "triangleMesh" / null to auto
     *              generate
     * 
     * @return Loaded Model as AnimatedModelData
     */
    public static AnimatedModelData loadAnimatedModelFileData(String file, String shape) {

        // Load scene
        AIScene aiScene = getScene(file);
        if (aiScene == null)
            return null;

        // Static data
        List<BoneParser> boneData = new ArrayList<>();
        MaterialData[] materials = getMaterials(aiScene);
        MeshData[] meshs = getMeshs(aiScene, materials, shape, boneData);

        // Animation data
        HashMap<String, Animation> animations = getAnimations(aiScene, boneData);
        NodeParser nodeParser = new NodeParser(aiScene.mRootNode());
        nodeParser.parse(boneData);
        Joint skeleton = nodeParser.getSkeleton();

        // Create and store model data
        AnimatedModelData animatedModelData = new AnimatedModelData();
        animatedModelData.meshes = meshs;
        animatedModelData.skeleton = skeleton;
        animatedModelData.animations = animations;

        return animatedModelData;
    }

    /**
     * Loading an aiScene from a file
     *
     * @param file Model file to load
     * @return Loaded AIScene
     */
    private static AIScene getScene(String file) {

        // Importer flags
        int flags = aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_JoinIdenticalVertices
                | aiProcess_CalcTangentSpace | aiProcess_GenNormals | aiProcess_LimitBoneWeights
                | aiProcess_ValidateDataStructure | aiProcess_RemoveRedundantMaterials | aiProcess_GenUVCoords
                | aiProcess_OptimizeMeshes;

        // Import model with props
        AIPropertyStore props = aiCreatePropertyStore();
        aiSetImportPropertyInteger(props, AI_CONFIG_IMPORT_REMOVE_EMPTY_BONES, 0);
        AIScene aiScene = aiImportFileExWithProperties(file, flags, null, props);
        aiReleasePropertyStore(props);

        // Error at import
        if (aiScene == null) {
            Logger.warn("Error by loading model",
                    "The model file " + file + " could not be loaded! " + "Returning null!");
            return null;
        }

        return aiScene;
    }

    /**
     * Loading animations from an aiScene
     *
     * @param aiScene AIScene to load animations from
     * @param bones   Bones of the model
     * @return Map of animations with its name
     */
    private static HashMap<String, Animation> getAnimations(AIScene aiScene, List<BoneParser> bones) {
        int animationCount = aiScene.mNumAnimations();

        // Create animation data structure
        HashMap<String, Animation> animations = new HashMap<>();

        for (int i = 0; i < animationCount; i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiScene.mAnimations().get(i));
            AnimationParser animationParser = new AnimationParser(aiAnimation);
            animationParser.parse(bones);
            animations.put(animationParser.getName(), animationParser.getAnimation());
        }

        return animations;
    }

    /**
     * Loading dataStructures meshes from an aiScene
     *
     * @param aiScene   AIScene to load meshes from
     * @param materials Materials of the AIScene
     * @param shape     Collision shape to use
     * @param bones     List to add bones, or null to dont load bones
     * @return Array of the scene meshes
     */
    private static MeshData[] getMeshs(AIScene aiScene, MaterialData[] materials, String shape,
            List<BoneParser> bones) {
        int meshCount = aiScene.mNumMeshes();

        // Create mesh data structure
        MeshData[] meshes = new MeshData[meshCount];

        // Iterate through meshes and parse them
        for (int i = 0; i < meshCount; i++) {
            AIMesh aiMesh = AIMesh.create(aiScene.mMeshes().get(i));
            MeshParser meshParser = new MeshParser(aiMesh, materials);
            meshParser.parse(shape, bones);
            meshes[i] = meshParser.getMeshData();
        }

        return meshes;
    }

    /**
     * Loading dataStructures materials from an aiScene
     *
     * @param aiScene AIScene to load materials from
     * @return Array of the scene materials
     */
    private static MaterialData[] getMaterials(AIScene aiScene) {
        int matCount = aiScene.mNumMaterials();

        // Create material data structure
        MaterialData[] materials = new MaterialData[matCount];

        // Iterate through materials and parse them
        for (int i = 0; i < matCount; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            MaterialParser materialParser = new MaterialParser(aiMaterial);
            materialParser.parse();
            materials[i] = materialParser.getMaterialData();
        }

        return materials;
    }
}
