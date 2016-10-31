package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.building.warehouse.WarehouseFactory;

import java.util.List;

public class BuildWarehouseOrder extends BuildOrder {

    public BuildWarehouseOrder(RtsGameState gameState, List<Selectable> selectableList) {
        super(gameState, selectableList);
    }

    @Override
    protected BuildingFactory createBuildingFactory() {
        return new WarehouseFactory();
    }

}
