package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.BuildHouseAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.SmoothenTerrainAction;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;

public class BuildHouseOrder extends Order<BallMan> {

	@Override
	protected void doPerform(BallMan ballMan, Vector2f location) {
		if (ballMan.getOwner().hasResources(100, 0)) {

			ballMan.setAction(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(MoveTowardsLocationAction.class)
					.init(location, 10f));
			ballMan.addAction(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(SmoothenTerrainAction.class)
					.init(location.add(new Vector2f(-5f, -5f)),
							location.add(new Vector2f(5f, 5f)), 5));
			ballMan.addAction(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(BuildHouseAction.class).init(location));
		}
	}

	@Override
	protected void doPerform(BallMan ballMan, Selectable target) {
		System.out.println("I cannot smoothen the " + target);
	}

}
