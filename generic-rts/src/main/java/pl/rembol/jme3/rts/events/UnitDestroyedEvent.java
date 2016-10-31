package pl.rembol.jme3.rts.events;

import pl.rembol.jme3.game.events.Event;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;

public class UnitDestroyedEvent extends Event {

    private Selectable unit;

    public UnitDestroyedEvent(Selectable unit) {
        this.unit = unit;
    }

    public Selectable getUnit() {
        return unit;
    }
}
