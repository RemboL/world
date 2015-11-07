package pl.rembol.jme3.rts.unit.interfaces;

import pl.rembol.jme3.rts.unit.control.MovingControl;

public interface WithMovingControl extends WithActionQueueControl {

    default MovingControl movingControl() {
        return getNode().getControl(MovingControl.class);
    }

}
