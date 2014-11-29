package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.interfaces.WithNode;

import com.jme3.animation.LoopMode;

public class MoveTowardsTargetAction extends Action {

	private WithNode target;
	private float targetDistance;

	public MoveTowardsTargetAction(WithNode target, float targetDistance) {
		this.target = target;
		this.targetDistance = targetDistance;
	}

	@Override
	protected void doAct(BallMan ballMan) {
		ballMan.lookTowards(target);
		ballMan.setTargetVelocity(5f);
	}

	@Override
	public boolean isFinished(BallMan ballMan) {
		if (ballMan.getLocation().distance(
				target.getNode().getWorldTranslation()) < targetDistance) {
			ballMan.setTargetVelocity(0f);
			return true;
		}

		return false;
	}

	protected void start(BallMan ballMan) {
		ballMan.setAnimation("walk", LoopMode.Loop);
	}

}
