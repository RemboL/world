package pl.rembol.jme3.rts.resources.units;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.resources.ResourceType;
import pl.rembol.jme3.rts.smallobjects.SmallObject;

public abstract class ResourceUnit extends SmallObject {

    private int resources = 5;

    public ResourceUnit(GameState gameState,
                        Vector3f location, int chopCounter) {
        super(gameState);

        node = (Node) gameState.assetManager.loadModel(
                getModelFileName());
        node.setShadowMode(ShadowMode.Cast);
        node.setLocalTranslation(location);

        control = new RigidBodyControl(1f);
        node.addControl(control);

        gameState.bulletAppState.getPhysicsSpace()
                .add(control);

        resources = chopCounter;
    }

    public void increaseCount(int count) {
        resources += count;
    }

    public int getResourceCount() {
        return resources;
    }

    public abstract ResourceType getResourceType();

    protected abstract String getModelFileName();

}