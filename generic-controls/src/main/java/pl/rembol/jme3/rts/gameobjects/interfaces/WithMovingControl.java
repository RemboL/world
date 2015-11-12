package pl.rembol.jme3.rts.gameobjects.interfaces;

import pl.rembol.jme3.rts.gameobjects.control.MovingControl;

public interface WithMovingControl extends WithActionQueueControl {

    default MovingControl movingControl() {
        return getNode().getControl(MovingControl.class);
    }

}
