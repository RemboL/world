package pl.rembol.jme3.world.ballman.action;

import java.util.Optional;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.BallMan.Hand;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.resources.deposits.ResourceDeposit;
import pl.rembol.jme3.world.resources.units.ResourceUnit;

public class GatherResourcesAction extends Action {

    private ResourceDeposit resourceDeposit;

    private BallMan ballMan;

    public GatherResourcesAction init(BallMan ballMan,
            ResourceDeposit resourceDeposit) {
        this.ballMan = ballMan;
        this.resourceDeposit = resourceDeposit;

        getClosestWarehouse();

        return this;
    }

    @Override
    protected void doAct(BallMan ballMan, float tpf) {

        Optional<Warehouse> warehouse = getClosestWarehouse();

        if (ballMan.getWieldedObject(Hand.LEFT) instanceof ResourceUnit
                && warehouse.isPresent()) {
            ballMan.addActionOnStart(applicationContext
                    .getAutowireCapableBeanFactory()
                    .createBean(ReturnResourcesAction.class)
                    .init(warehouse.get()));
        } else {
            ballMan.addActionOnStart(applicationContext
                    .getAutowireCapableBeanFactory()
                    .createBean(MineResourcesAction.class).init(resourceDeposit));
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
