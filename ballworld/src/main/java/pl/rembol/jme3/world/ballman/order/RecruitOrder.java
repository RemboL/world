package pl.rembol.jme3.world.ballman.order;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.building.house.House;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.resources.Cost;

import static pl.rembol.jme3.world.resources.ResourceType.WOOD;

public class RecruitOrder extends Order<House> {

    public RecruitOrder(GameState gameState) {
        super(gameState);
    }

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
            gameState.consoleLog.addLine("Not enough housing");
            return;
        }
        if (house.getOwner().retrieveResources(
                new Cost().of(WOOD, 50))) {
            house.control().addToQueue();
        }
    }
}
