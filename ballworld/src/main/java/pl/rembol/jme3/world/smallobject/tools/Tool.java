package pl.rembol.jme3.world.smallobject.tools;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ModelHelper;
import pl.rembol.jme3.world.hud.InventoryIcon;
import pl.rembol.jme3.world.smallobject.SmallObject;

public abstract class Tool extends SmallObject {

    private InventoryIcon icon;

    public Tool(GameState gameState) {
        super(gameState);

        node = ModelHelper.rewriteDiffuseToAmbient((Node) gameState.assetManager.loadModel(modelFileName()));
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
