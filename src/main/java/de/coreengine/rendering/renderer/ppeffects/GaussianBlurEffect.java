package de.coreengine.rendering.renderer.ppeffects;

import java.util.List;

import de.coreengine.rendering.programs.pp.GaussianBlurPPShader;
import de.coreengine.util.Configuration;

public class GaussianBlurEffect extends PostProcessingEffect {
    private static final float DEFAULT_DIRECTIONS = Configuration.getValuef("GAUSS_BLUR_DEFAULT_DIRECTIONS");
    private static final float DEFAULT_QUALITY = Configuration.getValuef("GAUSS_BLUR_DEFAULT_QUALITY");
    private static final float DEFAULT_SIZE = Configuration.getValuef("GAUSS_BLUR_DEFAULT_SIZE");

    // Blur directions
    private float directions = DEFAULT_DIRECTIONS;

    // Quality of the blur
    private float quality = DEFAULT_QUALITY;

    // Blur size / radius
    private float size = DEFAULT_SIZE;

    public GaussianBlurEffect() {
        super(new GaussianBlurPPShader());
    }

    @Override
    protected void setUniforms() {
        ((GaussianBlurPPShader) shader).prepare(directions, quality, size);
    }

    /**
     * Setting the directions to blur.
     * 
     * @param directions New blur directions
     */
    public void setDirections(float directions) {
        this.directions = directions;
    }

    /**
     * Setting the blur quality.
     * 
     * @param quality New blur quality
     */
    public void setQuality(float quality) {
        this.quality = quality;
    }

    /**
     * Setting the blur size. Often called blur radius.
     * 
     * @param size New blur size / radius
     */
    public void setSize(float size) {
        this.size = size;
    }

    /**
     * Getting the directions to blur.
     * 
     * @return Current blur direction
     */
    public float getDirections() {
        return directions;
    }

    /**
     * Getting the blur quality.
     * 
     * @return Current blur quality
     */
    public float getQuality() {
        return quality;
    }

    /**
     * Getting the blur size. Often called blur radius.
     * 
     * @return Current blur size / radius
     */
    public float getSize() {
        return size;
    }

    @Override
    public void addImpliedEffects(List<PostProcessingEffect> effects) {
    }
}
