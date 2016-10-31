package pl.rembol.jme3.world.building;

import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.particles.DustParticleEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BuildingDestructionControl extends AbstractControl {

    private static final float MAX_TIME = 5f;

    private float timeToLive = MAX_TIME;

    private Building building;

    private final List<DustParticleEmitter> particleEmitters = new ArrayList<>();

    private Vector2f start;

    private float minX;
    private float maxX;
    private float minY;
    private float maxY;
    private Random random = new Random();

    private RtsGameState gameState;

    public BuildingDestructionControl(RtsGameState gameState, Building building) {
        this.gameState = gameState;
        this.building = building;

        start = new Vector2f(building.getNode().getWorldTranslation().x,
                building.getNode().getWorldTranslation().z);

        minX = start.x - 7.5f;
        maxX = start.x + 7.5f;
        minY = start.y - 7.5f;
        maxY = start.y + 7.5f;

        for (int i = 0; i < 10; ++i) {
            particleEmitters.add(createParticleEmiter());
        }

        building.getNode().addControl(this);

    }

    private DustParticleEmitter createParticleEmiter() {
        return new DustParticleEmitter(gameState)
                .doSetLocalTranslation(new Vector3f(start.x, gameState.terrain
                        .getTerrain().getHeight(start), start.y));
    }

    private void randomizeEmitterLocation(ParticleEmitter emitter) {

        float x = minX + (maxX - minX) * random.nextFloat();
        float y = minY + (maxY - minY) * random.nextFloat();

        emitter.setLocalTranslation(x,
                gameState.terrain.getTerrain().getHeight(new Vector2f(x, y)), y);
    }

    @Override
    protected void controlUpdate(float tpf) {
        timeToLive -= tpf;

        building.getNode().move((random.nextFloat() - .5f) * tpf,
                -tpf * building.getHeight() / MAX_TIME,
                (random.nextFloat() - .5f) * tpf);
        building.getNode().rotate((random.nextFloat() - .5f) * .1f,
                (random.nextFloat() - .5f) * .1f,
                (random.nextFloat() - .5f) * .1f);

        particleEmitters.forEach(this::randomizeEmitterLocation);

        if (timeToLive < 0) {
            particleEmitters.forEach(DustParticleEmitter::stopEmitting);
            gameState.rootNode.detachChild(building.getParentNode());
            building.getNode().removeControl(this);
        }
    }

    @Override
    protected void controlRender(RenderManager paramRenderManager,
                                 ViewPort paramViewPort) {
    }

}
