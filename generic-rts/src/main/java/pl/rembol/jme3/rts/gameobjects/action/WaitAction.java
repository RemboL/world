package pl.rembol.jme3.rts.gameobjects.action;

import com.jme3.animation.LoopMode;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;

public class WaitAction extends Action<WithNode> {

    private int seconds;
    private long started;

    public WaitAction(RtsGameState gameState, int seconds) {
        super(gameState);
        this.seconds = seconds;
    }

    @Override
    protected void doAct(WithNode unit, float tpf) {
    }

    @Override
    protected boolean start(WithNode unit) {
        unit.setAnimation("stand", LoopMode.DontLoop);
        started = System.currentTimeMillis();
        return true;
    }

    @Override
    public boolean isFinished(WithNode unit) {
        return System.currentTimeMillis() > started + seconds * 1000;
    }

}
