package pl.rembol.jme3.world.building;

import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.rts.gui.status.DefaultStatus;

public class BuildingStatus extends DefaultStatus {

    private final Building building;

    public BuildingStatus(Building building, GameState gameState) {
        super(gameState);

        this.building = building;
    }

    @Override
    public void update() {

        if (building.isConstructed()) {
            updateText(building.statusLines());
        } else {
            updateText(building.getName(),
                    "Under construction",
                    "owner: " + building.getOwner().getName());
        }

    }

    @Override
    protected int getTextLineNumber() {
        return 3;
    }
}
