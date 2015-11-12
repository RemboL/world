package pl.rembol.jme3.rts.events.unitdestroyed;

import pl.rembol.jme3.rts.events.AbstractEvent;
import pl.rembol.jme3.rts.events.EventType;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;

public class UnitDestroyedEvent extends AbstractEvent {

    private Selectable unit;

    public UnitDestroyedEvent(Selectable unit) {
        this.unit = unit;
    }

    public Selectable getUnit() {
        return unit;
    }

    @Override
    public EventType getType() {
        return EventType.UNIT_DESTROYED;
    }
}
