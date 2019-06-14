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
package de.coreengine.rendering;

import de.coreengine.framework.Window;
import de.coreengine.system.Game;
import de.coreengine.util.Configuration;
import de.coreengine.util.Logger;
import de.coreengine.util.Toolbox;
import de.coreengine.util.gl.MemoryDumper;
import java.awt.Dimension;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**Class that represents a frame buffer object in opengl
 *
 * @author Darius Dinger
 */
public class FrameBufferObject {
    
    //Id of the frame buffer object in opengl context
    private final int id;
    
    //The dimension (width/height) of the fbo in pixels
    private final Dimension dimension;
    
    //Id of the depth buffer, where this fbo is rendering into
    private final int depthBuffer;
    
    //Id of the color buffer 0, where this fbo is rendering into
    private final int firstColorBuffer;
    
    //Array of all attached color buffers
    private int[] colorBuffers = new int[0];
    
    //Is fbo multisampled?
    private final boolean multisampled;
    
    /**Creates new framebuffer in opengl and attaching first color buffer 
     * (color buffer 0) and depth buffer
     * 
     * @param width Horizontal resolution of the fbo in pixels
     * @param height Vertical resolution of the fbo in pixels
     * @param multisampled Should fbo use multisampling
     */
    public FrameBufferObject(int width, int height, boolean multisampled) {
        this.multisampled = multisampled;
        this.dimension = new Dimension(width, height);
        
        //Generate and bind fbo
        id = GL30.glGenFramebuffers();
        MemoryDumper.addFramebuffer(id);
        
        //Attach default color and depth texture
        firstColorBuffer = addColorBuffer(0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE);
        depthBuffer = addDepthBuffer();
        
        //Check frameBuffer for errors, throw CoreEngineException, when fails
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE){
            Logger.err("Framebuffer creation", "Error by creating framebuffer!");
            Game.exit(1);
        }
        
        //Unbind framebuffer
        unbind();
    }
    
    /**Adding the depth attachment to this fbo. If fbo is multisampled, returning
     * the attached renderbuffer, else returning the attached texture
     * 
     * @return id of the depth buffer/texture
     */
    private int addDepthBuffer(){
        
        //Bind fbo and generate depth buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        int tex;
        
        if(multisampled){
            
            //Create multisampled depth buffer
            tex = GL30.glGenRenderbuffers();
            MemoryDumper.addRenderbuffer(tex);
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, tex);
            GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 
                    Configuration.getValuei("MSAA_LEVEL"), 
                    GL14.GL_DEPTH_COMPONENT24, dimension.width, dimension.height);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, 
                    GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, tex);
        }else{
            
            //Create non multisampled depth buffer
            tex = GL11.glGenTextures();
            MemoryDumper.addTexture(tex);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, 
                    dimension.width, dimension.height, 0, GL11.GL_DEPTH_COMPONENT, 
                    GL11.GL_FLOAT, 0);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, 
                    GL11.GL_TEXTURE_2D, tex, 0);
        }
        
        unbind();
        return tex;
    }
    
    /**Adding new color attachment to this fbo. If fbo is multisampled, returning
     * the attached renderbuffer, else returning the attached texture
     * 
     * @param att Color attachment to add
     * @param internalFormat Internal format of the pixels (GL_RGB, GL_RGB8, GL_RGB16F, ..)
     * @param type Type of the texture data (GL_UNSIGNED_BYTE, GL_FLOAT, ...) (Only when not multisampled)
     * @return Id of the color buffer/texture
     */
    protected final int addColorBuffer(int att, int internalFormat, int type){
        
        //Bind fbo and generate color buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        int attachment = GL30.GL_COLOR_ATTACHMENT0 +att;
        int tex;
        
        if(multisampled){
            
            //Create multisampled color buffer
            tex = GL30.glGenRenderbuffers();
            MemoryDumper.addRenderbuffer(tex);
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, tex);
            GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 
                    Configuration.getValuei("MSAA_LEVEL"), 
                    internalFormat, dimension.width, dimension.height);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, 
                    attachment, GL30.GL_RENDERBUFFER, tex);
        }else{
            
            //Create non multisampled color buffer
            tex = GL11.glGenTextures();
            MemoryDumper.addTexture(tex);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, 
                    dimension.width, dimension.height, 0, GL11.GL_RGB, type, 0);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, 
                    GL11.GL_TEXTURE_2D, tex, 0);
        }
        
        //Unbind fbo and add new color buffer to color buffers
        unbind();
        colorBuffers = Toolbox.addElement(colorBuffers, attachment);
        
        return tex;
    }
    
    /**Blitting/writing color and depth information from this fbo into another fbo
     * 
     * @param out Output fbo to blit in
     * @param buffers Wich buffers to blit (GL_DEPTH_BUFFER_BIT, GL_COLOR_BUFFER_BIT)
     */
    public final void blitToFbo(FrameBufferObject out, int buffers){
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, out.id);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GL30.glBlitFramebuffer(0, 0, dimension.width, dimension.height, 0, 0, 
                Window.getWidth(), Window.getHeight(), buffers, GL11.GL_NEAREST);
        unbind();
    }
    
    /**Blitting this fbo onto the glfw window
     */
    public final void blitToScreen(){
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
        GL30.glBlitFramebuffer(0, 0, dimension.width, dimension.height, 
                0, 0, Window.getWidth(), Window.getHeight(), 
                GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
        unbind();
    }
    
    /**Binding framebuffer as draw framebuffer and bind all color buffers.
     * Sets glViewport to fbo dimension
     * 
     * @param readAttachment Color attachment to bind as read buffer
     */
    public final void bind(int readAttachment){
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        GL20.glDrawBuffers(colorBuffers);
        GL11.glReadBuffer(readAttachment);
        GL11.glViewport(0, 0, dimension.width, dimension.height);
    }
    
    /**Unbing framebuffer (bind 0) and reset glViewport to window dimension
     */
    public final void unbind(){
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, Window.getWidth(), Window.getHeight());
    }
    
    /**Returning the id of the depth texture, if multisampling is disabled. 
     * If multisampling is enabled, returning the id of the depth buffer
     * 
     * @return Id of the depth texture/buffer
     */
    public final int getDepthAttachment() {
        return depthBuffer;
    }
    
    /**Returning the id of the color attachment texture, if multisampling is disabled. 
     * If multisampling is enabled, returning the id of the color attachment buffer
     * 
     * @return Id of the color attachment 0 texture/renderbuffer
     */
    public final int getColorAttachment0() {
        return firstColorBuffer;
    }
}
