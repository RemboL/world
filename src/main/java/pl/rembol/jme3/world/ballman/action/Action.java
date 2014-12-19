package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.ballman.BallMan;

public abstract class Action {

	protected int frame = 0;
	private boolean isStarted = false;

	protected void start(BallMan ballMan, GameRunningAppState appState) {
		frame = 0;
	}

	public void act(BallMan ballMan, GameRunningAppState appState) {
		if (!isStarted) {
			isStarted = true;
			start(ballMan, appState);
		}
		doAct(ballMan);

		frame++;
	}

	abstract protected void doAct(BallMan ballMan);

	public boolean isFinished(BallMan ballMan) {
		return false;
	}

	public void finish() {
	}

}
