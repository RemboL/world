package pl.rembol.jme3.world.ballman.order;

import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.gameobjects.order.Order;
import pl.rembol.jme3.rts.gameobjects.order.WithSilhouette;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.BuildAction;
import pl.rembol.jme3.world.ballman.action.SmoothenTerrainAction;
import pl.rembol.jme3.world.building.BuildingFactory;

public abstract class BuildOrder extends Order<BallMan> implements WithSilhouette {

    protected BuildingFactory factory;

    public BuildOrder(GameState gameState, List<Selectable> selected) {
        super(gameState, selected);

        factory = createBuildingFactory();
    }

    protected abstract BuildingFactory createBuildingFactory();

    @Override
    protected void doPerform(BallMan ballMan, Vector2f location) {
        if (!gameState.unitRegistry.isSpaceFreeWithBuffer(
                gameState.terrain.getGroundPosition(location), factory.width())) {
            gameState.consoleLog.addLine("Can't build here, something's in the way");
            return;
        }
        if (hasResources(ballMan)) {

            ballMan.control().addAction(new SmoothenTerrainAction(gameState, location.add(new Vector2f(-factory.width(),
                    -factory.width())),
                    location.add(new Vector2f(factory.width(),
                            factory.width())), 3));
            ballMan.control().addAction(new BuildAction(gameState, location, factory));
        }
    }

    protected boolean hasResources(BallMan ballMan) {
        return ballMan.getOwner().hasResources(factory.cost());
    }

    @Override
    protected void doPerform(BallMan ballMan, WithNode target) {
        gameState.consoleLog.addLine("I cannot build on the " + target);
    }

    @Override
    public boolean isApplicableFor(Selectable unit) {
        return unit instanceof BallMan;
    }

    @Override
    public Node createNode() {
        return factory.createNodeWithScale(gameState);
    }

    @Override
    public float requiredFreeWidth() {
        return factory.width();
    }

    @Override
    public boolean snapTargetPositionToGrid() {
        return true;
    }

}