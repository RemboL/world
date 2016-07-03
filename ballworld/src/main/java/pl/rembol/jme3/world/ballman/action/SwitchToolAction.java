package pl.rembol.jme3.world.ballman.action;

import com.jme3.animation.LoopMode;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.action.Action;
import pl.rembol.jme3.rts.smallobjects.SmallObject;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.BallMan.Hand;

import java.util.Optional;

class SwitchToolAction extends Action<BallMan> {

    private static final int SWITCH_FRAME = 17 * 1000 / 30;

    private static final int ANIMATION_LENGTH = 35 * 1000 / 30;

    private SmallObject smallObject;

    private long animationStart;

    private boolean switched = false;

    SwitchToolAction(GameState gameState, SmallObject smallObject) {
        super(gameState);
        this.smallObject = smallObject;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        ballMan.setAnimation("switchWeapons", LoopMode.DontLoop);
        animationStart = System.currentTimeMillis();
        return true;
    }

    @Override
    protected void doAct(BallMan ballMan, float tpf) {
        if (animationSwitch()) {
            switched = true;
            ballMan.wield(smallObject, Hand.RIGHT);
        }
    }

    @Override
    public boolean isFinished(BallMan ballMan) {
        return animationEnded();
    }

    private boolean animationEnded() {
        return System.currentTimeMillis() - animationStart >= ANIMATION_LENGTH;
    }

    private boolean animationSwitch() {
        return !switched
                && System.currentTimeMillis() - animationStart >= SWITCH_FRAME;
    }

}
