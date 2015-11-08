package pl.rembol.jme3.world.ballman.order;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.unit.order.Order;
import pl.rembol.jme3.rts.unit.selection.Selectable;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.BuildAction;
import pl.rembol.jme3.world.ballman.action.SmoothenTerrainAction;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.rts.unit.interfaces.WithNode;

public abstract class BuildOrder extends Order<BallMan> {

    protected BuildingFactory factory;

    public BuildOrder(GameState gameState) {
        super(gameState);

        factory = createBuildingFactory();
    }

    protected abstract BuildingFactory createBuildingFactory();

    @Override
    protected void doPerform(BallMan ballMan, Vector2f location) {
        // TODO FIXME
        if (!pl.rembol.jme3.world.GameState.class.cast(gameState).unitRegistry.isSpaceFreeWithBuffer(
                gameState.terrain.getGroundPosition(location), factory.width())) {
            gameState.consoleLog.addLine("Can't build here, something's in the way");
            return;
        }
        if (hasResources(ballMan)) {

            ballMan.control().addAction(new SmoothenTerrainAction(gameState, location.add(new Vector2f(-factory.width(),
                    -factory.width())),
                    location.add(new Vector2f(factory.width(),
                            factory.width())), 3));
            // TODO FIXME
            ballMan.control().addAction(new BuildAction(pl.rembol.jme3.world.GameState.class.cast(gameState), location, factory));
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

    public BuildingFactory factory() {
        return factory;
    }

}