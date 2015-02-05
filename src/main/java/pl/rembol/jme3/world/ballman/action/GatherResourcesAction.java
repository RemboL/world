package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.Tree;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.smallobject.Log;
import pl.rembol.jme3.world.warehouse.Warehouse;

public class GatherResourcesAction extends Action {

	private Tree tree;

	private Warehouse warehouse;

	public GatherResourcesAction(GameRunningAppState appState, Tree tree) {
		super(appState);
		this.tree = tree;

		warehouse = getClosestWarehouse();
	}

	@Override
	protected void doAct(BallMan ballMan, float tpf) {

		if (ballMan.getWieldedObject() instanceof Log) {
			ballMan.addActionOnStart(new ReturnResourcesAction(appState,
					getClosestWarehouse()));
		} else {
			ballMan.addActionOnStart(new ChopTreeAction(appState, tree));
		}
	}

	public boolean isFinished(BallMan ballMan) {
		return (getClosestWarehouse() == null) || tree.isDestroyed();
	}

	private Warehouse getClosestWarehouse() {

		if (warehouse == null) {
			warehouse = appState.getClosestWarehouse(tree.getLocation());
		}

		return warehouse;
	}

}
