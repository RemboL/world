package pl.rembol.jme3.world.smallobject;

import pl.rembol.jme3.world.GameState;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Log extends SmallObject {

	public Log(Vector3f position) {

		node = (Node) GameState.getAssetManager().loadModel("log.blend")
				.clone();
		node.setShadowMode(ShadowMode.Cast);
		GameState.getRootNode().attachChild(node);
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

		GameState.getBulletAppState().getPhysicsSpace().add(control);

	}

}