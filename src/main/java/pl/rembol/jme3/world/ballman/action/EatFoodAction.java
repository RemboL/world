package pl.rembol.jme3.world.ballman.action;

import java.util.Optional;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.hud.ConsoleLog;
import pl.rembol.jme3.world.resources.Cost;
import pl.rembol.jme3.world.resources.ResourceType;

public class EatFoodAction extends Action<BallMan> {

    private static final float REQUIRED_DISTANCE = 3;

    private boolean finished = false;

    int howHungry;

    public EatFoodAction init(float howHungry) {
        this.howHungry = (int) howHungry;
        return this;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        Optional<Warehouse> closestWarehouse = ballMan.getOwner()
                .getClosestWarehouse(ballMan.getLocation());

        if (!closestWarehouse.isPresent()) {
            if (ballMan.getOwner().isActive()) {
                applicationContext.getBean(ConsoleLog.class).addLineExternal(
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

        if (ballMan.getOwner().getResource(ResourceType.FOOD) > 0) {
            int foodToEat = Math.min(
                    ballMan.getOwner().getResource(ResourceType.FOOD),
                    howHungry);

            if (ballMan.getOwner().retrieveResources(
                    new Cost().of(ResourceType.FOOD, foodToEat))) {
                ballMan.hunger().eat(foodToEat);
            }
        }

        finished = true;
    }

    public boolean isFinished(BallMan ballMan) {
        return finished;
    }

}
