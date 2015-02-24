package pl.rembol.jme3.particles;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.controls.TimeToLiveControl;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class SparkParticleEmitter extends ParticleEmitter {

	private static final float TTL = 4f;

	public SparkParticleEmitter(ApplicationContext applicationContext,
			ColorRGBA color, int numberOfParticles, Node parentNode) {
		super("spark", ParticleMesh.Type.Triangle, numberOfParticles);
		Material sparkMat = new Material(
				applicationContext.getBean(AssetManager.class),
				"Common/MatDefs/Misc/Particle.j3md");
		sparkMat.setTexture(
				"Texture",
				applicationContext.getBean(AssetManager.class).loadTexture(
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

		addControl(new TimeToLiveControl(applicationContext, TTL));

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
