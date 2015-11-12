package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.building.toolshop.ToolshopFactory;

import java.util.List;

public class BuildToolshopOrder extends BuildOrder {

    public BuildToolshopOrder(GameState gameState, List<Selectable> selectableList) {
        super(gameState, selectableList);
    }

    @Override
    protected BuildingFactory createBuildingFactory() {
        return new ToolshopFactory();
    }

}
