package pl.rembol.jme3.rts.gameobjects.selection;

public interface Destructable extends Selectable {

    void strike(int strength);

    boolean isDestroyed();

}
