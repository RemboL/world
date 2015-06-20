package pl.rembol.jme3.world.building;

import org.springframework.context.ApplicationContext;

import pl.rembol.jme3.world.resources.Cost;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

public abstract class BuildingFactory {

    public Building create(ApplicationContext applicationContext,
            Vector2f position, boolean startUnderground) {
        return create(applicationContext).init(position, startUnderground);
    }

    public Building create(ApplicationContext applicationContext) {
        return applicationContext.getAutowireCapableBeanFactory().createBean(
                building());
    }

    protected abstract Class<? extends Building> building();

    public abstract Cost cost();

    public abstract float width();

    public Node createNodeWithScale(ApplicationContext applicationContext) {
        return create(applicationContext).initNodeWithScale();
    }
}
