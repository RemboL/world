package pl.rembol.jme3.world.selection;

import java.util.Optional;

import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.house.House;
import pl.rembol.jme3.world.hud.ClickablePicture;

public class SelectionIcon extends ClickablePicture {

    private Selectable selectable;

    public SelectionIcon(GameState gameState, Selectable selectable, String iconName) {
        super(gameState, iconName);
        this.selectable = selectable;
        setImage(gameState.assetManager, "interface/icons/" + iconName + ".png", true);
    }

    @Override
    public void onClick() {

        if (selectable instanceof BallMan) {
            Optional<House> house = ((BallMan) selectable).isInside();
            if (house.isPresent()) {
                house.get().exit((BallMan) selectable);
                return;
            }
        }
        gameState.selectionManager.select(selectable);
    }
}