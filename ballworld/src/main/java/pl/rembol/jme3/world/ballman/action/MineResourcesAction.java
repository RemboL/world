package pl.rembol.jme3.world.ballman.action;

import com.jme3.animation.LoopMode;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.BallMan.Hand;
import pl.rembol.jme3.world.resources.deposits.ResourceDeposit;
import pl.rembol.jme3.world.resources.units.ResourceUnit;

import java.util.Optional;

public class MineResourcesAction extends Action<BallMan> {

    private static final int HIT_FRAME = 20 * 1000 / 30;

    private static final int MAX_CHOPS = 10;

    private static final float REQUIRED_DISTANCE = 3f;

    private ResourceDeposit resourceDeposit;

    private long animationStart;

    private boolean hit = false;

    private boolean waitForAnimationToFinish = false;

    /**
     * 35 frames of animation * 1000 millis in second / 30 frames per second
     */
    private static final int ANIMATION_LENGTH = 35 * 1000 / 30;

    public MineResourcesAction(GameState gameState, ResourceDeposit resourceDeposit) {
        super(gameState);
        this.resourceDeposit = resourceDeposit;
    }

    @Override
    protected boolean start(BallMan ballMan) {
        if (!assertWielded(ballMan, resourceDeposit.requiredTool())) {
            return false;
        }

        if (!assertDistance(ballMan, resourceDeposit, REQUIRED_DISTANCE)) {
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
                resourceDeposit.getChoppedBy(ballMan);
                increaseHeldResourceCount(ballMan, 2);
            }

            if (actionFinished(ballMan)) {
                waitForAnimationToFinish = true;
            }
        }
    }

    private void increaseHeldResourceCount(BallMan ballMan, int count) {
        if (!resourceDeposit.givesResource().isInstance(
                ballMan.getWieldedObject(Hand.LEFT))) {
            ballMan.wield(Optional.of(resourceDeposit.produceResource()),
                    Hand.LEFT);
        }

        ResourceUnit.class.cast(ballMan.getWieldedObject(Hand.LEFT))
                .increaseCount(count);
    }

    private boolean actionFinished(BallMan ballMan) {
        if (resourceDeposit.isDestroyed()) {
            return true;
        }
        if (resourceDeposit.givesResource().isInstance(
                ballMan.getWieldedObject(Hand.LEFT))
                && ResourceUnit.class.cast(ballMan.getWieldedObject(Hand.LEFT))
                .getResourceCount() >= MAX_CHOPS) {
            return true;
        }

        return false;
    }

    private void resetAnimation(BallMan ballMan) {
        ballMan.setAnimation("strike", LoopMode.DontLoop);
        animationStart = System.currentTimeMillis();
        hit = false;
    }

    @Override
    public boolean isFinished(BallMan ballMan) {
        if (actionFinished(ballMan) && animationEnded()) {

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
