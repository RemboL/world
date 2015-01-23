package pl.rembol.jme3.world.smallobject;

import pl.rembol.jme3.world.BlenderLoaderHelper;
import pl.rembol.jme3.world.GameRunningAppState;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Hammer extends SmallObject {

	public Hammer(GameRunningAppState appState) {

		super(appState);

		node = BlenderLoaderHelper.rewriteDiffuseToAmbient((Node) appState
				.getAssetManager().loadModel("hammer/hammer.blend"));
		node.setShadowMode(ShadowMode.Cast);
		appState.getRootNode().attachChild(node);

		control = new RigidBodyControl(1f);
		node.addControl(control);

		appState.getBulletAppState().getPhysicsSpace().add(control);

	}

}
