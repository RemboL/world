package pl.rembol.jme3.rts.unit.interfaces;

import pl.rembol.jme3.rts.unit.control.DefaultActionControl;
import pl.rembol.jme3.rts.unit.selection.Selectable;

public interface WithDefaultActionControl extends Selectable {

    default DefaultActionControl defaultActionControl() {
        return getNode().getControl(DefaultActionControl.class);
    }
}
