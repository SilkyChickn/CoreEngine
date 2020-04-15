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
package de.coreengine.rendering.renderable.terrain;

import de.coreengine.asset.FileLoader;
import de.coreengine.rendering.model.singletons.TerrainMesh;
import de.coreengine.util.Toolbox;
import de.coreengine.util.gl.IndexBuffer;
import de.coreengine.util.gl.VertexArrayObject;

/**
 * Class that loading the terrain mesh and its morphing levels
 *
 * @author Darius Dinger
 */
public class TerrainMeshLoader {
    private static final String MESH_FILE = "res/DefaultTerrainMesh";

    /**
     * Parsing a terrain mesh from a file into a terrain mesh object with all
     * morphing variants
     * 
     * @return Generated terrain mesh
     */
    public static TerrainMesh loadTerrainMesh() {

        // Loading file into String array
        String[] data = FileLoader.getResource(MESH_FILE, false);

        // Generate empty mesh
        VertexArrayObject meshData = new VertexArrayObject();

        // Prepare data arrays
        float[] verticesArr = new float[0];
        int[] fullArr = new int[0];
        int[] bottomArr = new int[0];
        int[] topArr = new int[0];
        int[] leftArr = new int[0];
        int[] rightArr = new int[0];
        int[] bottomleftArr = new int[0];
        int[] bottomrightArr = new int[0];
        int[] topleftArr = new int[0];
        int[] toprightArr = new int[0];

        // Parsing data lines to data arrays
        for (String line : data) {
            if (line.startsWith("v "))
                verticesArr = addLineToArray(verticesArr, line);
            else if (line.startsWith("f "))
                fullArr = addLineToArray(fullArr, line);
            else if (line.startsWith("b "))
                bottomArr = addLineToArray(bottomArr, line);
            else if (line.startsWith("t "))
                topArr = addLineToArray(topArr, line);
            else if (line.startsWith("l "))
                leftArr = addLineToArray(leftArr, line);
            else if (line.startsWith("r "))
                rightArr = addLineToArray(rightArr, line);
            else if (line.startsWith("bl "))
                bottomleftArr = addLineToArray(bottomleftArr, line);
            else if (line.startsWith("br "))
                bottomrightArr = addLineToArray(bottomrightArr, line);
            else if (line.startsWith("tl "))
                topleftArr = addLineToArray(topleftArr, line);
            else if (line.startsWith("tr "))
                toprightArr = addLineToArray(toprightArr, line);
        }

        // Adding parsed data into mesh data vao
        meshData.addVertexBuffer(verticesArr, 2, 0);
        IndexBuffer full = meshData.addIndexBuffer(fullArr);
        IndexBuffer bottom = meshData.addIndexBuffer(bottomArr);
        IndexBuffer top = meshData.addIndexBuffer(topArr);
        IndexBuffer left = meshData.addIndexBuffer(leftArr);
        IndexBuffer right = meshData.addIndexBuffer(rightArr);
        IndexBuffer bottomleft = meshData.addIndexBuffer(bottomleftArr);
        IndexBuffer bottomright = meshData.addIndexBuffer(bottomrightArr);
        IndexBuffer topleft = meshData.addIndexBuffer(topleftArr);
        IndexBuffer topright = meshData.addIndexBuffer(toprightArr);

        // return new meshdata object with its vao and index buffers
        return new TerrainMesh(meshData, full, top, bottom, right, left, topleft, topright, bottomleft, bottomright);
    }

    /**
     * Adding a line of floats, seperated with spaces into a float array<br>
     * chars before the first space will be ignored!
     * 
     * @param openArr Array to add floats
     * @param line    Line to split and add
     * @return Array with added floats
     */
    private static float[] addLineToArray(float[] openArr, String line) {
        String[] data = line.split(" ");
        for (int i = 1; i < data.length; i++) {
            openArr = Toolbox.addElement(openArr, Float.parseFloat(data[i]));
        }
        return openArr;
    }

    /**
     * Adding a line of integers, seperated with spaces into a integer array<br>
     * chars before the first space will be ignored!
     * 
     * @param openArr Array to add integers
     * @param line    Line to split and add
     * @return Array with added integers
     */
    private static int[] addLineToArray(int[] openArr, String line) {
        String[] data = line.split(" ");
        for (int i = 1; i < data.length; i++) {
            openArr = Toolbox.addElement(openArr, Integer.parseInt(data[i]));
        }
        return openArr;
    }
}
