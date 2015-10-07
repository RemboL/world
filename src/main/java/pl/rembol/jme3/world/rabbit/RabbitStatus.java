package pl.rembol.jme3.world.rabbit;

import org.springframework.context.ApplicationContext;
import pl.rembol.jme3.world.hud.status.DefaultStatus;

public class RabbitStatus extends DefaultStatus {

    public RabbitStatus(ApplicationContext applicationContext) {
        super(applicationContext);
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
