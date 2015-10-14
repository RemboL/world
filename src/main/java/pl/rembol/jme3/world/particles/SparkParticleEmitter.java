package pl.rembol.jme3.world.particles;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.controls.TimeToLiveControl;

public class SparkParticleEmitter extends ParticleEmitter {

    private static final float TTL = 4f;

    public SparkParticleEmitter(GameState gameState,
                                ColorRGBA color, int numberOfParticles, Node parentNode) {
        super("spark", ParticleMesh.Type.Triangle, numberOfParticles);
        Material sparkMat = new Material(gameState.assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
        sparkMat.setTexture("Texture", gameState.assetManager.loadTexture(
                "Effects/Explosion/spark.png"));
        setMaterial(sparkMat);
        setImagesX(1);
        setImagesY(1);
        getParticleInfluencer().setInitialVelocity(new Vector3f(0, -10, 0));
        getParticleInfluencer().setVelocityVariation(1f);
        setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
        setFacingVelocity(true);
        setStartColor(color);
        setGravity(0, 10, 0);
        setHighLife(TTL);
        setLowLife(TTL);
        setStartSize(1f);
        setEndSize(1f);

        parentNode.attachChild(this);

        addControl(new TimeToLiveControl(gameState, TTL));

    }

    public SparkParticleEmitter doSetLocalTranslation(Vector3f position) {
        setLocalTranslation(position);
        return this;
    }

    public SparkParticleEmitter emit() {
        emitAllParticles();
        return this;
    }

}
