package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.building.warehouse.WarehouseFactory;

public class BuildWarehouseOrder extends BuildOrder {

    @Override
    protected BuildingFactory createBuildingFactory() {
        return new WarehouseFactory();
    }

}
