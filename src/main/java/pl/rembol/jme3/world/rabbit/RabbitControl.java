package pl.rembol.jme3.world.rabbit;

import org.springframework.context.ApplicationContext;

import com.jme3.animation.LoopMode;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.WaitAction;
import pl.rembol.jme3.world.controls.ActionQueueControl;

public class RabbitControl extends ActionQueueControl<Rabbit> {

    private ApplicationContext applicationContext;

    protected RabbitControl(ApplicationContext applicationContext, Rabbit unit) {
        super(unit);
        this.applicationContext = applicationContext;
    }

    @Override
    protected void onEmptyQueue() {
        unit.setAnimation("stand", LoopMode.DontLoop);

        addAction(new MoveTowardsLocationAction(applicationContext.getBean(GameState.class), unit,
                new Vector2f(unit.getLocation().x
                        + FastMath.nextRandomInt(-10, 10), unit.getLocation().z
                        + FastMath.nextRandomInt(-10, 10)), 2));
        addAction(new WaitAction(applicationContext.getBean(GameState.class), FastMath.nextRandomInt(5, 10)));
    }

    @Override
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);

    }

    @Override
    protected void onFinishedAction() {
        unit.setAnimation("stand", LoopMode.DontLoop);
    }

}
