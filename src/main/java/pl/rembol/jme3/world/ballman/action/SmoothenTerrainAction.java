package pl.rembol.jme3.world.ballman.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.particles.DustParticleEmitter;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.smallobject.Shovel;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class SmoothenTerrainAction extends Action {

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

	@Autowired
	private AssetManager assetManager;

	@Autowired
	private Node rootNode;

	@Autowired
	private Terrain terrain;

	public SmoothenTerrainAction init(Vector2f start, Vector2f end, int border) {
		this.start = start;
		this.end = end;
		this.border = border;

		return this;
	}

	@Override
	protected void start(BallMan ballMan) {
		ballMan.wield(new Shovel(applicationContext));
		resetAnimation(ballMan);

		minX = start.x - border;
		maxX = end.x + border;
		minY = start.y - border;
		maxY = end.y + border;

		for (int i = 0; i < 10; ++i) {
			particleEmitters.add(createParticleEmiter());
		}
	}

	private DustParticleEmitter createParticleEmiter() {
		return new DustParticleEmitter(applicationContext)
				.doSetLocalTranslation(new Vector3f(start.x, terrain
						.getTerrain().getHeight(start), start.y));
	}

	private void randomizeEmitterLocation(ParticleEmitter emitter) {

		float x = minX + (maxX - minX) * random.nextFloat();
		float y = minY + (maxY - minY) * random.nextFloat();

		emitter.setLocalTranslation(x,
				terrain.getTerrain().getHeight(new Vector2f(x, y)), y);
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
			terrain.smoothenTerrain(start, end, border, .3f);
		}

	}

	@Override
	public boolean isFinished(BallMan ballMan) {
		return terrain.isTerrainSmooth(start, end);
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
