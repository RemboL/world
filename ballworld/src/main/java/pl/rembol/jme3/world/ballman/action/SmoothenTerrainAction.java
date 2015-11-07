package pl.rembol.jme3.world.ballman.action;

import com.jme3.animation.LoopMode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.rts.particles.DustParticleEmitter;
import pl.rembol.jme3.world.smallobject.tools.Shovel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SmoothenTerrainAction extends BallManAction {

    private static final int HIT_FRAME = 20 * 1000 / 30;

    private static final int ANIMATION_LENGTH = 45 * 1000 / 30;

    private Vector2f start;
    private Vector2f end;
    private int border;

    private long animationStart;

    private final List<DustParticleEmitter> particleEmitters = new ArrayList<>();

    private boolean hit = false;

    private float minX;
    private float maxX;
    private float minY;
    private float maxY;
    private Random random = new Random();

    public SmoothenTerrainAction(GameState gameState, Vector2f start, Vector2f end, int border) {
        super(gameState);
        this.start = start;
        this.end = end;
        this.border = border;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        if (!assertWielded(ballMan, Optional.of(Shovel.class))) {
            return false;
        }
        if (!assertDistance(ballMan, start.add(end).divideLocal(2), start.distance(end) / 2)) {
            return false;
        }
        resetAnimation(ballMan);

        minX = start.x - border;
        maxX = end.x + border;
        minY = start.y - border;
        maxY = end.y + border;

        for (int i = 0; i < 10; ++i) {
            particleEmitters.add(createParticleEmiter());
        }

        return true;
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
    protected void doAct(BallMan ballMan, float tpf) {
        for (ParticleEmitter emitter : particleEmitters) {
            randomizeEmitterLocation(emitter);
        }

        if (animationEnded()) {
            resetAnimation(ballMan);
        }
        if (animationHit()) {
            hit = true;
            gameState.terrain.smoothenTerrain(start, end, border, .3f);
        }

    }

    @Override
    public boolean isFinished(BallMan ballMan) {
        return gameState.terrain.isTerrainSmooth(start, end);
    }

    @Override
    public void finish() {
        stop();
    }

    @Override
    public void stop() {
        for (DustParticleEmitter emitter : particleEmitters) {
            emitter.stopEmitting();
        }
    }

    private boolean animationEnded() {
        return System.currentTimeMillis() - animationStart >= ANIMATION_LENGTH;
    }

    private boolean animationHit() {
        return !hit && System.currentTimeMillis() - animationStart >= HIT_FRAME;
    }

    private void resetAnimation(BallMan ballMan) {
        ballMan.setAnimation("digWithShovel", LoopMode.DontLoop);
        animationStart = System.currentTimeMillis();
        hit = false;
    }

}
