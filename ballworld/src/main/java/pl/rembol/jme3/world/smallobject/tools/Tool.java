package pl.rembol.jme3.world.smallobject.tools;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gui.InventoryIcon;
import pl.rembol.jme3.rts.smallobjects.SmallObject;

public abstract class Tool extends SmallObject {

    private InventoryIcon icon;

    public Tool(RtsGameState gameState) {
        super(gameState);

        node = (Node) gameState.assetManager.loadModel(modelFileName());
        node.setShadowMode(ShadowMode.Cast);

        control = new RigidBodyControl(1f);
        node.addControl(control);

        gameState.bulletAppState.getPhysicsSpace()
                .add(control);

        icon = new InventoryIcon(gameState, iconName());
    }

    protected abstract String modelFileName();

    public abstract String iconName();

    public InventoryIcon icon() {
        return icon;
    }

}
