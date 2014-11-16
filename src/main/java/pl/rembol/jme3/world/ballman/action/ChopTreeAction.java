package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.Tree;
import pl.rembol.jme3.world.ballman.BallMan;

import com.jme3.animation.LoopMode;

public class ChopTreeAction extends Action {

	private static final int HIT_FRAME = 20 * 1000 / 30;

	private Tree tree;

	private long animationStart;

	private boolean hit = false;

	private boolean waitForAnimationToFinish = false;

	/**
	 * 35 frames of animation * 1000 millis in second / 30 frames per second
	 */
	private static final int ANIMATION_LENGTH = 35 * 1000 / 30;

	public ChopTreeAction(Tree tree) {
		this.tree = tree;
	}

	@Override
	protected void doAct(BallMan ballMan) {
		if (!waitForAnimationToFinish) {
			if (animationEnded()) {
				resetAnimation(ballMan);
			}
			if (animationHit()) {
				hit = true;
				tree.getChoppedBy(ballMan);
			}

			if (tree.isDestroyed()) {
				waitForAnimationToFinish = true;
			}
		}
	}

	private void resetAnimation(BallMan ballMan) {
		ballMan.setAnimation("strike", LoopMode.DontLoop);
		animationStart = System.currentTimeMillis();
		hit = false;
	}

	@Override
	public boolean isFinished(BallMan ballMan) {
		if (tree.isDestroyed() && animationEnded()) {
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
