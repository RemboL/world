package pl.rembol.jme3.world.resources.deposits;

import org.springframework.context.ApplicationContext;
import pl.rembol.jme3.world.hud.status.DefaultStatus;

public class ResourceDepositStatus extends DefaultStatus {

    private final ResourceDeposit resourceDeposit;

    public ResourceDepositStatus(ResourceDeposit resourceDeposit, ApplicationContext applicationContext) {
        super(applicationContext);

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
