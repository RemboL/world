package pl.rembol.jme3.world.ballman.action;

import com.jme3.animation.LoopMode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.building.ConstructionSite;
import pl.rembol.jme3.world.hud.ConsoleLog;
import pl.rembol.jme3.world.particles.DustParticleEmitter;
import pl.rembol.jme3.world.smallobject.tools.Hammer;
import pl.rembol.jme3.world.terrain.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BuildAction extends Action<BallMan> {

    private static final int HIT_FRAME = 20 * 1000 / 30;

    private static final int ANIMATION_LENGTH = 35 * 1000 / 30;

    private long animationStart;

    private final List<DustParticleEmitter> particleEmitters = new ArrayList<>();

    private boolean hit = false;

    private float minX;
    private float maxX;
    private float minY;
    private float maxY;
    private Random random = new Random();

    private boolean isFinished = false;

    private ConstructionSite constructionSite;

    private Vector2f position;

    @Autowired
    private Terrain terrain;

    @Autowired
    private Node rootNode;

    @Autowired
    private ConsoleLog consoleLog;

    @Autowired
    private UnitRegistry gameState;

    private BuildingFactory factory;

    @Override
    public boolean isFinished(BallMan ballMan) {
        return isFinished;
    }

    @Override
    public void finish() {
        for (DustParticleEmitter emitter : particleEmitters) {
            emitter.stopEmitting();
        }

    }

    public BuildAction init(Vector2f position, BuildingFactory factory) {
        this.position = position;
        this.factory = factory;

        return this;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        if (!gameState.isSpaceFree(terrain.getGroundPosition(position),
                factory.width())) {
            consoleLog.addLine("Can't build here, something's in the way");
            isFinished = true;
            return true;
        }

        if (!assertWielded(ballMan, Optional.of(Hammer.class))) {
            return false;
        }

        if (!assertDistance(ballMan, position, factory.width() + 3)) {
            return false;
        }

        if (ballMan.getOwner().retrieveResources(factory.cost())) {
            resetAnimation(ballMan);

            Building building = factory.create(applicationContext, position,
                    true);
            building.setOwner(ballMan.getOwner());
            constructionSite = new ConstructionSite(applicationContext,
                    building, 5f);

            minX = position.getX() - constructionSite.getBuilding().getWidth()
                    - 5;
            maxX = position.getX() + constructionSite.getBuilding().getWidth()
                    + 5;
            minY = position.getY() - constructionSite.getBuilding().getWidth()
                    - 5;
            maxY = position.getY() + constructionSite.getBuilding().getWidth()
                    + 5;

            for (int i = 0; i < 10; ++i) {
                particleEmitters.add(createParticleEmitter());
            }
        } else {
            isFinished = true;
        }

        return true;

    }

    private DustParticleEmitter createParticleEmitter() {
        return new DustParticleEmitter(applicationContext)
                .doSetLocalTranslation(new Vector3f(position.x, terrain
                        .getTerrain().getHeight(position), position.y));
    }

    @Override
    protected void doAct(BallMan ballMan, float tpf) {
        if (constructionSite != null && constructionSite.isFinished()) {
            isFinished = true;
        }
        if (!isFinished) {
            constructionSite.addBuildProgress(tpf);

            for (ParticleEmitter emitter : particleEmitters) {
                randomizeEmitterLocation(emitter);
            }

            if (animationEnded()) {
                resetAnimation(ballMan);
            }
            if (animationHit()) {
                hit = true;
            }

        }

    }

    private boolean animationEnded() {
        return System.currentTimeMillis() - animationStart >= ANIMATION_LENGTH;
    }

    private boolean animationHit() {
        return !hit && System.currentTimeMillis() - animationStart >= HIT_FRAME;
    }

    private void resetAnimation(BallMan ballMan) {
        ballMan.setAnimation("strike", LoopMode.DontLoop);
        animationStart = System.currentTimeMillis();
        hit = false;
    }

    protected void randomizeEmitterLocation(ParticleEmitter emitter) {

        float x = minX + (maxX - minX) * random.nextFloat();
        float y = minY + (maxY - minY) * random.nextFloat();

        emitter.setLocalTranslation(x,
                terrain.getTerrain().getHeight(new Vector2f(x, y)), y);
    }

}
