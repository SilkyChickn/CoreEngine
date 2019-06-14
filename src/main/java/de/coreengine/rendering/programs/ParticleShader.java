package de.coreengine.rendering.programs;

import de.coreengine.asset.FileLoader;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.util.Toolbox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import javax.tools.Tool;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class ParticleShader extends Shader {

    private final int colorTextureUnit = 0;

    private int vpMatLoc, fMatLoc, scaleLoc, posLoc;

    @Override
    protected void addShaders() {
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "particle.vert", true),
                GL20.GL_VERTEX_SHADER, "Particle Vertex Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "particle.geo", true),
                GL32.GL_GEOMETRY_SHADER, "Particle Geometry Shader");
        addShader(FileLoader.getResource(Shader.SHADERS_LOCATION + "particle.frag", true),
                GL20.GL_FRAGMENT_SHADER, "Particle Fragment Shader");
    }

    @Override
    protected void bindAttribs() {
        bindAttribute(0, "position");
    }

    @Override
    protected void loadUniforms() {
        fMatLoc = getUniformLocation("fMat");
        vpMatLoc = getUniformLocation("vpMat");
        scaleLoc = getUniformLocation("scale");
        posLoc = getUniformLocation("pos");

        bindTextureUnit("colorTexture", colorTextureUnit);
    }

    /**Prepare camera to render next particles from
     *
     * @param cam Camera to render from
     */
    public void prepareCam(Camera cam){
        setUniform(vpMatLoc, Toolbox.matrixToFloatArray(cam.getViewProjectionMatrix()));
        setUniform(fMatLoc, Toolbox.matrixToFloatArray(cam.getFacingMatrix()));
    }

    /**Preparing stuff for next particles
     *
     * @param texture Texture of next particles
     */
    public void prepareParticles(int texture){
        bindTexture(texture, colorTextureUnit, GL11.GL_TEXTURE_2D);
    }

    /**Set transformation for next particle
     *
     * @param size Size of the next particle
     * @param pos Position of the next particle
     */
    public void setNextTransform(Vector2f size, Vector3f pos){
        setUniform(scaleLoc, size.x, size.y);
        setUniform(posLoc, pos.x, pos.y, pos.z);
    }
}
