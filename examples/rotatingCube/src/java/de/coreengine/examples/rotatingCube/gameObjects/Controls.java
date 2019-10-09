package de.coreengine.examples.rotatingCube.gameObjects;

import de.coreengine.asset.FntLoader;
import de.coreengine.asset.dataStructures.Font;
import de.coreengine.rendering.model.Material;
import de.coreengine.rendering.renderable.gui.GUIPane;
import de.coreengine.rendering.renderer.MasterRenderer;
import de.coreengine.system.GameObject;

public class Controls extends GameObject {
    private static Font CONTROLS_FONT = FntLoader.loadFont("Margarine.fnt", true);

    private GUIPane controlsPane;

    @Override
    public void onInit() {
        super.onInit();

        //Create guipane and setting its parent to null, so its independent from other guis
        controlsPane = new GUIPane(null);
        controlsPane.setTexture(Material.TEXTURE_BLANK);
        controlsPane.setScaleX(1.0f);
        controlsPane.setScaleY(0.5f);
        controlsPane.setPosY(0.75f);

        //Preparing text
        controlsPane.getText().setFont(CONTROLS_FONT);
        controlsPane.getText().setFontSize(5.0f);
        controlsPane.getText().setText("Mouse Left: Rotate Camera\nMouse Right: Zoom Camera");
        controlsPane.enableText();
    }

    @Override
    public void onRender() {
        super.onRender();

        //I will render the gui pane 2 dimensional, but i could render it easy in the 3d world
        //By calling MasterRenderer.renderGui3D(controlsPane);
        MasterRenderer.renderGui2D(controlsPane);
    }
}
