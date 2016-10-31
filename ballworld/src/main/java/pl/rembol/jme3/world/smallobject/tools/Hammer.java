package pl.rembol.jme3.world.smallobject.tools;

import pl.rembol.jme3.rts.RtsGameState;

public class Hammer extends Tool {

    public Hammer(RtsGameState gameState) {
        super(gameState);
    }

    @Override
    protected String modelFileName() {
        return "hammer/hammer.blend";
    }

    @Override
    public String iconName() {
        return "hammer";
    }

}
