package pl.rembol.jme3.world.smallobject.tools;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.ModelHelper;
import pl.rembol.jme3.world.smallobject.SmallObject;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

public class Hammer extends SmallObject {

    public SmallObject init(ApplicationContext applicationContext) {
        super.init(applicationContext);

        node = ModelHelper.rewriteDiffuseToAmbient((Node) applicationContext
                .getBean(AssetManager.class).loadModel("hammer/hammer.blend"));
        node.setShadowMode(ShadowMode.Cast);
        applicationContext.getBean("rootNode", Node.class).attachChild(node);

        control = new RigidBodyControl(1f);
        node.addControl(control);

        applicationContext.getBean(BulletAppState.class).getPhysicsSpace()
                .add(control);

        return this;
    }

}
