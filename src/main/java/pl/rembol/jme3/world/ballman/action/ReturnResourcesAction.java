package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.pathfinding.Rectangle2f;
import pl.rembol.jme3.world.smallobject.Log;

public class ReturnResourcesAction extends Action {

	private float targetDistance = 3;
	private Warehouse warehouse;

	public ReturnResourcesAction init(Warehouse target) {
		this.warehouse = target;

		return this;
	}

	@Override
	protected void doAct(BallMan ballMan, float tpf) {
		assertDistance(ballMan);

		if (isCloseEnough(ballMan)) {
			if (ballMan.getWieldedObject() instanceof Log) {
				warehouse.returnResource(ballMan);
			}
		}
	}

	@Override
	public boolean isFinished(BallMan ballMan) {
		if (ballMan.getWieldedObject() instanceof Log) {
			return false;
		}

		return true;
	}

	private void assertDistance(BallMan ballMan) {
		if (!isCloseEnough(ballMan)) {
			ballMan.addActionOnStart(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(MoveTowardsTargetAction.class)
					.init(warehouse, targetDistance));
		}
	}

	private boolean isCloseEnough(BallMan ballMan) {
		return new Rectangle2f(warehouse, targetDistance).isInside(ballMan
				.getNode().getWorldTranslation());
	}

}
