package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.Tree;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.animation.AttackAnimation;

public class ChopTreeAction extends Action {

	private Tree tree;

	public ChopTreeAction(Tree tree) {
		this.tree = tree;
	}

	@Override
	protected void doAct(BallMan ballMan) {
		if (frame % 100 == 51) {
			tree.getChoppedBy(ballMan);
		}
	}

	@Override
	public boolean isFinished(BallMan ballMan) {
		return false;
	}

	@Override
	public void start(BallMan ballMan) {
		ballMan.setAnimation(new AttackAnimation());
	}

}
