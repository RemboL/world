package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.BallMan.Hand;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.resources.deposits.ResourceDeposit;
import pl.rembol.jme3.world.resources.units.ResourceUnit;

import java.util.Optional;

public class GatherResourcesAction extends Action<BallMan> {

    private ResourceDeposit resourceDeposit;

    private BallMan ballMan;

    public GatherResourcesAction(GameState gameState, BallMan ballMan, ResourceDeposit resourceDeposit) {
        super(gameState);
        this.ballMan = ballMan;
        this.resourceDeposit = resourceDeposit;

        getClosestWarehouse();
    }

    @Override
    protected void doAct(BallMan ballMan, float tpf) {

        Optional<Warehouse> warehouse = getClosestWarehouse();

        if (ballMan.getWieldedObject(Hand.LEFT) instanceof ResourceUnit
                && warehouse.isPresent()) {
            ballMan.control().addActionOnStart(
                    new ReturnResourcesAction(gameState, warehouse.get()).withParent(this));
        } else {
            ballMan.control().addActionOnStart(
                    new MineResourcesAction(gameState, resourceDeposit).withParent(this));
        }
    }

    public boolean isFinished(BallMan ballMan) {
        return (getClosestWarehouse() == null) || resourceDeposit.isDestroyed();
    }

    private Optional<Warehouse> getClosestWarehouse() {

        return ballMan.getOwner().getClosestWarehouse(
                resourceDeposit.getLocation());
    }

}
