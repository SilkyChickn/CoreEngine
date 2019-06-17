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
package de.coreengine.rendering.renderer.ppeffects;

import de.coreengine.rendering.programs.pp.PPShader;

import java.util.List;

/**Represents a template for an effect for the postprocesser
 *
 * @author Darius Dinger
 */
public abstract class PostProcessingEffect {
    
    //Shader of the effect
    protected final PPShader shader;
    
    /**Creating newpost processing effect and setting its
     * shader.
     * 
     * @param shader Shader of the pp effect
     */
    public PostProcessingEffect(PPShader shader) {
        this.shader = shader;
    }
    
    //Setting uniforms of the ppe shader
    protected abstract void setUniforms();
    
    /**Preparing the effect
     * 
     * @param color Color input texture for the effect
     * @param depth Depth input texture for the effect
     */
    public void prepare(int color, int depth){
        shader.start();
        shader.setBaseTexture(color, depth);
        
        setUniforms();
    }
    
    /**Adding all effects that will be implied by this effect to the list.
     * 
     * @param effects List to add implied effects
     */
    public abstract void addImpliedEffects(List<PostProcessingEffect> effects);
    
    /**Exiting the effect
     */
    public void exit(){
        shader.stop();
    }
}
