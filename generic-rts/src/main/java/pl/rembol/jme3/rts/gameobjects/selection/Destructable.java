package pl.rembol.jme3.rts.gameobjects.selection;

import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;

public interface Destructable extends WithNode {

    void strike(int strength);

    boolean isDestroyed();

}
