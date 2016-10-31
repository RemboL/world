package pl.rembol.jme3.world.ballman;

import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;
import pl.rembol.jme3.rts.gameobjects.selection.SelectionIcon;
import pl.rembol.jme3.world.building.house.House;

import java.util.Optional;

public class BallManIcon extends SelectionIcon {
    public BallManIcon(RtsGameState gameState, Selectable selectable) {
        super(gameState, selectable, "ballman");
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

        super.onClick();
    }
}
