package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.Tree;
import pl.rembol.jme3.world.ballman.BallMan;

public class MoveTowardsTargetAction extends Action {

	private Tree target;
	private float targetDistance;

	public MoveTowardsTargetAction(Tree target, float targetDistance) {
		this.target = target;
		this.targetDistance = targetDistance;
	}

	@Override
	protected void doAct(BallMan ballMan) {
		ballMan.lookTorwards(target);
		ballMan.setTargetVelocity(5f);
	}

	@Override
	public boolean isFinished(BallMan ballMan) {
		if (ballMan.getLocation().distance(target.getLocation()) < targetDistance) {
			ballMan.setTargetVelocity(0f);
			return true;
		}

		return false;
	}

}
