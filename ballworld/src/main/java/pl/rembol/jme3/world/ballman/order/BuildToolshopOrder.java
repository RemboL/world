package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.building.toolshop.ToolshopFactory;

public class BuildToolshopOrder extends BuildOrder {

    public BuildToolshopOrder(GameState gameState) {
        super(gameState);
    }

    @Override
    protected BuildingFactory createBuildingFactory() {
        return new ToolshopFactory();
    }

}
