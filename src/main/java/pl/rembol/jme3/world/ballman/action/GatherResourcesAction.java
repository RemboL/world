package pl.rembol.jme3.world.ballman.action;

import java.util.Optional;

import pl.rembol.jme3.world.Tree;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.smallobject.Log;
import pl.rembol.jme3.world.warehouse.Warehouse;

public class GatherResourcesAction extends Action {

	private Tree tree;

	private Warehouse warehouse;

	private BallMan ballMan;

	public GatherResourcesAction init(BallMan ballMan, Tree tree) {
		this.ballMan = ballMan;
		this.tree = tree;

		warehouse = getClosestWarehouse();

		return this;
	}

	@Override
	protected void doAct(BallMan ballMan, float tpf) {

		if (ballMan.getWieldedObject() instanceof Log
				&& getClosestWarehouse() != null) {
			ballMan.addActionOnStart(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(ReturnResourcesAction.class)
					.init(getClosestWarehouse()));
		} else {
			ballMan.addActionOnStart(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(ChopTreeAction.class).init(tree));
		}
	}

	public boolean isFinished(BallMan ballMan) {
		return (getClosestWarehouse() == null) || tree.isDestroyed();
	}

	private Warehouse getClosestWarehouse() {

		if (warehouse == null) {
			Optional<Warehouse> optional = ballMan.getOwner()
					.getClosestWarehouse(tree.getLocation());
			if (optional.isPresent()) {
				warehouse = optional.get();
			}
		}

		return warehouse;
	}

}
