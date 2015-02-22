package pl.rembol.jme3.world.smallobject;

import org.springframework.context.ApplicationContext;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Shovel extends SmallObject {

	public Shovel(ApplicationContext applicationContext) {

		super(applicationContext);

		node = (Node) applicationContext.getBean(AssetManager.class).loadModel(
				"shovel/shovel.mesh.xml");
		node.setShadowMode(ShadowMode.Cast);
		applicationContext.getBean("rootNode", Node.class).attachChild(node);

		control = new RigidBodyControl(1f);
		node.addControl(control);

		applicationContext.getBean(BulletAppState.class).getPhysicsSpace()
				.add(control);

	}

}
