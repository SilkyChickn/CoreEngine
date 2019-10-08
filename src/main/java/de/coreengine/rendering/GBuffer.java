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
package de.coreengine.rendering;

import de.coreengine.framework.Window;
import de.coreengine.util.Configuration;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**Class that represent a gBuffer fbo for deferred rendering
 *
 * @author Darius Dinger
 */
public class GBuffer extends FrameBufferObject{
    
    //TextureData ids of the gBuffer buffers
    private final int colorBuffer, positionBuffer, normalBuffer, pickingBuffer, 
            glowingBuffer, sunBuffer, variable0Buffer, variable1Buffer;
    
    /**Creates new gBuffer with the size of the glfw window
     */
    public GBuffer() {
        super(Window.getWidth(), Window.getHeight(), false);
        
        //Get buffer ids
        //int cbId = Configuration.getValuei("GBUFFER_COLOR_BUFFER"); //Useless but somehow defensible...
        int pbId = Configuration.getValuei("GBUFFER_POSITION_BUFFER");
        int nbId = Configuration.getValuei("GBUFFER_NORMAL_BUFFER");
        int v0Id = Configuration.getValuei("GBUFFER_VAR0_BUFFER");
        int v1Id = Configuration.getValuei("GBUFFER_VAR1_BUFFER");
        int piId = Configuration.getValuei("GBUFFER_PICKING_BUFFER");
        int gbId = Configuration.getValuei("GBUFFER_GLOWING_BUFFER");
        int suId = Configuration.getValuei("GBUFFER_SUN_BUFFER");
        
        //Create color attachments and save texture ids
        colorBuffer         = getColorAttachment0();
        positionBuffer      = addColorBuffer(pbId, GL30.GL_RGB16F, GL11.GL_FLOAT);
        normalBuffer        = addColorBuffer(nbId, GL30.GL_RGB16F, GL11.GL_FLOAT);
        variable0Buffer     = addColorBuffer(v0Id, GL30.GL_RGB16F, GL11.GL_FLOAT);
        variable1Buffer     = addColorBuffer(v1Id, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE);
        pickingBuffer       = addColorBuffer(piId, GL30.GL_RGB32F, GL11.GL_UNSIGNED_BYTE);
        glowingBuffer       = addColorBuffer(gbId, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE);
        sunBuffer           = addColorBuffer(suId, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE);
    }
    
    /**@return GBuffers color buffer texture id
     */
    public int getColorBuffer() {
        return colorBuffer;
    }
    
    /**@return GBuffers position buffer texture id
     */
    public int getPositionBuffer() {
        return positionBuffer;
    }
    
    /**@return GBuffers normal buffer texture id
     */
    public int getNormalBuffer() {
        return normalBuffer;
    }
    
    /**Getting variable0 gbuffer attachment texture.<br>
     * Red component    = How much effected by lighting<br>
     * Green component  = How much effected by fog<br>
     * Blue component   = Ambient occlusion<br>
     * 
     * @return GBuffers variable0 buffer texture id
     */
    public int getVariable0Buffer() {
        return variable0Buffer;
    }
    
    /**Getting variable1 gbuffer attachment texture.<br>
     * Red component    = How much reflective<br>
     * Green component  = How much damping shine<br>
     * Blue component   = use fake lighting in diffuse lighting (normal=(0, 1, 0))<br>
     * 
     * @return GBuffers variable1 buffer texture id
     */
    public int getVariable1Buffer() {
        return variable1Buffer;
    }
    
    /**@return GBuffers picking buffer texture id
     */
    public int getPickingBuffer() {
        return pickingBuffer;
    }
    
    /**@return GBuffers glowing buffer texture id
     */
    public int getGlowingBuffer() {
        return glowingBuffer;
    }
    
    /**@return GBuffers sun buffer texture id
     */
    public int getSunBuffer() {
        return sunBuffer;
    }
}
