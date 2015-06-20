package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.building.toolshop.ToolshopFactory;

public class BuildToolshopOrder extends BuildOrder {

    @Override
    protected BuildingFactory createBuildingFactory() {
        return new ToolshopFactory();
    }

}
