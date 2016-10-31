package pl.rembol.jme3.game.events;

import java.util.List;
import java.util.Objects;

final class ListenerBinding<E extends Event> {

    final Class<E> eventClass;

    final EventListener<E> eventListener;

    public ListenerBinding(Class<E> eventClass, EventListener<E> eventListener) {
        this.eventClass = eventClass;
        this.eventListener = eventListener;
    }

    @Override
    public int hashCode() {
        return eventClass.hashCode() ^ eventListener.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ListenerBinding)) {
            return false;
        }

        ListenerBinding that = ((ListenerBinding) o);
        return eventClass.equals(that.eventClass) && eventListener.equals(that.eventListener);
    }

    boolean isApplicable(Event event) {
        return eventClass.isInstance(event);
    }
}
