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
package de.coreengine.rendering.renderable.gui;

import de.coreengine.asset.AssetDatabase;
import de.coreengine.rendering.model.Character;
import de.coreengine.rendering.model.Color;
import de.coreengine.util.Configuration;
import de.coreengine.util.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that represents a renderable text
 *
 * @author Darius Dinger
 */
public class GUIText {
    public static enum Alignment {
        CENTER, LEFT, RIGHT
    }

    private static final float DEFAULT_PADDING = Configuration.getValuef("GUI_TEXT_DEFAULT_PADDING");
    private static final float DEFAULT_FONT_SIZE = Configuration.getValuef("GUI_TEXT_DEFAULT_FONT_SIZE");
    private static final String DEFAULT_ALIGNMENT = Configuration.getValues("GUI_TEXT_DEFAULT_ALIGNMENT");

    // Text of the GUIText
    private String text = "";

    // Font of the text
    private String font;

    // Text alignment
    private Alignment alignment = Alignment.valueOf(DEFAULT_ALIGNMENT);

    // Size of the font
    private float fontSize = DEFAULT_FONT_SIZE;

    // Color ofthe font
    private Color fontColor = new Color();

    // Chars of the text to render
    private GUIChar[] chars = new GUIChar[0];

    // Padding at the border
    private float padding = DEFAULT_PADDING;

    // Width of a line
    private float lineWidth = 1.0f;

    // Package only constructor
    GUIText() {
    }

    /**
     * Setting text and recreating chars
     * 
     * @param text New text of the GUIText
     */
    public void setText(String text) {
        this.text = text;
        recreateChars();
    }

    /**
     * @return Text of the GUIText
     */
    public String getText() {
        return text;
    }

    /**
     * Setting font of the text and recreating chars
     * 
     * @param font New font of the text
     */
    public void setFont(String font) {
        this.font = font;
        recreateChars();
    }

    /**
     * Setting width of a line, when to make a line break
     *
     * @param width New line width
     */
    void setLineWidth(float width) {
        this.lineWidth = width;
    }

    /**
     * Setting the text alignment. Text will be aligned CENTERED, on the RIGHT or
     * LEFT edge of the GUIPane (note the padding).
     * 
     * @param alignment New alignment
     */
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
        recreateChars();
    }

    /**
     * Recreating all characters to render
     */
    private void recreateChars() {
        float cursor = 0;
        float line = 0.0f;

        List<GUIChar> lineChars = new LinkedList<>();
        List<GUIChar> textChars = new LinkedList<>();

        for (int i = 0; i < text.length(); i++) {
            int ascii = text.charAt(i);
            Character c = AssetDatabase.getFont(font).getCharacter(ascii);

            // Check if char exist in font
            if (c == null) {
                Logger.warn("Char not found", "Character '" + text.charAt(i) + "' not found in the font! (Skipping)");
                continue;
            }

            // Next line?
            if (cursor + c.getAdvancex() * fontSize > lineWidth - padding || ascii == 10) {
                alignChars(lineChars, cursor);

                line -= AssetDatabase.getFont(font).getLineHeight() * fontSize;
                cursor = 0;

                lineChars.clear();

                if (ascii == 10)
                    continue;
            }

            // Transform char
            GUIChar gc = new GUIChar();
            gc.setIndex(c.getIndex());
            gc.getOffset().set(cursor, line);
            textChars.add(gc);
            lineChars.add(gc);

            // Move cursor
            cursor += c.getAdvancex() * fontSize;
        }

        // Center last line horizontal
        alignChars(lineChars, cursor);

        // Center all chars vertical
        line -= AssetDatabase.getFont(font).getLineHeight() * fontSize;
        chars = new GUIChar[textChars.size()];
        for (int i = 0; i < textChars.size(); i++) {
            textChars.get(i).getOffset().y -= line / 2.0f;
            chars[i] = textChars.get(i);
        }
    }

    /**
     * Align chars of a line horizontal, by the selected alignment.
     * 
     * @param lineChars Chars to align
     * @param cursor    Cursor offset
     */
    private void alignChars(List<GUIChar> lineChars, float cursor) {
        for (GUIChar lineChar : lineChars) {
            switch (alignment) {
                case CENTER:
                    lineChar.getOffset().x += cursor / -2.0f;
                    break;
                case LEFT:
                    lineChar.getOffset().x += lineWidth / -2.0f + padding / 2.0f;
                    break;
                case RIGHT:
                    lineChar.getOffset().x += -cursor - padding / 2.0f + lineWidth / 2.0f;
                    break;
            }
        }
    }

    /**
     * @return Font of the text
     */
    public String getFont() {
        return font;
    }

    /**
     * @param fontSize New size of the font
     */
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        recreateChars();
    }

    /**
     * @return Characters of the text to render
     */
    public GUIChar[] getChars() {
        return chars;
    }

    /**
     * @return Font size of the text
     */
    public float getFontSize() {
        return fontSize;
    }

    /**
     * @param padding Padding of the gui text to the pane border
     */
    public void setPadding(float padding) {
        this.padding = padding;
        recreateChars();
    }

    /**
     * @return Read/writeable color of the text/font
     */
    public Color getFontColor() {
        return fontColor;
    }
}
