package pl.rembol.jme3.world.smallobject.tools;

import org.springframework.context.ApplicationContext;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ModelHelper;
import pl.rembol.jme3.world.hud.InventoryIcon;
import pl.rembol.jme3.world.smallobject.SmallObject;

public abstract class Tool extends SmallObject {

    private InventoryIcon icon;

    public Tool init(ApplicationContext applicationContext) {
        super.init(applicationContext);

        node = ModelHelper.rewriteDiffuseToAmbient((Node) applicationContext
                .getBean(GameState.class).assetManager.loadModel(modelFileName()));
        node.setShadowMode(ShadowMode.Cast);

        control = new RigidBodyControl(1f);
        node.addControl(control);

        applicationContext.getBean(BulletAppState.class).getPhysicsSpace()
                .add(control);

        icon = new InventoryIcon(iconName(), applicationContext.getBean(GameState.class));

        return this;
    }

    protected abstract String modelFileName();
    
    public abstract String iconName();

    public InventoryIcon icon() {
        return icon;
    }

}
