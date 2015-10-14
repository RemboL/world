package pl.rembol.jme3.world.particles;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.controls.TimeToLiveControl;

public class DustParticleEmitter extends ParticleEmitter {

    private static final int DUST_PARTICLE_HIGH_LIFE_IN_SECONDS = 5;

    private GameState gameState;

    public DustParticleEmitter(GameState gameState) {
        super("Dust", ParticleMesh.Type.Triangle, 20);
        this.gameState = gameState;

        Material dustMaterial = new Material(
                gameState.assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
        dustMaterial.setTexture(
                "Texture",
                gameState.assetManager.loadTexture(
                        "Effects/Explosion/flame.png"));
        setMaterial(dustMaterial);
        setImagesX(2);
        setImagesY(2);
        setRotateSpeed(4);
        setSelectRandomImage(true);
        getParticleInfluencer().setInitialVelocity(new Vector3f(0, -1, 0));
        setStartColor(new ColorRGBA(1f, .75f, .5f, .75f));
        setGravity(0, 0, 0);
        setHighLife(DUST_PARTICLE_HIGH_LIFE_IN_SECONDS * 1f);
        setLowLife(DUST_PARTICLE_HIGH_LIFE_IN_SECONDS * .4f);
        setStartSize(1f);
        setEndSize(4f);
        getParticleInfluencer().setVelocityVariation(2f);
        gameState.rootNode.attachChild(this);
    }

    public DustParticleEmitter doSetLocalTranslation(Vector3f position) {
        setLocalTranslation(position);
        return this;
    }

    public void stopEmitting() {
        setParticlesPerSec(0f);
        addControl(new TimeToLiveControl(gameState, DUST_PARTICLE_HIGH_LIFE_IN_SECONDS));
    }

}
