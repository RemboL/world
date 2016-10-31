package pl.rembol.jme3.world.ballman.order;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.gameobjects.order.Order;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.rts.resources.Cost;
import pl.rembol.jme3.world.building.house.House;
import pl.rembol.jme3.world.resources.ResourceTypes;

import java.util.List;

import static pl.rembol.jme3.world.resources.ResourceTypes.WOOD;

public class RecruitOrder extends Order<House> {

    public RecruitOrder(RtsGameState gameState, List<Selectable> selectableList) {
        super(gameState, selectableList);
    }

    @Override
    protected void doPerform(House house, Vector2f location) {
        doPerform(house);
    }

    @Override
    protected void doPerform(House house, WithNode target) {
        doPerform(house);
    }

    @Override
    public boolean isApplicableFor(Selectable unit) {
        return unit instanceof House;
    }

    private void doPerform(House house) {
        if (!house.control().canAddToQueue()) {
            return;
        }
        if (!house.getOwner().availableResource(ResourceTypes.HOUSING, 1)) {
            gameState.consoleLog.addLine("Not enough housing");
            return;
        }
        if (house.getOwner().retrieveResources(
                new Cost().of(WOOD, 50))) {
            house.control().addToQueue();
        }
    }
}
