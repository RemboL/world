package pl.rembol.jme3.game.events;

import java.util.HashSet;
import java.util.Set;

public class EventManager {

    public Set<ListenerBinding<?>> listenerBindings = new HashSet<>();

    public <E extends Event> void addListener(Class<E> eventClass, EventListener<E> eventListener) {
        listenerBindings.add(new ListenerBinding<>(eventClass, eventListener));
    }

    public <E extends Event> void sendEvent(E event) {
        listenerBindings.stream()
                .filter(listenerBinding -> listenerBinding.isApplicable(event))
                .map(listenerBinding -> (EventListener<E>) listenerBinding.eventListener)
                .forEach(eventListener -> eventListener.onEvent((E) event));
    }

}
