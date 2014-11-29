package pl.rembol.jme3.world.smallobject;

import pl.rembol.jme3.world.GameState;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Shovel extends SmallObject {

	public Shovel() {

		node = (Node) GameState
				.get().getAssetManager().loadModel("shovel/shovel.mesh.xml");
		node.setShadowMode(ShadowMode.Cast);
		GameState.get().getRootNode().attachChild(node);

		control = new RigidBodyControl(1f);
		node.addControl(control);

		GameState.get().getBulletAppState().getPhysicsSpace().add(control);

	}

}
