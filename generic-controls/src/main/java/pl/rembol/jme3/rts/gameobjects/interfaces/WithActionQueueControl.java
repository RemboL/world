package pl.rembol.jme3.rts.gameobjects.interfaces;

import pl.rembol.jme3.rts.gameobjects.control.ActionQueueControl;

public interface WithActionQueueControl extends WithNode {

    default <T extends WithNode> ActionQueueControl<T> actionQueueControl() {
        return getNode().getControl(ActionQueueControl.class);
    }
}
