package pl.rembol.jme3.world.ballman.order;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.BuildAction;
import pl.rembol.jme3.world.ballman.action.SmoothenTerrainAction;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.interfaces.WithNode;

public abstract class BuildOrder extends Order<BallMan> {

    @Autowired
    protected GameState gameState;

    @Autowired
    protected UnitRegistry unitRegistry;

    protected BuildingFactory factory;

    @PostConstruct
    private void initWidth() {
        factory = createBuildingFactory();
    }

    protected abstract BuildingFactory createBuildingFactory();

    @Override
    protected void doPerform(BallMan ballMan, Vector2f location) {
        if (!unitRegistry.isSpaceFreeWithBuffer(
                gameState.terrain.getGroundPosition(location), factory.width())) {
            gameState.consoleLog.addLine("Can't build here, something's in the way");
            return;
        }
        if (hasResources(ballMan)) {

            ballMan.control().addAction(new SmoothenTerrainAction(gameState, location.add(new Vector2f(-factory.width(),
                                    -factory.width())),
                                    location.add(new Vector2f(factory.width(),
                                            factory.width())), 3));
            ballMan.control().addAction(new BuildAction(gameState, applicationContext, location, factory));
        }
    }

    protected boolean hasResources(BallMan ballMan) {
        return ballMan.getOwner().hasResources(factory.cost());
    }

    @Override
    protected void doPerform(BallMan ballMan, WithNode target) {
        gameState.consoleLog.addLine("I cannot build on the " + target);
    }

    public BuildingFactory factory() {
        return factory;
    }

}