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
package de.coreengine.util.gl;

import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.*;

/**
 * Class that represent an opengl vao
 *
 * @author Darius Dinger
 */
public class VertexArrayObject {

    // Vao id
    private int id;

    // Rows/attributes of the vao
    private int[] attribs = new int[0];

    /**
     * Creates new VertexArrayObject and generate one in opengl
     */
    public VertexArrayObject() {
        id = GL30.glGenVertexArrays();
        MemoryDumper.addVao(id);
    }

    /**
     * Adding new VertexBufferObject (VBO) to the VAO
     * 
     * @param values    Values to fill into the buffer
     * @param dimension Dimension of the values
     * @param row       Row to store the buffer in the vao
     */
    public void addVertexBuffer(float[] values, int dimension, int row) {

        // Bind VAO
        bind();

        // Generate vertex buffer
        int vbo = GL15.glGenBuffers();

        // Fill and seperate data in buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, values, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(row, dimension, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Unbind VAO
        unbind();

        // Add new buffer to buffers and row to attributes
        MemoryDumper.addVbo(vbo);
        attribs = Toolbox.addElement(attribs, row);
    }

    /**
     * Adding new VertexBufferObject (VBO) to the VAO
     *
     * @param values    Values to fill into the buffer
     * @param dimension Dimension of the values
     * @param row       Row to store the buffer in the vao
     */
    public void addVertexBuffer(int[] values, int dimension, int row) {

        // Bind VAO
        bind();

        // Generate vertex buffer
        int vbo = GL15.glGenBuffers();

        // Fill and seperate data in buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, values, GL15.GL_STATIC_DRAW);
        GL30.glVertexAttribIPointer(row, dimension, GL11.GL_INT, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Unbind VAO
        unbind();

        // Add new buffer to buffers and row to attributes
        MemoryDumper.addVbo(vbo);
        attribs = Toolbox.addElement(attribs, row);
    }

    /**
     * Creates new IndexBuffer and adding it to the vao
     * 
     * @param indices Indices for the new index buffer
     * @return IndexBuffer that was created
     */
    public IndexBuffer addIndexBuffer(int[] indices) {

        // Bind VAO
        bind();

        // Generate index buffer
        int vbo = GL15.glGenBuffers();

        // Fill data into buffer
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        // Unbind VAO
        unbind();

        // Add new buffer to memory dumper to delete after exit
        MemoryDumper.addVbo(vbo);

        // Create and return new index buffer
        return new IndexBuffer(vbo, indices.length);
    }

    /**
     * Adding a vertex buffer, that change sper instance.
     * 
     * @param maxInstances Max instances of the vao per render call
     * @param dimension    Dimension of the instanced data
     * @param firstRow     First row of the instanced data
     * @param rowCount     Count of rows of the instanced data
     * @return VBO id of the instanced vertex buffer
     */
    public int addInstancedVertexBuffer(int maxInstances, int dimension, int firstRow, int rowCount) {

        // Bind VAO
        bind();

        // Generate vertex buffer
        int vbo = GL15.glGenBuffers();

        // Bind vertex buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        // Size of one row in the vertex buffer
        int rowSizeBytes = dimension * 4;

        // Setup data storage
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, rowSizeBytes * rowCount * maxInstances, GL15.GL_STREAM_DRAW);

        // Add Pointer(s) for instanced vertex buffer and setdivisors to 1 (per
        // instance)
        for (int i = firstRow; i < firstRow + rowCount; i++) {
            int offset = i - firstRow;
            GL20.glVertexAttribPointer(i, dimension, GL11.GL_FLOAT, false, rowSizeBytes * rowCount,
                    offset * rowSizeBytes);
            GL33.glVertexAttribDivisor(i, 1);
        }

        // Unbind vertex buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Unbind VAO
        unbind();

        // Add new buffer to memory dumper and rows to attributes
        MemoryDumper.addVbo(vbo);
        for (int i = firstRow; i < firstRow + rowCount; i++) {
            attribs = Toolbox.addElement(attribs, i);
        }

        return vbo;
    }

    /**
     * Enable all rows of the VAO
     */
    public void enableAttributes() {
        for (int row : attribs) {
            GL20.glEnableVertexAttribArray(row);
        }
    }

    /**
     * Enable specific row of the VAO
     * 
     * @param row Row to enable
     */
    public void enableAttribute(int row) {
        GL20.glEnableVertexAttribArray(row);
    }

    /**
     * Disable all rows of the VAO
     */
    public void disableAttributes() {
        for (int row : attribs) {
            GL20.glDisableVertexAttribArray(row);
        }
    }

    /**
     * Disable specific row of the VAO
     * 
     * @param row Row to disable
     */
    public void disableAttribute(int row) {
        GL20.glDisableVertexAttribArray(row);
    }

    /**
     * Bind VAO to opengl
     */
    public void bind() {
        GL30.glBindVertexArray(id);
    }

    /**
     * Unbind VAO from opengl (bind 0)
     */
    public final void unbind() {
        GL30.glBindVertexArray(0);
    }
}
