package pl.rembol.jme3.world.building.house;

import com.jme3.math.Vector2f;
import com.jme3.ui.Picture;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.hud.Clickable;
import pl.rembol.jme3.world.resources.ResourceType;

public class RecruitQueueIcon extends Picture implements Clickable {

    private final RecruitQueuedAction action;

    public RecruitQueueIcon(GameState gameState, RecruitQueuedAction action) {
        super("recruit action icon");
        this.action = action;

        setImage(gameState.assetManager, "interface/icons/ballman.png", true);
        setWidth(32);
        setHeight(32);
    }

    @Override
    public void onClick() {
        action.cancel();
        action.getControl().getHouse().getOwner().addResource(ResourceType.WOOD, 50);
    }

    @Override
    public boolean isClicked(Vector2f cursorPosition) {
        return getWorldTranslation().x <= cursorPosition.x &&
                getWorldTranslation().x + getWorldScale().x >= cursorPosition.x &&
                getWorldTranslation().y <= cursorPosition.y &&
                getWorldTranslation().y + getWorldScale().y >= cursorPosition.y;

    }
}
