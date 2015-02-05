package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.smallobject.Log;
import pl.rembol.jme3.world.warehouse.Warehouse;

public class ReturnResourcesAction extends Action {

	private float targetDistance = 8;
	private Warehouse warehouse;

	public ReturnResourcesAction(GameRunningAppState appState, Warehouse target) {
		super(appState);
		this.warehouse = target;
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
			ballMan.addActionOnStart(new MoveTowardsTargetAction(appState,
					warehouse, 8));
		}
	}

	private boolean isCloseEnough(BallMan ballMan) {
		return ballMan.getLocation().distance(
				warehouse.getNode().getWorldTranslation()) < targetDistance;
	}

}
