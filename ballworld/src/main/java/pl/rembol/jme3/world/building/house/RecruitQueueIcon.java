package pl.rembol.jme3.world.building.house;

import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.hud.ClickablePicture;
import pl.rembol.jme3.world.resources.ResourceType;

public class RecruitQueueIcon extends ClickablePicture {

    private final RecruitQueuedAction action;

    public RecruitQueueIcon(GameState gameState, RecruitQueuedAction action) {
        super(gameState, "ballman");
        this.action = action;
    }

    @Override
    public void onClick() {
        action.cancel();
        action.getControl().getHouse().getOwner().addResource(ResourceType.WOOD, 50);
    }

}
