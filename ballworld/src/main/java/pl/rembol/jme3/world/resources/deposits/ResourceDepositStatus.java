package pl.rembol.jme3.world.resources.deposits;

import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gui.status.DefaultStatus;

public class ResourceDepositStatus extends DefaultStatus {

    private final ResourceDeposit resourceDeposit;

    public ResourceDepositStatus(ResourceDeposit resourceDeposit, GameState gameState) {
        super(gameState);

        this.resourceDeposit = resourceDeposit;

        update();
    }

    @Override
    public void update() {
        updateText(resourceDeposit.getName(),
                "Resources left: " + resourceDeposit.getHp());
    }

    @Override
    protected int getTextLineNumber() {
        return 2;
    }
}
