package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.BallMan.Hand;
import pl.rembol.jme3.world.resources.deposits.ResourceDeposit;
import pl.rembol.jme3.world.smallobject.tools.Axe;

import com.jme3.animation.LoopMode;

public class ChopTreeAction extends Action {

    private static final int HIT_FRAME = 20 * 1000 / 30;

    private static final int MAX_CHOPS = 10;

    private static final float REQUIRED_DISTANCE = 2f;

    private ResourceDeposit resourceDeposit;

    private long animationStart;

    private boolean hit = false;

    private boolean waitForAnimationToFinish = false;

    private int chopCounter = 0;

    /**
     * 35 frames of animation * 1000 millis in second / 30 frames per second
     */
    private static final int ANIMATION_LENGTH = 35 * 1000 / 30;

    public ChopTreeAction init(ResourceDeposit resourceDeposit) {
        this.resourceDeposit = resourceDeposit;

        return this;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        assertDistance(ballMan, resourceDeposit, REQUIRED_DISTANCE);
        return assertWielded(ballMan, Axe.class);
    }

    @Override
    protected void doAct(BallMan ballMan, float tpf) {
        if (!waitForAnimationToFinish) {
            if (animationEnded()) {
                resetAnimation(ballMan);
            }
            if (animationHit()) {
                hit = true;
                resourceDeposit.getChoppedBy(ballMan);
                chopCounter += 2;
            }

            if (actionFinished()) {
                waitForAnimationToFinish = true;
            }
        }
    }

    private boolean actionFinished() {
        return resourceDeposit.isDestroyed() || chopCounter >= MAX_CHOPS;
    }

    private void resetAnimation(BallMan ballMan) {
        ballMan.setAnimation("strike", LoopMode.DontLoop);
        animationStart = System.currentTimeMillis();
        hit = false;
    }

    @Override
    public boolean isFinished(BallMan ballMan) {
        if (actionFinished() && animationEnded()) {

            ballMan.wield(resourceDeposit.produceResource(chopCounter),
                    Hand.LEFT);
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
