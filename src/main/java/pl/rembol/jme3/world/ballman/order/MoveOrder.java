package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsTargetAction;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;

public class MoveOrder extends Order {

	@Override
	protected void doPerform(BallMan ballMan, Vector2f location) {
		ballMan.addAction(new MoveTowardsLocationAction(location, 5f));
	}

	@Override
	protected void doPerform(BallMan ballMan, Selectable target) {
		ballMan.addAction(new MoveTowardsTargetAction(target, 5f));
	}

}
