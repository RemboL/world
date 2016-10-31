package pl.rembol.jme3.world.building.warehouse;

import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.resources.Cost;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.BuildingFactory;

import static pl.rembol.jme3.world.resources.ResourceTypes.STONE;
import static pl.rembol.jme3.world.resources.ResourceTypes.WOOD;

public class WarehouseFactory extends BuildingFactory {

    @Override
    public Building create(RtsGameState gameState) {
        return new Warehouse(gameState);
    }

    @Override
    public Cost cost() {
        return new Cost().of(WOOD, 50).and(STONE, 25);
    }

    @Override
    public float width() {
        return 5f;
    }
}
