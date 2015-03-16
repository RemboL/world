package pl.rembol.jme3.world.ballman.action;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import pl.rembol.jme3.world.ballman.BallMan;

public abstract class Action implements ApplicationContextAware {

	protected int frame = 0;
	private boolean isStarted = false;
	protected ApplicationContext applicationContext;

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
		stop();
	}

	public void stop() {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
