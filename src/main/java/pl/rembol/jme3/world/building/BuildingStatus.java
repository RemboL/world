package pl.rembol.jme3.world.building;

import org.springframework.context.ApplicationContext;
import pl.rembol.jme3.world.hud.status.DefaultStatus;

public class BuildingStatus extends DefaultStatus {

    private final Building building;

    public BuildingStatus(Building building, ApplicationContext applicationContext) {
        super(applicationContext);

        this.building = building;

        update();
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
