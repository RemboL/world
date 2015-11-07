package pl.rembol.jme3.rts.unit.interfaces;

import pl.rembol.jme3.rts.unit.control.ActionQueueControl;

public interface WithActionQueueControl extends WithNode {

    default ActionQueueControl actionQueueControl() {
        return getNode().getControl(ActionQueueControl.class);
    }
}
