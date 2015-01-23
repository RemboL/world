package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.ballman.BallMan;

public abstract class Action {

	protected int frame = 0;
	private boolean isStarted = false;

	protected void start(BallMan ballMan, GameRunningAppState appState) {
		frame = 0;
	}

	public void act(BallMan ballMan, GameRunningAppState appState, float tpf) {
		if (!isStarted) {
			isStarted = true;
			start(ballMan, appState);
		}
		doAct(ballMan, tpf);

		frame++;
	}

	abstract protected void doAct(BallMan ballMan, float tpf);

	public boolean isFinished(BallMan ballMan) {
		return false;
	}

	public void finish() {
	}

}
