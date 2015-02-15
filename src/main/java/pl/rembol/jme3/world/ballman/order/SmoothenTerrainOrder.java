package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.SmoothenTerrainAction;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;

public class SmoothenTerrainOrder extends Order {

	@Override
	protected void doPerform(BallMan ballMan, Vector2f location) {
		ballMan.setAction(new MoveTowardsLocationAction(appState, location, 10f));
		ballMan.addAction(new SmoothenTerrainAction(appState, GameState.get()
				.getTerrain(), location.add(new Vector2f(-5f, -5f)), location
				.add(new Vector2f(5f, 5f)), 5));
	}

	@Override
	protected void doPerform(BallMan ballMan, Selectable target) {
		System.out.println("I cannot smoothen the " + target);
	}

}
