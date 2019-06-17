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
package de.coreengine.rendering.renderer;

import de.coreengine.framework.Window;
import de.coreengine.rendering.FrameBufferObject;
import de.coreengine.rendering.model.SimpleModel;
import de.coreengine.rendering.model.singletons.Quad2D;
import de.coreengine.rendering.renderer.ppeffects.PostProcessingEffect;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.LinkedList;
import java.util.List;

/**Can apply post processing effects to the scene
 *
 * @author Darius Dinger
 */
public class PostProcesser {
    
    //List of all active effect for the next frame
    private static List<PostProcessingEffect> effects = new LinkedList<>();
    
    //Input and output fbo
    private static FrameBufferObject input;
    private static FrameBufferObject output;
    
    public static void init(){
        recreateFbos();
        Window.addWindowListener((x, y, aspect) -> recreateFbos());
    }
    
    /**(Re)creating the pp fbos
     */
    private static void recreateFbos(){
        input = 
            new FrameBufferObject(Window.getWidth(), Window.getHeight(), false);
        output = 
            new FrameBufferObject(Window.getWidth(), Window.getHeight(), false);
    }
    
    /**Applying all effects to the color and depth attatchment of the input fbo
     * and render into the output fbo
     */
    static void render(){
        
        //If theres no effects, just blitting input to output
        if(effects.isEmpty()){
            input.blitToFbo(output, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            return;
        }
        
        SimpleModel model = Quad2D.getInstance();
        
        model.getVao().bind();
        model.getVao().enableAttributes();
        model.getIndexBuffer().bind();
        
        for(PostProcessingEffect effect: effects){
            
            effect.prepare(input.getColorAttachment0(), input.getDepthAttachment());
            
            output.bind(GL30.GL_COLOR_ATTACHMENT0);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            
            GL11.glDrawElements(GL11.GL_TRIANGLES, 
                    model.getIndexBuffer().getSize(), GL11.GL_UNSIGNED_INT, 0);
            
            output.unbind();
            output.blitToFbo(input, GL11.GL_COLOR_BUFFER_BIT);
            
            effect.exit();
        }
        
        model.getIndexBuffer().unbind();
        model.getVao().disableAttributes();
        model.getVao().unbind();
        
        input.blitToFbo(output, GL11.GL_DEPTH_BUFFER_BIT);
        
        effects.clear();
    }
    
    /**Adding a new effect to the post processing pipeline
     * 
     * @param effect Effect to add
     */
    public static void addEffect(PostProcessingEffect effect){
        effects.add(effect);
        effect.addImpliedEffects(effects);
    }
    
    /** @return Input FBO of the post processer
     */
    static FrameBufferObject getInput() {
        return input;
    }
    
    /** @return Output FBO of the post processer
     */
    static FrameBufferObject getOutput() {
        return output;
    }
}
