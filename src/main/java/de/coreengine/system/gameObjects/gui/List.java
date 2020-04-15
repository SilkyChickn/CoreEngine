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
package de.coreengine.system.gameObjects.gui;

import de.coreengine.framework.Mouse;
import de.coreengine.rendering.model.Color;
import de.coreengine.rendering.model.Font;
import de.coreengine.rendering.renderable.gui.GUIPane;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;
import de.coreengine.util.Configuration;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;

/**
 * List gui element
 *
 * @author Darius Dinger
 */
public class List extends GameObject {
    private static final float DEFAULT_ITEM_SCALE = Configuration.getValuef("LIST_DEFAULT_ITEM_SCALE");
    private static final float DEFAULT_FONT_SIZE = Configuration.getValuef("LIST_DEFAULT_FONT_SIZE");
    private static final float DEFAULT_SCROLL_SPEED = Configuration.getValuef("LIST_DEFAULT_SCROLL_SPEED");
    private static final float DEFAULT_COOLDOWN = Configuration.getValuef("LIST_DEFAULT_COOLDOWN");

    private static final float[] DEFAULT_ITEM_COLOR = Configuration.getValuefa("LIST_DEFAULT_ITEM_COLOR");
    private static final float[] DEFAULT_FONT_COLOR = Configuration.getValuefa("LIST_DEFAULT_FONT_COLOR");
    private static final float[] DEFAULT_SELECTED_COLOR = Configuration.getValuefa("LIST_DEFAULT_SELECTED_COLOR");
    private static final float[] DEFAULT_BACKGROUND_COLOR = Configuration.getValuefa("LIST_DEFAULT_BACKGROUND_COLOR");

    // Item settings
    private float itemScale = DEFAULT_ITEM_SCALE;
    private float fontSize = DEFAULT_FONT_SIZE;
    private String font;

    // Colors
    private Color selectedColor = new Color(DEFAULT_SELECTED_COLOR[0], DEFAULT_SELECTED_COLOR[1],
            DEFAULT_SELECTED_COLOR[2]);
    private Color itemColor = new Color(DEFAULT_ITEM_COLOR[0], DEFAULT_ITEM_COLOR[1], DEFAULT_ITEM_COLOR[2]);
    private Color fontColor = new Color(DEFAULT_FONT_COLOR[0], DEFAULT_FONT_COLOR[1], DEFAULT_FONT_COLOR[2]);

    // List background
    private GUIPane background, topPane, bottomPane;

    // Items of the list
    private LinkedList<GUIPane> items = new LinkedList<>();
    private LinkedList<GUIPane> visibleItems = new LinkedList<>();
    private String selectedItem = null;

    // Scroll speed
    private float scrollSpeed = DEFAULT_SCROLL_SPEED;
    private float cooldown = DEFAULT_COOLDOWN;

    // Current list scroll offset
    private float offset = 0.0f;
    private float speedy = 0.0f;

    /**
     * @param parent Parent pane of the list or null
     * @param font   Font of the list
     */
    public List(GUIPane parent, String font) {
        this.font = font;
        background = new GUIPane(parent);
        background.getColor().set(DEFAULT_BACKGROUND_COLOR[0], DEFAULT_BACKGROUND_COLOR[1],
                DEFAULT_BACKGROUND_COLOR[2]);
        revalidate();
    }

    /**
     * @return Is mouse over one of the components of the list
     */
    private boolean isHover() {
        if (items.stream().anyMatch(GUIPane::isMouseOver)) {
            return true;
        }
        return background.isMouseOver() || topPane.isMouseOver() || bottomPane.isMouseOver();
    }

    /**
     * Revalidating list gui
     */
    private void revalidate() {

        // replace top and bottom pane
        topPane = new GUIPane(background);
        topPane.setPosX(0.0f);
        topPane.setPosY(1.0f - itemScale);
        topPane.setPosZ(0.2f);
        topPane.setScaleX(1.0f);
        topPane.setScaleY(itemScale);
        topPane.getColor().set(background.getColor());

        bottomPane = new GUIPane(background);
        bottomPane.setPosX(0.0f);
        bottomPane.setPosY(-1.0f + itemScale);
        bottomPane.setPosZ(0.2f);
        bottomPane.setScaleX(1.0f);
        bottomPane.setScaleY(itemScale);
        bottomPane.getColor().set(background.getColor());

        items.stream().peek((item) -> item.getColor().set(itemColor))
                .forEachOrdered((item) -> item.getText().getFontColor().set(itemColor));
    }

    /**
     * Adding item to the list. If item already exist in the list it return false
     * else it returns true
     * 
     * @param item Item to add
     * @return Could the item be added
     */
    public boolean addItem(String item) {

        // Check if item already exist
        if (items.stream().anyMatch((i) -> (i.getText().getText().equals(item)))) {
            return false;
        }

        // create and add item
        GUIPane newItem = new GUIPane(background);
        newItem.getColor().set(itemColor);
        newItem.setPosX(0.0f);
        newItem.setPosZ(0.1f);
        newItem.setScaleX(1.0f);
        newItem.setScaleY(itemScale);
        newItem.getText().setFont(font);
        newItem.getText().setText(item);
        newItem.getText().setFontSize(fontSize);
        newItem.getText().getFontColor().set(fontColor);
        newItem.enableText();
        items.add(newItem);

        recalcPositions();
        getVisibleItems();

        return true;
    }

    /**
     * Clear list
     */
    public void clear() {
        items.clear();
        offset = 0.0f;
    }

    /**
     * @return Itemcount of the list
     */
    public int size() {
        return items.size();
    }

    /**
     * Removing item from list
     * 
     * @param item Item name to remove
     * @return Could the item be removed
     */
    public boolean removeItem(String item) {
        for (GUIPane i : items) {
            if (i.getText().getText().equals(item)) {
                items.remove(i);

                recalcPositions();
                getVisibleItems();

                return true;
            }
        }
        return false;
    }

    @Override
    public void onUpdate() {
        if (isHover()) {

            // Scrolling
            if (Mouse.getDWheel() != 0)
                speedy = -scrollSpeed * (float) Mouse.getDWheel();

            // Check for items clicked
            if (Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                selectedItem = null;
                items.forEach((item) -> {
                    if (item.isMouseOver()) {
                        item.getColor().set(selectedColor);
                        selectedItem = item.getText().getText();
                    } else
                        item.getColor().set(itemColor);
                });
            }
        }

        // Check scroll range
        if (!items.isEmpty()) {
            if (items.getFirst().getPosY() + items.getFirst().getScaleY() * 3.0f <= background.getPosY()
                    + background.getScaleY() && speedy < 0
                    || items.getLast().getPosY() - items.getLast().getScaleY() * 3.0f >= background.getPosY()
                            - background.getScaleY() && speedy > 0) {
                speedy = 0;
            }
        }

        // Scroll items
        if (speedy != 0) {
            offset += speedy;
            speedy *= cooldown;

            recalcPositions();
            getVisibleItems();
        }

        super.onUpdate();
    }

    /**
     * @return Current selected item of the list or null, if no item is selected
     */
    public String getSelectedItem() {
        return selectedItem;
    }

    /**
     * Recalc positons of all items
     */
    private void recalcPositions() {
        if (items.isEmpty())
            return;

        float y = offset + 1.0f - (items.getFirst().getScaleY() / background.getScaleY() * 3.0f);
        for (GUIPane pane : items) {
            pane.setPosY(y);
            y -= pane.getScaleY() / background.getScaleY() * 2.0f;
        }
    }

    /**
     * @return Main pane of the list
     */
    public GUIPane getPane() {
        return background;
    }

    /**
     * Recalculate the visible items list
     */
    private void getVisibleItems() {
        visibleItems.clear();
        items.stream()
                .filter((pane) -> (pane.getPosY() + pane.getScaleY() <= background.getPosY() + background.getScaleY()
                        && pane.getPosY() - pane.getScaleY() >= background.getPosY() - background.getScaleY()))
                .forEachOrdered((pane) -> visibleItems.add(pane));
    }

    @Override
    public void onRender() {
        MasterRenderer.renderGui2D(background);
        MasterRenderer.renderGui2D(topPane);
        MasterRenderer.renderGui2D(bottomPane);

        visibleItems.forEach(MasterRenderer::renderGui2D);

        super.onRender();
    }

    /**
     * @return Read/Writeable color of a selected item
     */
    public Color getSelectedColor() {
        return selectedColor;
    }

    /**
     * @return Read/Writeable color of a item
     */
    public Color getItemColor() {
        return itemColor;
    }
}
