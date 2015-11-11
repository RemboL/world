package pl.rembol.jme3.rts.events.selectionchanged;

import pl.rembol.jme3.rts.events.AbstractEvent;
import pl.rembol.jme3.rts.events.EventType;
import pl.rembol.jme3.rts.unit.selection.Selectable;

import java.util.List;

public class SelectionChangedEvent extends AbstractEvent {

    private final List<Selectable> selectableList;

    public SelectionChangedEvent(List<Selectable> selectableList) {
        this.selectableList = selectableList;
    }

    public List<Selectable> getSelectableList() {
        return selectableList;
    }

    @Override
    public EventType getType() {
        return EventType.SELECTION_CHANGED;
    }

}
