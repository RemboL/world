package pl.rembol.jme3.world.interfaces;

import pl.rembol.jme3.world.controls.MovingControl;

public interface WithMovingControl extends WithNode {

    default MovingControl movingControl() {
        return getNode().getControl(MovingControl.class);
    }

}
