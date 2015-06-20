package pl.rembol.jme3.world.building.house;

import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.resources.Cost;

public class HouseFactory extends BuildingFactory {

    @Override
    public Cost cost() {
        return new Cost(100, 0, 0);
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
