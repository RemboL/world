package pl.rembol.jme3.world.ballman.order;

import static pl.rembol.jme3.world.resources.ResourceType.WOOD;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.building.house.House;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.resources.Cost;

public class RecruitOrder extends Order<House> {

    @Override
    protected void doPerform(House house, Vector2f location) {
        doPerform(house);
    }

    @Override
    protected void doPerform(House house, WithNode target) {
        doPerform(house);
    }

    private void doPerform(House house) {
        if (!house.control().canAddToQueue()) {
            return;
        }
        if (!house.getOwner().availableHousing(1)) {
            applicationContext.getBean(GameState.class).consoleLog.addLine("Not enough housing");
            return;
        }
        if (house.getOwner().retrieveResources(
                new Cost().of(WOOD, 50))) {
            house.control().addToQueue();
        }
    }
}
