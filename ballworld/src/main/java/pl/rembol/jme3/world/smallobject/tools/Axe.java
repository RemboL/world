package pl.rembol.jme3.world.smallobject.tools;

import pl.rembol.jme3.rts.RtsGameState;

public class Axe extends Tool {

    public Axe(RtsGameState gameState) {
        super(gameState);
    }

    @Override
    protected String modelFileName() {
        return "axe.blend";
    }

    @Override
    public String iconName() {
        return "axe";
    }
}
