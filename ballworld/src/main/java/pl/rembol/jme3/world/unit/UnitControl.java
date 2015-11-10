package pl.rembol.jme3.world.unit;

import com.jme3.animation.LoopMode;
import pl.rembol.jme3.rts.unit.control.ActionQueueControl;
import pl.rembol.jme3.rts.GameState;

public class UnitControl extends ActionQueueControl<Unit> {

    public UnitControl(GameState gameState, Unit unit) {
        super(gameState, unit);
    }

    @Override
    protected void onEmptyQueue() {
        unit.setAnimation("stand", LoopMode.DontLoop);
    }

    @Override
    protected void onFinishedAction() {
        unit.setAnimation("stand", LoopMode.DontLoop);
    }
}
