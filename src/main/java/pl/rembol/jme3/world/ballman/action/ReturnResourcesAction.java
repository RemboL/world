package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.smallobject.Log;
import pl.rembol.jme3.world.warehouse.Warehouse;

public class ReturnResourcesAction extends Action {

	private float targetDistance = 8;
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
					.init(warehouse, 8));
		}
	}

	private boolean isCloseEnough(BallMan ballMan) {
		return ballMan.getLocation().distance(
				warehouse.getNode().getWorldTranslation()) < targetDistance;
	}

}
