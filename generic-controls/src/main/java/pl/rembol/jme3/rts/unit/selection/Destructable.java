package pl.rembol.jme3.rts.unit.selection;

import pl.rembol.jme3.rts.unit.selection.Selectable;

public interface Destructable extends Selectable {

    void strike(int strength);

    boolean isDestroyed();

}
