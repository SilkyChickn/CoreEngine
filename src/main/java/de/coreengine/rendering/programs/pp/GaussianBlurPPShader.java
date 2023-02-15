package de.coreengine.rendering.programs.pp;

import de.coreengine.framework.Window;

public class GaussianBlurPPShader extends PPShader {

    private int directionsLoc, qualityLoc, sizeLoc, resolutionLoc;

    @Override
    protected String getPPFragShaderFile() {
        return "gaussianBlur.frag";
    }

    @Override
    protected void setUniformLocations() {
        directionsLoc = getUniformLocation("directions");
        qualityLoc = getUniformLocation("quality");
        sizeLoc = getUniformLocation("size");
        resolutionLoc = getUniformLocation("resolution");
    }

    /**
     * Prepare gaussian blur shader, by setting blur settings for next blur.
     * 
     * @param directions Directions to blur
     * @param quality    Blur quality
     * @param size       Blur size / radius
     */
    public void prepare(float directions, float quality, float size) {
        setUniform(directionsLoc, directions);
        setUniform(qualityLoc, quality);
        setUniform(sizeLoc, size);
        setUniform(resolutionLoc, Window.getWidth(), Window.getHeight());
    }
}
