package pl.rembol.jme3.world.smallobject;

import pl.rembol.jme3.world.GameRunningAppState;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public abstract class SmallObject {

	protected RigidBodyControl control;
	protected Node node;
	protected GameRunningAppState appState;

	public SmallObject(GameRunningAppState appState) {
		this.appState = appState;
	}

	protected Vector3f getHandlePosition() {
		return Vector3f.ZERO;
	}

	public Node getNode() {
		return node;
	}

	public void detach() {
		Vector3f itemPosition = node.getWorldTranslation();
		node.getParent().detachChild(node);
		appState.getRootNode().attachChild(node);
		node.setLocalTranslation(itemPosition);
		node.addControl(control);
		appState.getBulletAppState().getPhysicsSpace().add(control);
	}

	public void attach(Node parent) {
		node.removeControl(control);
		node.setLocalTranslation(getHandlePosition());
		appState.getBulletAppState().getPhysicsSpace().remove(control);

		parent.attachChild(node);
	}

}
