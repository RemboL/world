package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;

public class SelectOrder extends Order {

	@Override
	public void perform(Selectable target) {
		appState.getSelectionManager().select(target);
	}

	@Override
	protected void doPerform(BallMan ballMan, Vector2f location) {
		// doNothing
	}

	@Override
	protected void doPerform(BallMan ballMan, Selectable target) {
		// doNothing
	}

}
