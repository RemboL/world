package pl.rembol.jme3.world.ballman.order;

import java.util.List;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.unit.selection.Selectable;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.building.house.HouseFactory;

public class BuildHouseOrder extends BuildOrder {

    public BuildHouseOrder(GameState gameState, List<Selectable> selectableList) {
        super(gameState, selectableList);
    }

    @Override
    protected BuildingFactory createBuildingFactory() {
        return new HouseFactory();
    }

}
