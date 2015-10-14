package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.building.warehouse.WarehouseFactory;

public class BuildWarehouseOrder extends BuildOrder {

    public BuildWarehouseOrder(GameState gameState) {
        super(gameState);
    }

    @Override
    protected BuildingFactory createBuildingFactory() {
        return new WarehouseFactory();
    }

}
