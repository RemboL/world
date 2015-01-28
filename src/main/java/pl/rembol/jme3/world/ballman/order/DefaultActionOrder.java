package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;

public class DefaultActionOrder extends Order {

	@Override
	protected void doPerform(BallMan ballMan, Vector2f location) {
		ballMan.performDefaultAction(location);
	}

	@Override
	protected void doPerform(BallMan ballMan, Selectable target) {
		ballMan.performDefaultAction(target);
	}

}
