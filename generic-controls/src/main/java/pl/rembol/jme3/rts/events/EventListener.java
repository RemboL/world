package pl.rembol.jme3.rts.events;

@FunctionalInterface
public interface EventListener<T extends AbstractEvent> {

    void onEvent(T t);
}
