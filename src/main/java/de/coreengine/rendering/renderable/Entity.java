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
package de.coreengine.rendering.renderable;

import de.coreengine.rendering.model.Color;
import de.coreengine.rendering.model.Transformation;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.util.Toolbox;

/**
 * Class that represents an entity in the world
 *
 * @author Darius Dinger
 */
public class Entity {

    // Transformation of the entity
    private Transformation transform = new Transformation();

    // Model of the entity
    private String model = null;

    // Should the entity rotate and move with the camera
    private boolean rotateWithCam = false;

    // Color for the mouse picking
    private final Color pickColor;

    public Entity() {
        pickColor = Toolbox.generateRandomColor();
    }

    /**
     * @param model New model of the entity
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return Read/writeable transformation of the entity
     */
    public Transformation getTransform() {
        return transform;
    }

    /**
     * @return Model of the entity
     */
    public String getModel() {
        return model;
    }

    /**
     * Should the entity rotate and move with the camera
     * 
     * @param rotateWithCam New value
     */
    public void setRotateWithCam(boolean rotateWithCam) {
        this.rotateWithCam = rotateWithCam;
    }

    /**
     * @return Should the entity rotate and move with the camera
     */
    public boolean isRotateWithCam() {
        return rotateWithCam;
    }

    /**
     * @return Color of this Entity in the mouse picker
     */
    public Color getPickColor() {
        return pickColor;
    }

    /**
     * Returns true if mouse is over entity. <br>
     * 
     * <b>Only works, if picking is enabled!!!</b>
     * 
     * @return Is mouse over entity
     */
    public boolean isMouseOver() {
        return pickColor.compare(MasterRenderer.getPickedColor());
    }
}
