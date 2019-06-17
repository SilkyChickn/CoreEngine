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
package io.github.suuirad.coreengine.system.gameObjects.gui;

import io.github.suuirad.coreengine.asset.ImageLoader;
import io.github.suuirad.coreengine.asset.meta.Font;
import io.github.suuirad.coreengine.rendering.renderable.gui.GUIPane;
import io.github.suuirad.coreengine.system.GameObject;
import io.github.suuirad.coreengine.util.Configuration;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

/**Class that represents a numeric input field
 *
 * @author Darius Dinger
 */
public class NumericUpDown extends GameObject{
    private static final float DEFAULT_STEP = 
            Configuration.getValuef("NUMERIC_DEFAULT_STEP");
    private static final float DEFAULT_MAX = 
            Configuration.getValuef("NUMERIC_DEFAULT_MAX");
    private static final float DEFAULT_MIN = 
            Configuration.getValuef("NUMERIC_DEFAULT_MIN");
    private static final int DEFAULT_UP_BUTTON_IMG = 
            ImageLoader.loadImageFileGl("res/up.png",
                    true, GL11.GL_LINEAR, true);
    private static final int DEFAULT_DOWN_BUTTON_IMG = 
            ImageLoader.loadImageFileGl("res/down.png",
                    true, GL11.GL_LINEAR, true);
    
    //Components
    private GUIPane pane;
    private Button upBt, downBt;
    private TextField textField;
    
    //Numeric
    private float value, step = DEFAULT_STEP, max = DEFAULT_MAX, min = DEFAULT_MIN;
    private Set<String> filter = new HashSet<>();
    
    /**Creating new Numeric up down and setting ts parent or null, if no parent gui
     * exist
     * 
     * @param parent Parent gui or null
     * @param font Font of the numeric
     */
    public NumericUpDown(GUIPane parent, Font font) {
        pane = new GUIPane(parent);
        textField = new TextField(pane, font);
        upBt = new Button(pane);
        downBt = new Button(pane);
        
        filter.add("0");
        filter.add("1");
        filter.add("2");
        filter.add("3");
        filter.add("4");
        filter.add("5");
        filter.add("6");
        filter.add("7");
        filter.add("8");
        filter.add("9");
        filter.add(".");
        textField.setFilter(filter);
    }
    
    @Override
    public void onInit() {
        
        upBt.getPane().setPosX(0.75f);
        upBt.getPane().setPosY(0.5f);
        upBt.getPane().setPosZ(0.1f);
        upBt.getPane().setScaleX(0.25f);
        upBt.getPane().setScaleY(0.5f);
        upBt.getPane().getColor().set(textField.getBackgroundColor());
        upBt.setTexture(DEFAULT_UP_BUTTON_IMG);
        upBt.setOverTexture(DEFAULT_UP_BUTTON_IMG);
        upBt.setPressedTexture(DEFAULT_UP_BUTTON_IMG);
        upBt.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                value = Float.max(Float.min(value +step, max), min);
                String txt = (value % 1.0f == 0.0f) ? 
                        Integer.toString((int)value) : Float.toString(value);
                textField.setText(txt);
            }
            
            @Override
            public void onPress() {}
            
            @Override
            public void onMouseOver() {}
            
            @Override
            public void onMouseLeave() {
                upBt.getPane().getColor().set(textField.getBackgroundColor());
            }
            
            @Override
            public void onMouseEnter() {
                upBt.getPane().getColor().set(textField.getActiveColor());
            }
        });
        addChild(upBt);
        
        downBt.getPane().setPosX(0.75f);
        downBt.getPane().setPosY(-0.5f);
        downBt.getPane().setPosZ(0.1f);
        downBt.getPane().setScaleX(0.25f);
        downBt.getPane().setScaleY(0.5f);
        downBt.getPane().getColor().set(textField.getBackgroundColor());
        downBt.setTexture(DEFAULT_DOWN_BUTTON_IMG);
        downBt.setOverTexture(DEFAULT_DOWN_BUTTON_IMG);
        downBt.setPressedTexture(DEFAULT_DOWN_BUTTON_IMG);
        downBt.setListener(new ButtonListener() {
            @Override
            public void onClick() {
                value = Float.max(Float.min(value -step, max), min);
                value -= value % step;
                String txt = (value % 1.0f == 0.0f) ? 
                        Integer.toString((int)value) : Float.toString(value);
                textField.setText(txt);
            }
            
            @Override
            public void onPress() {}
            
            @Override
            public void onMouseOver() {}
            
            @Override
            public void onMouseLeave() {
                downBt.getPane().getColor().set(textField.getBackgroundColor());
            }
            
            @Override
            public void onMouseEnter() {
                downBt.getPane().getColor().set(textField.getActiveColor());
            }
        });
        addChild(downBt);
        
        textField.getPane().setPosX(-0.25f);
        textField.getPane().setPosY(0.0f);
        textField.getPane().setPosZ(0.1f);
        textField.getPane().setScaleX(0.75f);
        textField.getPane().setScaleY(1.0f);
        textField.setCursor("");
        textField.setListener((String newText) -> {
            if(newText.equals("")){
                setValue(min);
                return;
            }
            try{
                value = Float.max(Float.min(Float.parseFloat(newText), max), min);
                value -= value % step;
                if(value != Float.parseFloat(newText)){
                    String txt = (value % 1.0f == 0.0f) ? 
                            Integer.toString((int)value) : Float.toString(value);
                    textField.setText(txt);
                }
            }catch(NumberFormatException e){
                setValue(value);
            }
        });
        addChild(textField);
        
        setValue(value);
        
        super.onInit();
    }
    
    /**@return Frame pane component
     */
    public GUIPane getPane() {
        return pane;
    }
    
    /**@return Down button component
     */
    public Button getDownBt() {
        return downBt;
    }
    
    /**@return Up button component
     */
    public Button getUpBt() {
        return upBt;
    }
    
    /**@return Textfield component
     */
    public TextField getTextField() {
        return textField;
    }
    
    /**@return Current value of the numeric
     */
    public float getValue() {
        return value;
    }
    
    /**@param value New value of the text field
     */
    public void setValue(float value) {
        this.value = Float.max(Float.min(value, max), min);
        this.value -= this.value % step;
        String txt = (value % 1.0f == 0.0f) ? 
                Integer.toString((int)value) : Float.toString(value);
        textField.setText(txt);
    }
    
    /**@param max New maximum value
     */
    public void setMax(float max) {
        this.max = max;
    }
    
    /**@param min New minimum value
     */
    public void setMin(float min) {
        this.min = min;
    }
}
