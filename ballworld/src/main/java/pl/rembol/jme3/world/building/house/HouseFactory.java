package pl.rembol.jme3.world.building.house;

import static pl.rembol.jme3.world.resources.ResourceTypes.WOOD;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.resources.Cost;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.building.BuildingFactory;

public class HouseFactory extends BuildingFactory {

    @Override
    public Building create(GameState gameState) {
        return new House(gameState);
    }

    @Override
    public Cost cost() {
        return new Cost().of(WOOD, 100);
    }

    @Override
    public float width() {
        return 5f;
    }

}
