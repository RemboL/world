package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.ballman.BallMan;

public abstract class Action {

	protected int frame = 0;
	private boolean isStarted = false;
	protected GameRunningAppState appState;

	public Action(GameRunningAppState appState) {
		this.appState = appState;

	}

	protected void start(BallMan ballMan) {
		frame = 0;
	}

	public void act(BallMan ballMan, float tpf) {
		if (!isStarted) {
			isStarted = true;
			start(ballMan);
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
