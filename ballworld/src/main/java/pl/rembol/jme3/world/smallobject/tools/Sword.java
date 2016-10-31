package pl.rembol.jme3.world.smallobject.tools;

import pl.rembol.jme3.rts.RtsGameState;

public class Sword extends Tool {

    public Sword(RtsGameState gameState) {
        super(gameState);
    }

    @Override
    protected String modelFileName() {
        return "sword/sword.scene";
    }

    @Override
    public String iconName() {
        return "sword";
    }

}
