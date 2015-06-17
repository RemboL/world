package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.BallMan.Hand;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.resources.units.ResourceUnit;

public class ReturnResourcesAction extends Action {

    private float targetDistance = 3;
    private Warehouse warehouse;

    public ReturnResourcesAction init(Warehouse target) {
        this.warehouse = target;

        return this;
    }

    @Override
    protected void doAct(BallMan ballMan, float tpf) {
        assertDistance(ballMan, warehouse, targetDistance);

        if (isCloseEnough(ballMan, warehouse, targetDistance)) {
            if (ballMan.getWieldedObject(Hand.LEFT) instanceof ResourceUnit) {
                warehouse.returnResource(ballMan);
            }
        }
    }

    @Override
    public boolean isFinished(BallMan ballMan) {
        if (ballMan.getWieldedObject(Hand.LEFT) instanceof ResourceUnit) {
            return false;
        }

        return true;
    }

}
