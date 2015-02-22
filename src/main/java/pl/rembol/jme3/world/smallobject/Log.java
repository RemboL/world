package pl.rembol.jme3.world.smallobject;

import org.springframework.context.ApplicationContext;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Log extends SmallObject {

	private int resources = 5;

	public Log(ApplicationContext applicationContext, Vector3f position) {

		super(applicationContext);

		node = (Node) applicationContext.getBean(AssetManager.class).loadModel(
				"log/log.mesh.xml");
		node.setShadowMode(ShadowMode.Cast);
		applicationContext.getBean("rootNode", Node.class).attachChild(node);
		node.setLocalTranslation(position);

		control = new RigidBodyControl(1f);
		node.addControl(control);
		control.setLinearVelocity(new Vector3f(
				FastMath.nextRandomFloat() * 10 - 5f, 0, FastMath
						.nextRandomFloat() * 10 - 5f));
		control.setAngularVelocity(new Vector3f(
				FastMath.nextRandomFloat() * 10 - 5f, FastMath
						.nextRandomFloat() * 10 - 5f, FastMath
						.nextRandomFloat() * 10 - 5f));

		applicationContext.getBean(BulletAppState.class).getPhysicsSpace()
				.add(control);

	}

	public Log(ApplicationContext applicationContext, Vector3f location,
			int chopCounter) {
		this(applicationContext, location);

		resources = chopCounter;
	}

	public int getResourceCount() {
		return resources;
	}

}