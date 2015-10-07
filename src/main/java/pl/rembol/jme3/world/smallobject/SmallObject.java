package pl.rembol.jme3.world.smallobject;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.springframework.context.ApplicationContext;

public abstract class SmallObject {

    protected RigidBodyControl control;
    protected Node node;
    protected ApplicationContext applicationContext;

    public SmallObject init(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    protected Vector3f getHandlePosition() {
        return Vector3f.ZERO;
    }

    public Node getNode() {
        return node;
    }

    public void detach() {
        node.getParent().detachChild(node);
    }

    public void attach(Node parent) {
        node.removeControl(control);
        node.setLocalTranslation(getHandlePosition());
        applicationContext.getBean(BulletAppState.class).getPhysicsSpace()
                .remove(control);

        parent.attachChild(node);
    }

}
