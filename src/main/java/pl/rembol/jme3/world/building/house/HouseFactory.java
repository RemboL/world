package pl.rembol.jme3.world.building.house;

import static pl.rembol.jme3.world.resources.ResourceType.WOOD;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.resources.Cost;

public class HouseFactory extends BuildingFactory {

    @Override
    public Cost cost() {
        return new Cost().of(WOOD, 100);
    }

    @Override
    public float width() {
        return 5f;
    }

    @Override
    protected Class<? extends Building> building() {
        return House.class;
    }

}
