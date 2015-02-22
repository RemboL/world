package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.Tree;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.smallobject.Axe;
import pl.rembol.jme3.world.smallobject.Log;

import com.jme3.animation.LoopMode;

public class ChopTreeAction extends Action {

	private static final int HIT_FRAME = 20 * 1000 / 30;

	private static final int MAX_CHOPS = 10;

	private static final float REQUIRED_DISTANCE = 5f;

	private Tree tree;

	private long animationStart;

	private boolean hit = false;

	private boolean waitForAnimationToFinish = false;

	private int chopCounter = 0;

	/**
	 * 35 frames of animation * 1000 millis in second / 30 frames per second
	 */
	private static final int ANIMATION_LENGTH = 35 * 1000 / 30;

	public ChopTreeAction init(Tree tree) {
		this.tree = tree;

		return this;
	}

	@Override
	protected void start(BallMan ballMan) {
		assertDistance(ballMan);
		ballMan.wield(new Axe(applicationContext));
	}

	private void assertDistance(BallMan ballMan) {
		if (!isCloseEnough(ballMan)) {
			ballMan.addActionOnStart(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(MoveTowardsTargetAction.class)
					.init(tree, REQUIRED_DISTANCE));
		}
	}

	private boolean isCloseEnough(BallMan ballMan) {
		return ballMan.getLocation().distance(
				tree.getNode().getWorldTranslation()) < REQUIRED_DISTANCE;
	}

	@Override
	protected void doAct(BallMan ballMan, float tpf) {
		if (!waitForAnimationToFinish) {
			if (animationEnded()) {
				resetAnimation(ballMan);
			}
			if (animationHit()) {
				hit = true;
				tree.getChoppedBy(ballMan);
				// chopCounter++;
				chopCounter += 5;
			}

			if (actionFinished()) {
				waitForAnimationToFinish = true;
			}
		}
	}

	private boolean actionFinished() {
		return tree.isDestroyed() || chopCounter >= MAX_CHOPS;
	}

	private void resetAnimation(BallMan ballMan) {
		ballMan.setAnimation("strike", LoopMode.DontLoop);
		animationStart = System.currentTimeMillis();
		hit = false;
	}

	@Override
	public boolean isFinished(BallMan ballMan) {
		if (actionFinished() && animationEnded()) {

			ballMan.wield(new Log(applicationContext, ballMan.getLocation(),
					chopCounter));
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
