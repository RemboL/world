package pl.rembol.jme3.world.ballman.order;

import com.jme3.math.Vector2f;
import org.springframework.beans.factory.annotation.Autowired;
import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.BuildAction;
import pl.rembol.jme3.world.ballman.action.SmoothenTerrainAction;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.hud.ConsoleLog;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.terrain.Terrain;

import javax.annotation.PostConstruct;

public abstract class BuildOrder extends Order<BallMan> {

    @Autowired
    protected Terrain terrain;

    @Autowired
    protected ConsoleLog consoleLog;

    @Autowired
    protected UnitRegistry gameState;

    protected BuildingFactory factory;

    @PostConstruct
    private void initWidth() {
        factory = createBuildingFactory();
    }

    protected abstract BuildingFactory createBuildingFactory();

    @Override
    protected void doPerform(BallMan ballMan, Vector2f location) {
        if (!gameState.isSpaceFreeWithBuffer(
                terrain.getGroundPosition(location), factory.width())) {
            consoleLog.addLine("Can't build here, something's in the way");
            return;
        }
        if (hasResources(ballMan)) {

            ballMan.control().addAction(
                    applicationContext
                            .getAutowireCapableBeanFactory()
                            .createBean(SmoothenTerrainAction.class)
                            .init(location.add(new Vector2f(-factory.width(),
                                    -factory.width())),
                                    location.add(new Vector2f(factory.width(),
                                            factory.width())), 3));
            ballMan.control().addAction(
                    applicationContext.getAutowireCapableBeanFactory()
                            .createBean(BuildAction.class)
                            .init(location, factory));
        }
    }

    protected boolean hasResources(BallMan ballMan) {
        return ballMan.getOwner().hasResources(factory.cost());
    }

    @Override
    protected void doPerform(BallMan ballMan, WithNode target) {
        consoleLog.addLine("I cannot build on the " + target);
    }

    public BuildingFactory factory() {
        return factory;
    }

}