package pl.rembol.jme3.world.ballman.order;

import java.util.List;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.building.warehouse.WarehouseFactory;

public class BuildWarehouseOrder extends BuildOrder {

    public BuildWarehouseOrder(GameState gameState, List<Selectable> selectableList) {
        super(gameState, selectableList);
    }

    @Override
    protected BuildingFactory createBuildingFactory() {
        return new WarehouseFactory();
    }

}
