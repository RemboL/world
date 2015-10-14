package pl.rembol.jme3.world.rabbit;

import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.hud.status.DefaultStatus;

public class RabbitStatus extends DefaultStatus {

    public RabbitStatus(GameState gameState) {
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
