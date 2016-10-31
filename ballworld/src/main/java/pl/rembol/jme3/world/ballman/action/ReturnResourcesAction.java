package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.action.Action;
import pl.rembol.jme3.rts.resources.units.ResourceUnit;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.BallMan.Hand;
import pl.rembol.jme3.world.building.warehouse.Warehouse;

public class ReturnResourcesAction extends Action<BallMan> {

    private float targetDistance = 3;
    private Warehouse warehouse;

    public ReturnResourcesAction(RtsGameState gameState, Warehouse target) {
        super(gameState);
        this.warehouse = target;
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
