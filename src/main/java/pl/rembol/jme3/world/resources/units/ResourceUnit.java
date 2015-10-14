package pl.rembol.jme3.world.resources.units;

import org.springframework.context.ApplicationContext;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.resources.ResourceType;
import pl.rembol.jme3.world.smallobject.SmallObject;

public abstract class ResourceUnit extends SmallObject {

    private int resources = 5;

    public SmallObject init(ApplicationContext applicationContext,
            Vector3f location, int chopCounter) {
        super.init(applicationContext);

        node = (Node) applicationContext.getBean(GameState.class).assetManager.loadModel(
                getModelFileName());
        node.setShadowMode(ShadowMode.Cast);
        node.setLocalTranslation(location);

        control = new RigidBodyControl(1f);
        node.addControl(control);

        applicationContext.getBean(BulletAppState.class).getPhysicsSpace()
                .add(control);

        resources = chopCounter;

        return this;
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