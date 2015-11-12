package pl.rembol.jme3.rts.gameobjects.interfaces;

import pl.rembol.jme3.rts.gameobjects.control.DefaultActionControl;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;

public interface WithDefaultActionControl extends Selectable {

    default DefaultActionControl defaultActionControl() {
        return getNode().getControl(DefaultActionControl.class);
    }
}
