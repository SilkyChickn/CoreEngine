package demo;

import io.github.suuirad.coreengine.asset.ImageLoader;
import io.github.suuirad.coreengine.framework.Keyboard;
import io.github.suuirad.coreengine.framework.Mouse;
import io.github.suuirad.coreengine.framework.Window;
import io.github.suuirad.coreengine.rendering.programs.Shader;
import io.github.suuirad.coreengine.rendering.renderable.Camera;
import io.github.suuirad.coreengine.system.Game;
import io.github.suuirad.coreengine.system.gameObjects.FPCamera;
import io.github.suuirad.coreengine.util.FrameTimer;
import io.github.suuirad.coreengine.util.Toolbox;
import io.github.suuirad.coreengine.util.gl.IndexBuffer;
import io.github.suuirad.coreengine.util.gl.VertexArrayObject;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

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

/**
 *
 * @author Darius Dinger
 */
public class TestPOM {
    
    public static void main(String[] args){
        Game.init(1024, 720, "Test POM", false);
        
        VertexArrayObject vao = new VertexArrayObject();
        vao.addVertexBuffer(new float[]{
            0, 0, 1, 0, 0, 1, 1, 1
        }, 2, 0);
        IndexBuffer index = vao.addIndexBuffer(new int[]{
            0, 1, 2, 2, 1, 3
        });
        
        int diff = ImageLoader.loadImageFileGl("res/textures/oreon/Ground_11_DIF.png", 
                true, GL11.GL_LINEAR, false);
        int norm = ImageLoader.loadImageFileGl("res/textures/Brick_wall_002_NORM.jpg", 
                true, GL11.GL_LINEAR, false);
        int disp = ImageLoader.loadImageFileGl("res/textures/oreon/Ground_11_DISP.png", 
                true, GL11.GL_LINEAR, false);
        
        FPCamera fpc = new FPCamera(0, 0, 0);
        
        TestShader testShader = new TestShader();
        
        boolean escPressed = false;
        boolean enFpc = true;
        while (Window.keepAlive()) {
            
            if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)){
                if(!escPressed){
                    escPressed = true;
                    enFpc = !enFpc;
                    Mouse.setGrabbed(!Mouse.isGrabbed());
                    Mouse.setVisible(!Mouse.isVisible());
                }
            }else escPressed = false;
            
            if(enFpc) fpc.onUpdate();
            
            GL11.glClearColor(0.8f, 0.8f, 1, 1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glDisable(GL11.GL_CULL_FACE);
            
            testShader.start();
            vao.bind();
            vao.enableAttributes();
            index.bind();
            
            testShader.setCamera(fpc.getCamera());
            testShader.setTextures(diff, norm, disp);
            
            GL11.glDrawElements(GL11.GL_TRIANGLES, index.getSize(), GL11.GL_UNSIGNED_INT, 0);
            
            index.unbind();
            vao.disableAttributes();
            vao.unbind();
            testShader.stop();
            
            Window.update();
            Mouse.update();
            FrameTimer.update();
        }
        
        Game.exit(0);
    }
    
    private static class TestShader extends Shader {
        private final int colorTextureUnit = 0, normalTextureUnit = 1, 
                displacementTextureUnit = 2;
        
        private int vpMatLoc, camPosLoc;
        
        @Override
        protected void addShaders() {
            addShader(new String[]{
                "#version 400 core\n",
                
                "in vec2 position;",
                "out vec2 texCoords;",
                "out vec3 pos;",
                
                "uniform mat4 vpMat;",
                
                "void main(void){",
                    "texCoords = position;",
                    "pos = vec3((position.x -0.5) *4, -0.5, (position.y -0.5) *4);",
                    "gl_Position = vpMat * vec4(pos, 1);",
                "}"
            }, GL20.GL_VERTEX_SHADER, "POM vertex shader");
            addShader(new String[]{
                "#version 400 core\n",
                
                "in vec2 texCoords;",
                "in vec3 pos;",
                "out vec4 out_Color;",
                
                "uniform sampler2D colorTexture;",
                "uniform sampler2D normalTexture;",
                "uniform sampler2D displacementTexture;",
                
                "uniform vec3 camPos;",
                
                "void main(void){",
                    "float dispF = 0.05;",
                    "float dispO = 0.01;",
                    
                    "float disp = texture(displacementTexture, texCoords).r;",
                    "vec3 toCam = normalize(camPos -pos);",
                    "float bias = dispF / 2.0f;",
                    "vec2 tc = texCoords + toCam.xz * (disp * dispF + (-bias + (bias * dispO)));",
                    
                    "out_Color = texture(colorTexture, tc);",
                "}"
            }, GL20.GL_FRAGMENT_SHADER, "POM fragment shader");
        }
        
        @Override
        protected void bindAttribs() {
            bindAttribute(0, "position");
        }
        
        @Override
        protected void loadUniforms() {
            vpMatLoc = getUniformLocation("vpMat");
            camPosLoc = getUniformLocation("camPos");
            bindTextureUnit("colorTexture", colorTextureUnit);
            bindTextureUnit("normalTexture", normalTextureUnit);
            bindTextureUnit("displacementTexture", displacementTextureUnit);
        }
        
        public void setCamera(Camera cam){
            setUniform(vpMatLoc, Toolbox.matrixToFloatArray(cam.getViewProjectionMatrix()));
            setUniform(camPosLoc, cam.getPosition().x, cam.getPosition().y, cam.getPosition().z);
        }
        
        public void setTextures(int diff, int norm, int disp){
            bindTexture(diff, colorTextureUnit, GL11.GL_TEXTURE_2D);
            bindTexture(norm, normalTextureUnit, GL11.GL_TEXTURE_2D);
            bindTexture(disp, displacementTextureUnit, GL11.GL_TEXTURE_2D);
        }
    }
}
