package pl.rembol.jme3.rts.events;

import pl.rembol.jme3.game.events.Event;
import pl.rembol.jme3.rts.gameobjects.selection.Selectable;

import java.util.List;

public class SelectionChangedEvent extends Event {

    private final List<Selectable> selectableList;

    public SelectionChangedEvent(List<Selectable> selectableList) {
        this.selectableList = selectableList;
    }

    public List<Selectable> getSelectableList() {
        return selectableList;
    }
}
