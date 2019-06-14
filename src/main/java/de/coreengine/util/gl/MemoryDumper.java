/*
 * Copyright (c) 2019, Darius Dinger
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.coreengine.util.gl;

import de.coreengine.util.Toolbox;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**Class that clean up memory after game quits
 *
 * @author Darius Dinger
 */
public class MemoryDumper {
    
    //Arrays of all 'to dump' opengl ids
    private static int[] VBOS = {};
    private static int[] VAOS = {};
    private static int[] SHADERS = {};
    private static int[] PROGRAMS = {};
    private static int[] RENDERBUFFERS = {};
    private static int[] FRAMEBUFFERS = {};
    private static int[] TEXTURES = {};
    private static int[] SOUND_BUFFERS = {};
    private static int[] SOUND_SOURCES = {};
    
    /**Dumping all memory saved opengl stuff.
     * ATTENTION: Only dump, if program is over (at exit)
     */
    public static void dumpMemory(){
        
        //Dump vertex array objects and buffers
        GL15.glDeleteBuffers(VBOS);
        GL30.glDeleteVertexArrays(VAOS);
        
        //Dump textures
        GL11.glDeleteTextures(TEXTURES);
        
        //Dump shaders and shader programs
        for(int shader: SHADERS) GL20.glDeleteShader(shader);
        for(int program: PROGRAMS) GL20.glDeleteProgram(program);
        
        //Dump renderbuffers and fbos
        GL30.glDeleteRenderbuffers(RENDERBUFFERS);
        GL30.glDeleteFramebuffers(FRAMEBUFFERS);
        
        //Dump audios and audio sources
        AL10.alDeleteBuffers(SOUND_BUFFERS);
        AL10.alDeleteSources(SOUND_SOURCES);
    }
    
    /**Adding vao to dump after program exits
     * 
     * @param vaoId Vao to dump at exit
     */
    public static void addVao(int vaoId){
        VAOS = Toolbox.addElement(VAOS, vaoId);
    }
    
    /**Adding vbo to dump after program exits
     * 
     * @param vboId Vbo to dump at exit
     */
    public static void addVbo(int vboId){
        VBOS = Toolbox.addElement(VBOS, vboId);
    }
    
    /**Adding shader program to dump after program exits
     * 
     * @param programId Program to dump at exit
     */
    public static void addProgramm(int programId){
        PROGRAMS = Toolbox.addElement(PROGRAMS, programId);
    }
    
    /**Adding shader to dump after program exits
     * 
     * @param shaderId Shader to dump at exit
     */
    public static void addShader(int shaderId){
        SHADERS = Toolbox.addElement(SHADERS, shaderId);
    }
    
    /**Adding framebuffer to dump after program exits
     * 
     * @param framebufferId Framebuffer to dump at exit
     */
    public static void addFramebuffer(int framebufferId){
        FRAMEBUFFERS = Toolbox.addElement(FRAMEBUFFERS, framebufferId);
    }
    
    /**Adding renderbuffer to dump after program exits
     * 
     * @param renderbufferId Renderbuffer to dump at exit
     */
    public static void addRenderbuffer(int renderbufferId){
        RENDERBUFFERS = Toolbox.addElement(RENDERBUFFERS, renderbufferId);
    }
    
    /**Adding texture to dump after program exits
     * 
     * @param textureId Texture id to dump at exit
     */
    public static void addTexture(int textureId){
        TEXTURES = Toolbox.addElement(TEXTURES, textureId);
    }
    
    /**Adding audio buffer to dump after programm exits
     * 
     * @param buffer Audio buffer id to dump at exit
     */
    public static void addAudioBuffer(int buffer){
        SOUND_BUFFERS = Toolbox.addElement(SOUND_BUFFERS, buffer);
    }
    
    /**Adding audio source to dump after programm exits
     * 
     * @param source Audio source id to dump at exit
     */
    public static void addAudioSource(int source){
        SOUND_SOURCES = Toolbox.addElement(SOUND_SOURCES, source);
    }
}
