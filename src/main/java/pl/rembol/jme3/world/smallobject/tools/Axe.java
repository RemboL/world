package pl.rembol.jme3.world.smallobject.tools;

import pl.rembol.jme3.world.GameState;

public class Axe extends Tool {

    public Axe(GameState gameState) {
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
