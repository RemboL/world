package pl.rembol.jme3.rts.gameobjects.unit;

import com.jme3.animation.LoopMode;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.control.ActionQueueControl;

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
