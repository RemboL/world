package pl.rembol.jme3.world.building;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import pl.rembol.jme3.world.particles.DustParticleEmitter;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

public class BuildingDestructionControl extends AbstractControl implements
		ApplicationContextAware {

	private static final float MAX_TIME = 5f;

	private float timeToLive = MAX_TIME;

	private Building building;

	private final List<DustParticleEmitter> particleEmitters = new ArrayList<>();

	private ApplicationContext applicationContext;

	private Vector2f start;

	private float minX;
	private float maxX;
	private float minY;
	private float maxY;
	private Random random = new Random();

	@Autowired
	private Terrain terrain;

	@Autowired
	private Node rootNode;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void init(Building building) {
		this.building = building;

		start = new Vector2f(
				building.getBuildingNode().getWorldTranslation().x, building
						.getBuildingNode().getWorldTranslation().z);

		minX = start.x - 7.5f;
		maxX = start.x + 7.5f;
		minY = start.y - 7.5f;
		maxY = start.y + 7.5f;

		for (int i = 0; i < 10; ++i) {
			particleEmitters.add(createParticleEmiter());
		}

		building.getBuildingNode().addControl(this);

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
	protected void controlUpdate(float tpf) {
		timeToLive -= tpf;

		building.getBuildingNode().move((random.nextFloat() - .5f) * tpf,
				-tpf * building.getHeight() / MAX_TIME,
				(random.nextFloat() - .5f) * tpf);
		building.getBuildingNode().rotate((random.nextFloat() - .5f) * .1f,
				(random.nextFloat() - .5f) * .1f,
				(random.nextFloat() - .5f) * .1f);

		particleEmitters.forEach(emitter -> randomizeEmitterLocation(emitter));

		if (timeToLive < 0) {
			particleEmitters.forEach(emitter -> emitter.stopEmitting());
			rootNode.detachChild(building.getParentNode());
			building.getBuildingNode().removeControl(this);
		}
	}

	@Override
	protected void controlRender(RenderManager paramRenderManager,
			ViewPort paramViewPort) {
	}

}
