package pl.rembol.jme3.world.ballman.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.rembol.jme3.controls.TimeToLiveControl;
import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.ConstructionSite;
import pl.rembol.jme3.world.house.House;
import pl.rembol.jme3.world.smallobject.Hammer;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class BuildHouseAction extends Action {

	private static final int DUST_PARTICLE_HIGH_LIFE_IN_SECONDS = 5;

	private static final int HIT_FRAME = 20 * 1000 / 30;

	private static final int ANIMATION_LENGTH = 35 * 1000 / 30;

	private Terrain terrain;

	private long animationStart;

	private final List<ParticleEmitter> particleEmitters = new ArrayList<>();

	private boolean hit = false;

	private float minX;
	private float maxX;
	private float minY;
	private float maxY;
	private Random random = new Random();

	private ConstructionSite constructionSite;

	private Vector2f position;

	public BuildHouseAction(Terrain terrain, Vector2f position) {
		this.terrain = terrain;
		this.position = position;
	}

	@Override
	protected void start(BallMan ballMan, GameRunningAppState appState) {
		ballMan.wield(new Hammer(appState));
		resetAnimation(ballMan);

		constructionSite = new ConstructionSite(new House(position, appState, true),
				appState, 15f);

		minX = position.getX() - constructionSite.getBuilding().getWidth() - 5;
		maxX = position.getX() + constructionSite.getBuilding().getWidth() + 5;
		minY = position.getY() - constructionSite.getBuilding().getWidth() - 5;
		maxY = position.getY() + constructionSite.getBuilding().getWidth() + 5;

		for (int i = 0; i < 10; ++i) {
			particleEmitters.add(createParticleEmiter(
					appState.getAssetManager(), appState.getRootNode()));
		}

	}

	private ParticleEmitter createParticleEmiter(AssetManager assetManager,
			Node rootNode) {
		ParticleEmitter emitter = new ParticleEmitter("Debris",
				ParticleMesh.Type.Triangle, 20);
		Material dustMaterial = new Material(assetManager,
				"Common/MatDefs/Misc/Particle.j3md");
		dustMaterial.setTexture("Texture",
				assetManager.loadTexture("Effects/Explosion/flame.png"));
		emitter.setMaterial(dustMaterial);
		emitter.setImagesX(2);
		emitter.setImagesY(2);
		emitter.setRotateSpeed(4);
		emitter.setSelectRandomImage(true);
		emitter.getParticleInfluencer().setInitialVelocity(
				new Vector3f(0, -1, 0));
		emitter.setStartColor(new ColorRGBA(1f, .75f, .5f, .75f));
		emitter.setGravity(0, 0, 0);
		emitter.setHighLife(DUST_PARTICLE_HIGH_LIFE_IN_SECONDS * 1f);
		emitter.setLowLife(DUST_PARTICLE_HIGH_LIFE_IN_SECONDS * .4f);
		emitter.setStartSize(1f);
		emitter.setEndSize(4f);
		emitter.getParticleInfluencer().setVelocityVariation(2f);
		rootNode.attachChild(emitter);
		emitter.setLocalTranslation(position.x,
				terrain.getTerrain().getHeight(position), position.y);
		return emitter;
	}

	private void randomizeEmitterLocation(ParticleEmitter emitter) {

		float x = minX + (maxX - minX) * random.nextFloat();
		float y = minY + (maxY - minY) * random.nextFloat();

		emitter.setLocalTranslation(x,
				terrain.getTerrain().getHeight(new Vector2f(x, y)), y);
	}

	@Override
	protected void doAct(BallMan ballMan, float tpf) {

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

	@Override
	public boolean isFinished(BallMan ballMan) {
		return constructionSite != null && constructionSite.isFinished();
	}

	@Override
	public void finish() {
		for (ParticleEmitter emitter : particleEmitters) {
			emitter.setParticlesPerSec(0f);
			emitter.addControl(new TimeToLiveControl(
					DUST_PARTICLE_HIGH_LIFE_IN_SECONDS));
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

}
