package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.ballman.BallMan;

public abstract class Action {

	protected int frame = 0;
	private boolean isStarted = false;

	protected void start(BallMan ballMan) {
	}

	public void act(BallMan ballMan) {
		if (!isStarted) {
			isStarted = true;
			start(ballMan);
		}
		frame++;

		doAct(ballMan);
	}

	abstract protected void doAct(BallMan ballMan);

	public boolean isFinished(BallMan ballMan) {
		return false;
	}

}
