package pl.rembol.jme3.world.rabbit;

import com.jme3.animation.LoopMode;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.unit.action.MoveTowardsLocationAction;
import pl.rembol.jme3.rts.unit.action.WaitAction;
import pl.rembol.jme3.rts.unit.control.ActionQueueControl;
import pl.rembol.jme3.world.GameState;

public class RabbitControl extends ActionQueueControl<Rabbit> {

    protected RabbitControl(GameState gameState, Rabbit unit) {
        super(gameState, unit);
    }

    @Override
    protected void onEmptyQueue() {
        unit.setAnimation("stand", LoopMode.DontLoop);

        addAction(new MoveTowardsLocationAction(gameState, unit,
                new Vector2f(unit.getLocation().x
                        + FastMath.nextRandomInt(-10, 10), unit.getLocation().z
                        + FastMath.nextRandomInt(-10, 10)), 2));
        addAction(new WaitAction(gameState, FastMath.nextRandomInt(5, 10)));
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