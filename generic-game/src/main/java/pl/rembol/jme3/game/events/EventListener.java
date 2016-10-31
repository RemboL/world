package pl.rembol.jme3.game.events;

@FunctionalInterface
public interface EventListener<E extends Event> {

    void onEvent(E t);
}
