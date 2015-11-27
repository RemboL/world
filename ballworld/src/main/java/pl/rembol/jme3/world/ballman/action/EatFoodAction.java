package pl.rembol.jme3.world.ballman.action;

import java.util.Optional;

import pl.rembol.jme3.rts.gameobjects.action.Action;
import pl.rembol.jme3.rts.resources.Cost;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballmanunitregistry.BallManUnitRegistry;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.resources.ResourceTypes;

public class EatFoodAction extends Action<BallMan> {

    private static final float REQUIRED_DISTANCE = 3;
    private final BallManUnitRegistry ballManUnitRegistry;

    private boolean finished = false;

    int howHungry;

    public EatFoodAction(GameState gameState, float howHungry) {
        super(gameState);
        this.ballManUnitRegistry = new BallManUnitRegistry(gameState);
        this.howHungry = (int) howHungry;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        Optional<Warehouse> closestWarehouse = ballManUnitRegistry.getClosestWarehouse(ballMan.getLocation(), ballMan.getOwner());

        if (!closestWarehouse.isPresent()) {
            if (ballMan.getOwner().isActive()) {
                gameState.consoleLog.addLineExternal(
                        "Could not find a Warehouse");
            }
            cancel();
            return false;
        }

        if (!assertDistance(ballMan, closestWarehouse.get(), REQUIRED_DISTANCE)) {
            return false;
        }

        return true;

    }

    @Override
    protected void doAct(BallMan ballMan, float tpf) {

        if (ballMan.getOwner().getResource(ResourceTypes.FOOD) > 0) {
            int foodToEat = Math.min(
                    ballMan.getOwner().getResource(ResourceTypes.FOOD),
                    howHungry);

            if (ballMan.getOwner().retrieveResources(
                    new Cost().of(ResourceTypes.FOOD, foodToEat))) {
                ballMan.hunger().eat(foodToEat);
            }
        }

        finished = true;
    }

    public boolean isFinished(BallMan ballMan) {
        return finished;
    }

}
