package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.warehouse.Warehouse;

public class BuildWarehouseAction extends BuildAction {

    @Override
    public Building createBuilding() {
        return applicationContext.getAutowireCapableBeanFactory().createBean(
                Warehouse.class);
    }

    @Override
    protected boolean retrieveResources(BallMan ballMan) {
        return ballMan.getOwner().retrieveResources(150, 50, 0);
    }

}
