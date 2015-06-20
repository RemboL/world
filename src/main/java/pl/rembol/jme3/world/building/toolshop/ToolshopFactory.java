package pl.rembol.jme3.world.building.toolshop;

import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.resources.Cost;

public class ToolshopFactory extends BuildingFactory {

    @Override
    public Cost cost() {
        return new Cost(75, 25, 0);
    }

    @Override
    public float width() {
        return 5f;
    }

    @Override
    protected Class<? extends Building> building() {
        return Toolshop.class;
    }

}
