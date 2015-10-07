package pl.rembol.jme3.world.ballman.action;

import com.jme3.animation.LoopMode;
import pl.rembol.jme3.world.interfaces.WithNode;

public class WaitAction extends Action<WithNode> {

    private int seconds;
    private long started;

    public WaitAction init(int seconds) {
        this.seconds = seconds;
        return this;
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
