package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.building.house.HouseFactory;

public class BuildHouseOrder extends BuildOrder {

    public BuildHouseOrder(GameState gameState) {
        super(gameState);
    }

    @Override
    protected BuildingFactory createBuildingFactory() {
        return new HouseFactory();
    }

}
