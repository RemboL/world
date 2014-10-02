package pl.rembol.jme3.world.smallobject;

import pl.rembol.jme3.world.GameState;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Axe extends SmallObject {

	public Axe() {

		node = (Node) GameState.getAssetManager().loadModel("axe.blend");
		node.setShadowMode(ShadowMode.Cast);
		GameState.getRootNode().attachChild(node);

		control = new RigidBodyControl(1f);
		node.addControl(control);

		GameState.getBulletAppState().getPhysicsSpace().add(control);

	}

	@Override
	protected Vector3f getHandlePosition() {
		return new Vector3f(0, 1f, .5f);
	}
}
