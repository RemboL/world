package pl.rembol.jme3.world.building.house;

import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gui.ClickablePicture;
import pl.rembol.jme3.world.resources.ResourceTypes;

public class RecruitQueueIcon extends ClickablePicture {

    private final RecruitQueuedAction action;

    public RecruitQueueIcon(RtsGameState gameState, RecruitQueuedAction action) {
        super(gameState);
        picture.setHeight(32);
        picture.setWidth(32);
        picture.setName("ballman");
        picture.setImage(gameState.assetManager, "interface/icons/ballman.png", true);

        this.action = action;
    }

    @Override
    public void onClick() {
        action.cancel();
        action.getControl().getHouse().getOwner().addResource(ResourceTypes.WOOD, 50);
    }

}
