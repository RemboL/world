package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.SmoothenTerrainAction;
import pl.rembol.jme3.world.interfaces.WithNode;

import com.jme3.math.Vector2f;

public class SmoothenTerrainOrder extends Order<BallMan> {

	@Override
	protected void doPerform(BallMan ballMan, Vector2f location) {
		ballMan.setAction(applicationContext.getAutowireCapableBeanFactory()
				.createBean(MoveTowardsLocationAction.class)
				.init(location, 10f));
		ballMan.addAction(applicationContext
				.getAutowireCapableBeanFactory()
				.createBean(SmoothenTerrainAction.class)
				.init(location.add(new Vector2f(-5f, -5f)),
						location.add(new Vector2f(5f, 5f)), 5));
	}

	@Override
	protected void doPerform(BallMan ballMan, WithNode target) {
		System.out.println("I cannot smoothen the " + target);
	}

}
