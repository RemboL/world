package pl.rembol.jme3.rts.smallobjects;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import pl.rembol.jme3.rts.GameState;

public abstract class SmallObject {

    protected RigidBodyControl control;
    protected Node node;
    protected GameState gameState;

    public SmallObject(GameState gameState) {
        this.gameState = gameState;
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
        gameState.bulletAppState.getPhysicsSpace()
                .remove(control);

        parent.attachChild(node);
    }

}
