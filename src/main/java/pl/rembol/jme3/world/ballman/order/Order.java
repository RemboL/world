package pl.rembol.jme3.world.ballman.order;

import java.util.ArrayList;
import java.util.List;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public abstract class Order {

	protected GameRunningAppState appState;

	protected List<BallMan> selected = new ArrayList<>();

	public void perform(Vector3f location) {
		perform(new Vector2f(location.x, location.z));
	}

	public void perform(Vector2f location) {
		for (BallMan ballMan : selected) {
			doPerform(ballMan, location);
		}
	}

	public void perform(Selectable target) {
		for (BallMan ballMan : selected) {
			doPerform(ballMan, target);
		}
	}

	public void setAppState(GameRunningAppState appState) {
		this.appState = appState;
	}

	protected abstract void doPerform(BallMan ballMan, Vector2f location);

	protected abstract void doPerform(BallMan ballMan, Selectable target);

	public void setSelected(List<BallMan> selected) {
		this.selected = selected;
	}

}
