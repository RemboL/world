package pl.rembol.jme3.world.ballman.action;

import com.jme3.animation.LoopMode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.particles.DustParticleEmitter;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.building.ConstructionSite;
import pl.rembol.jme3.world.smallobject.tools.Hammer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BuildAction extends BallManAction {

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

    private BuildingFactory factory;

    public BuildAction(GameState gameState, Vector2f position,
                       BuildingFactory factory) {
        super(gameState);
        this.position = position;
        this.factory = factory;

    }

    @Override
    public boolean isFinished(BallMan ballMan) {
        return isFinished;
    }

    @Override
    public void finish() {
        particleEmitters.forEach(DustParticleEmitter::stopEmitting);

    }

    @Override
    protected boolean start(BallMan ballMan) {
        if (!gameState.unitRegistry.isSpaceFree(gameState.terrain.getGroundPosition(position),
                factory.width())) {
            gameState.consoleLog.addLine("Can't build here, something's in the way");
            isFinished = true;
            return true;
        }

        if (!assertWielded(ballMan, Hammer.class)) {
            return false;
        }

        if (!assertDistance(ballMan, position, factory.width() + 3)) {
            return false;
        }

        if (ballMan.getOwner().retrieveResources(factory.cost())) {
            resetAnimation(ballMan);

            Building building = factory.create(gameState, position,
                    true);
            building.setOwner(ballMan.getOwner());

            constructionSite = new ConstructionSite(gameState, building, 5f);

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
        return new DustParticleEmitter(gameState)
                .doSetLocalTranslation(new Vector3f(position.x, gameState.terrain
                        .getTerrain().getHeight(position), position.y));
    }

    @Override
    protected void doAct(BallMan ballMan, float tpf) {
        if (constructionSite != null && constructionSite.isFinished()) {
            isFinished = true;
        }
        if (constructionSite != null && !isFinished) {
            constructionSite.addBuildProgress(tpf);

            particleEmitters.forEach(this::randomizeEmitterLocation);

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

    private void randomizeEmitterLocation(ParticleEmitter emitter) {

        float x = minX + (maxX - minX) * random.nextFloat();
        float y = minY + (maxY - minY) * random.nextFloat();

        emitter.setLocalTranslation(x,
                gameState.terrain.getTerrain().getHeight(new Vector2f(x, y)), y);
    }

}
