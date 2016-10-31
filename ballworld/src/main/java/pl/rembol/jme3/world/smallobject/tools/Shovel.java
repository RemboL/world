package pl.rembol.jme3.world.smallobject.tools;

import pl.rembol.jme3.rts.RtsGameState;

public class Shovel extends Tool {

    public Shovel(RtsGameState gameState) {
        super(gameState);
    }

    @Override
    protected String modelFileName() {
        return "shovel/shovel.mesh.xml";
    }

    @Override
    public String iconName() {
        return "shovel";
    }

}
