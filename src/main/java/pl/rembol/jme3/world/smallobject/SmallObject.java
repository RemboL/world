package pl.rembol.jme3.world.smallobject;

import pl.rembol.jme3.world.GameState;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public abstract class SmallObject {

	protected RigidBodyControl control;
	protected Node node;

	protected Vector3f getHandlePosition() {
		return Vector3f.ZERO;
	}
	
	public Node getNode() {
		return node;
	}

	public void detach() {
		Vector3f itemPosition = node.getWorldTranslation();
		node.getParent().detachChild(node);
		GameState.get().getRootNode().attachChild(node);
		node.setLocalTranslation(itemPosition);
		node.addControl(control);
		GameState.get().getBulletAppState().getPhysicsSpace().add(control);
	}

	public void attach(Node parent) {
		node.removeControl(control);
		node.setLocalTranslation(getHandlePosition());
		GameState.get().getBulletAppState().getPhysicsSpace().remove(control);

		parent.attachChild(node);
	}

}
