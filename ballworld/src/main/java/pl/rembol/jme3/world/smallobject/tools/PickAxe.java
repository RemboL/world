package pl.rembol.jme3.world.smallobject.tools;

import pl.rembol.jme3.rts.GameState;

public class PickAxe extends Tool {

    public PickAxe(GameState gameState) {
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
