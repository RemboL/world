package pl.rembol.jme3.world.building.toolshop;

import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.resources.Cost;

import static pl.rembol.jme3.world.resources.ResourceType.STONE;
import static pl.rembol.jme3.world.resources.ResourceType.WOOD;

public class ToolshopFactory extends BuildingFactory {

    @Override
    public Cost cost() {
        return new Cost().of(WOOD, 75).and(STONE, 25);
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
