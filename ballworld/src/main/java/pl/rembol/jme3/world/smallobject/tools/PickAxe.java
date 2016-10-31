package pl.rembol.jme3.world.smallobject.tools;

import pl.rembol.jme3.rts.RtsGameState;

public class PickAxe extends Tool {

    public PickAxe(RtsGameState gameState) {
        super(gameState);
    }

    @Override
    protected String modelFileName() {
        return "pickaxe.blend";
    }

    @Override
    public String iconName() {
        return "pickaxe";
    }

}
