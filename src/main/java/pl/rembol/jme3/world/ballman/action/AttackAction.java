package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.selection.Destructable;
import pl.rembol.jme3.world.smallobject.tools.Sword;

import com.jme3.animation.LoopMode;

public class AttackAction extends Action {

    private static final int HIT_FRAME = 20 * 1000 / 30;

    private static final float REQUIRED_DISTANCE = 3f;

    private Destructable target;

    private long animationStart;

    private boolean hit = false;

    private boolean waitForAnimationToFinish = false;

    /**
     * 35 frames of animation * 1000 millis in second / 30 frames per second
     */
    private static final int ANIMATION_LENGTH = 35 * 1000 / 30;

    public AttackAction init(Destructable target) {
        this.target = target;

        return this;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        if (!assertWielded(ballMan, Sword.class)) {
            return false;
        }
        if (!assertDistance(ballMan, target, REQUIRED_DISTANCE)) {
            return false;
        }
        return true;
    }

    @Override
    protected void doAct(BallMan ballMan, float tpf) {
        if (!waitForAnimationToFinish) {
            if (animationEnded()) {
                resetAnimation(ballMan);
            }
            if (animationHit()) {
                hit = true;
                target.strike(5);
            }

            if (actionFinished()) {
                waitForAnimationToFinish = true;
            }
        }
    }

    private boolean actionFinished() {
        return target.isDestroyed();
    }

    private void resetAnimation(BallMan ballMan) {
        ballMan.setAnimation("strike", LoopMode.DontLoop);
        animationStart = System.currentTimeMillis();
        hit = false;
    }

    @Override
    public boolean isFinished(BallMan ballMan) {
        if (actionFinished() && animationEnded()) {
            return true;
        }
        return false;
    }

    private boolean animationEnded() {
        return System.currentTimeMillis() - animationStart >= ANIMATION_LENGTH;
    }

    private boolean animationHit() {
        return !hit && System.currentTimeMillis() - animationStart >= HIT_FRAME;
    }

}
