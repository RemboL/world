package pl.rembol.jme3.world.smallobject;

import pl.rembol.jme3.world.GameRunningAppState;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Log extends SmallObject {

	private int resources = 5;

	public Log(Vector3f position, GameRunningAppState appState) {

		super(appState);

		node = (Node) appState.getAssetManager().loadModel("log/log.mesh.xml");
		node.setShadowMode(ShadowMode.Cast);
		appState.getRootNode().attachChild(node);
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

		appState.getBulletAppState().getPhysicsSpace().add(control);

	}

	public Log(Vector3f location, GameRunningAppState appState, int chopCounter) {
		this(location, appState);

		resources = chopCounter;
	}

	public int getResourceCount() {
		return resources;
	}

}