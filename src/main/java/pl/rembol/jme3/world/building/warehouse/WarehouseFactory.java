package pl.rembol.jme3.world.building.warehouse;

import static pl.rembol.jme3.world.resources.ResourceType.STONE;
import static pl.rembol.jme3.world.resources.ResourceType.WOOD;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.BuildingFactory;
import pl.rembol.jme3.world.resources.Cost;

public class WarehouseFactory extends BuildingFactory {

    @Override
    public Cost cost() {
        return new Cost().of(WOOD, 50).and(STONE, 25);
    }

    @Override
    public float width() {
        return 5f;
    }

    @Override
    protected Class<? extends Building> building() {
        return Warehouse.class;
    }

}
