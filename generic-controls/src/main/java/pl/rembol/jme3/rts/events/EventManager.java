package pl.rembol.jme3.rts.events;

import pl.rembol.jme3.rts.events.selectionchanged.SelectionChangedEvent;
import pl.rembol.jme3.rts.events.selectionchanged.SelectionChangedListener;
import pl.rembol.jme3.rts.events.unitdestroyed.UnitDestroyedEvent;
import pl.rembol.jme3.rts.events.unitdestroyed.UnitDestroyedListener;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private List<UnitDestroyedListener> unitDestroyedListeners = new ArrayList<>();
    private List<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();

    public void onUnitDestroyed(UnitDestroyedListener unitDestroyedListener) {
        unitDestroyedListeners.add(unitDestroyedListener);
    }

    public void onSelectionChanged(SelectionChangedListener selectionChangedListener) {
        selectionChangedListeners.add(selectionChangedListener);
    }

    public void sendEvent(AbstractEvent event) {
        switch (event.getType()) {
            case UNIT_DESTROYED:
                unitDestroyedListeners.forEach(unitDestroyedListener -> unitDestroyedListener.onEvent((UnitDestroyedEvent) event));
                break;
            case SELECTION_CHANGED:
                selectionChangedListeners.forEach(selectionChangedListener -> selectionChangedListener.onEvent((SelectionChangedEvent) event));
                break;
        }
    }

}
