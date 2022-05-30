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
package de.coreengine.rendering.programs;

import de.coreengine.rendering.model.Color;
import de.coreengine.system.Game;
import de.coreengine.util.Logger;
import de.coreengine.util.gl.MemoryDumper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

/**
 * Class that represents an opengl shader program
 *
 * @author Darius Dinger
 */
public abstract class Shader {

    /**
     * Default location of the glsl shader files
     */
    public static final String SHADERS_LOCATION = "shaders/";

    // Id of the shader program
    private final int program;

    // Name of the shader
    private String shaderName = "NoName";

    /**
     * Creates new Shader and creating shader program in opengl
     */
    public Shader() {
        program = GL20.glCreateProgram();
        MemoryDumper.addProgramm(program);

        initShader();
    }

    /**
     * Initialize shader and uniforms
     */
    private void initShader() {
        addShaders();

        // Rebind vbos attrib locations
        bindAttribs();

        // Link and validate shaderprogram
        GL20.glLinkProgram(program);
        GL20.glValidateProgram(program);

        loadUniforms();
    }

    /**
     * Adding new shader from sourcecode to the program
     * 
     * @param shaderCode String that contains the shader source code
     * @param shaderType Type of the shader (vs, fs, geo, tes, tcs, cs)
     * @param name       Pseudonym name of the shader for faster error detection
     */
    protected final void addShader(String[] shaderCode, int shaderType, String name) {

        // Create shader
        this.shaderName = name;
        int id = GL20.glCreateShader(shaderType);

        // Store shader code into shader and compile the shader
        GL20.glShaderSource(id, shaderCode);
        GL20.glCompileShader(id);

        // Check for compiling errors
        int err = GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS);
        if (err == GL11.GL_FALSE) { // Error occurs during compiling

            // Get shader info log
            String infoLog = GL20.glGetShaderInfoLog(id);

            // Print info log and throw exception
            Logger.err("Shader Compile Error (" + name + ")", infoLog);
            Game.exit(1);
        }

        // Attach Shader to program
        GL20.glAttachShader(program, id);

        // Adding new shader to memory dumper
        MemoryDumper.addShader(id);
    }

    /**
     * Getting location of anshader uniform variable
     * 
     * @param uniform Uniform variable name in shader code
     * @return Uniforms location
     */
    protected final int getUniformLocation(String uniform) {
        start();
        int uniformLocation = GL20.glGetUniformLocation(program, uniform);
        stop();

        if (uniformLocation == -1) {
            Logger.warn("Uniform not found",
                    "The uniform " + uniform + " could" + "not be found in the " + shaderName + " shader!");
        }

        return uniformLocation;
    }

    /**
     * Binding an attribute from a vbo to an 'in' variable in the shader programm
     * 
     * @param attrib Vbos attrib to bind (row)
     * @param name   'in' Variable name in shader code
     */
    protected final void bindAttribute(int attrib, String name) {
        GL20.glBindAttribLocation(program, attrib, name);
    }

    /**
     * Binding uniform variable to opengl texture unit
     * 
     * @param uniform Uniform variable name in shader code
     * @param unit    Opengl texture unit id
     */
    protected final void bindTextureUnit(String uniform, int unit) {
        start();
        int uniformId = GL20.glGetUniformLocation(program, uniform);
        GL20.glUniform1i(uniformId, unit);
        stop();
    }

    /**
     * Adding shaders to program
     */
    protected abstract void addShaders();

    /**
     * Binding all vbo attributes to shader attribute ("in" variable)
     */
    protected abstract void bindAttribs();

    /**
     * Loading uniform locations
     */
    protected abstract void loadUniforms();

    /**
     * Starting/enable shaderprogram
     */
    public void start() {
        GL20.glUseProgram(program);
    }

    /**
     * Stopping/disable shaderprogram (use 0)
     */
    public void stop() {
        GL20.glUseProgram(0);
    }

    /**
     * Loading a boolean into a uniform variable
     * 
     * @param location Location of the uniform variable
     * @param value    Boolean to load
     */
    protected final void setUniform(int location, boolean value) {
        if (value)
            GL20.glUniform1f(location, 1.0f);
        else
            GL20.glUniform1f(location, 0.0f);
    }

    /**
     * Loading a float into a uniform variable
     * 
     * @param location Location of the uniform variable
     * @param value    Float to load
     */
    protected final void setUniform(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    /**
     * Loading an int into a uniform variable
     * 
     * @param location Location of the uniform variable
     * @param value    Int to load
     */
    protected final void setUniform(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    /**
     * Loading a 2d vector into a uniform variable
     * 
     * @param location Location of the uniform variable
     * @param x        First value of the vector
     * @param y        Second value of the vector
     */
    protected final void setUniform(int location, float x, float y) {
        GL20.glUniform2f(location, x, y);
    }

    /**
     * Loading a 3d vector into a uniform variable
     * 
     * @param location Location of the uniform variable
     * @param x        First value of the vector
     * @param y        Second value of the vector
     * @param z        Third value ofthe vector
     */
    protected final void setUniform(int location, float x, float y, float z) {
        GL20.glUniform3f(location, x, y, z);
    }

    /**
     * Loading a 4d vector into a uniform variable
     * 
     * @param location Location of the uniform variable
     * @param x        First value of the vector
     * @param y        Second value of the vector
     * @param z        Third value ofthe vector
     * @param w        Fourth value of the vector
     */
    protected final void setUniform(int location, float x, float y, float z, float w) {
        GL20.glUniform4f(location, x, y, z, w);
    }

    /**
     * Loading a 4x4 matrix into a uniform variable
     * 
     * @param location Location of the uniform variable
     * @param matrix   4x4 matrix to load as float array
     */
    protected final void setUniform(int location, float[] matrix) {
        GL20.glUniformMatrix4fv(location, false, matrix);
    }

    /**
     * Loading a vec3 array into a uniform variable
     * 
     * @param location Location of the uniform variable
     * @param floats   float array to use (x0,y0,z0,x1,y1,z1,x2,...)
     */
    protected final void setUniformArray3f(int location, float[] floats) {
        GL20.glUniform3fv(location, floats);
    }

    /**
     * Loading a vec2 array into a uniform variable
     * 
     * @param location Location of the uniform variable
     * @param floats   float array to use (x0,y0,x1,y1,x2,...)
     */
    protected final void setUniformArray2f(int location, float[] floats) {
        GL20.glUniform2fv(location, floats);
    }

    /**
     * Loading a float array into a uniform variable
     * 
     * @param location Location of the uniform variable
     * @param floats   float array to use (x0,x1,x2,...)
     */
    protected final void setUniformArray1f(int location, float[] floats) {
        GL20.glUniform1fv(location, floats);
    }

    /**
     * Loading a int array into a uniform variable
     * 
     * @param location Location of the uniform variable
     * @param ints     int array to use (x0,x1,x2,...)
     */
    protected final void setUniformArray1i(int location, int[] ints) {
        GL20.glUniform1iv(location, ints);
    }

    /**
     * Loading a color into a uniform vec3f variable
     * 
     * @param location Location of the uniform variable
     * @param col      color to load as vec3f
     */
    protected final void setUniform(int location, Color col) {
        GL20.glUniform3f(location, col.getRed(), col.getGreen(), col.getBlue());
    }

    /**
     * Binding texture to an opengl textureunit
     * 
     * @param texture TextureData to bind
     * @param unit    Textureunit to bind texture to
     * @param type    TextureData type (GL_TEXTURE_2D, GL_TEXTURE_CUBE_MAP, ...)
     */
    protected final void bindTexture(int texture, int unit, int type) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
        GL11.glBindTexture(type, texture);
    }
}
