package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.rts.unit.action.Action;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.resources.Cost;
import pl.rembol.jme3.world.resources.ResourceTypes;

import java.util.Optional;

public class EatFoodAction extends Action<BallMan> {

    private static final float REQUIRED_DISTANCE = 3;

    private boolean finished = false;

    int howHungry;

    public EatFoodAction(GameState gameState, float howHungry) {
        super(gameState);
        this.howHungry = (int) howHungry;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        Optional<Warehouse> closestWarehouse = ballMan.getOwner()
                .getClosestWarehouse(ballMan.getLocation());

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
