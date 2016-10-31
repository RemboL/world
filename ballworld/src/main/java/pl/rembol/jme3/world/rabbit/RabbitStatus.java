package pl.rembol.jme3.world.rabbit;

import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gui.status.DefaultStatus;

public class RabbitStatus extends DefaultStatus {

    public RabbitStatus(RtsGameState gameState) {
        super(gameState);
    }

    @Override
    public void update() {
        updateText("Rabbit");
    }

    @Override
    protected int getTextLineNumber() {
        return 1;
    }
}
