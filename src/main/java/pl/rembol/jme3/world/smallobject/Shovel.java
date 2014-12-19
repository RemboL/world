package pl.rembol.jme3.world.smallobject;

import pl.rembol.jme3.world.GameRunningAppState;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Shovel extends SmallObject {

	public Shovel(GameRunningAppState appState) {

		super(appState);

		node = (Node) appState.getAssetManager().loadModel(
				"shovel/shovel.mesh.xml");
		node.setShadowMode(ShadowMode.Cast);
		appState.getRootNode().attachChild(node);

		control = new RigidBodyControl(1f);
		node.addControl(control);

		appState.getBulletAppState().getPhysicsSpace().add(control);

	}

}
