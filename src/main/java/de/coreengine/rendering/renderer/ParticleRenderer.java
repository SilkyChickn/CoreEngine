package de.coreengine.rendering.renderer;

import de.coreengine.rendering.model.SimpleModel;
import de.coreengine.rendering.model.singletons.Quad2D;
import de.coreengine.rendering.programs.ParticleShader;
import de.coreengine.rendering.renderable.Camera;
import de.coreengine.rendering.renderable.Particle;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;

/**Renderer that can render 3d particles
 */
public class ParticleRenderer {

    private ParticleShader shader = new ParticleShader();

    /**Rendering a batch of particles sortet by texture
     *
     * @param particles Particle batches, sortet by textures
     * @param cam Camera to render particles from
     */
    public void render(HashMap<Integer, List<Particle>> particles, Camera cam){

        SimpleModel model = Quad2D.getInstance();

        shader.start();
        shader.prepareCam(cam);

        model.getVao().bind();
        model.getVao().enableAttributes();

        //Iterate particle textures
        for(int tex: particles.keySet()){
            shader.prepareParticles(tex);

            //Iterate particles for texture
            for(Particle particle: particles.get(tex)){
                shader.setNextTransform(particle.getSize(), particle.getPosition());
                GL11.glDrawArrays(GL11.GL_POINTS, 0, 1);
            }
        }

        model.getVao().disableAttributes();
        model.getVao().unbind();

        shader.stop();
    }
}
