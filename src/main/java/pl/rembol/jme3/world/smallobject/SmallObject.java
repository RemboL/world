package pl.rembol.jme3.world.smallobject;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.controls.TimeToLiveControl;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public abstract class SmallObject {

	protected RigidBodyControl control;
	protected Node node;
	protected ApplicationContext applicationContext;

	public SmallObject(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected Vector3f getHandlePosition() {
		return Vector3f.ZERO;
	}

	public Node getNode() {
		return node;
	}

	public void detach(int timeToLive) {
		Vector3f itemPosition = node.getWorldTranslation();
		node.getParent().detachChild(node);
		applicationContext.getBean("rootNode", Node.class).attachChild(node);
		node.setLocalTranslation(itemPosition);
		node.addControl(control);
		applicationContext.getBean(BulletAppState.class).getPhysicsSpace().add(control);
		node.addControl(new TimeToLiveControl(applicationContext, timeToLive));
	}

	public void detach() {
		detach(1);
	}

	public void attach(Node parent) {
		node.removeControl(control);
		node.setLocalTranslation(getHandlePosition());
		applicationContext.getBean(BulletAppState.class).getPhysicsSpace().remove(control);

		parent.attachChild(node);
	}

}
