package pl.rembol.jme3.world.ballman.action;

import java.util.Optional;

import pl.rembol.jme3.world.Tree;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.smallobject.Log;

public class GatherResourcesAction extends Action {

	private Tree tree;

	private BallMan ballMan;

	public GatherResourcesAction init(BallMan ballMan, Tree tree) {
		this.ballMan = ballMan;
		this.tree = tree;

		getClosestWarehouse();

		return this;
	}

	@Override
	protected void doAct(BallMan ballMan, float tpf) {

		Optional<Warehouse> warehouse = getClosestWarehouse();

		if (ballMan.getWieldedObject() instanceof Log && warehouse.isPresent()) {
			ballMan.addActionOnStart(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(ReturnResourcesAction.class)
					.init(warehouse.get()));
		} else {
			ballMan.addActionOnStart(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(ChopTreeAction.class).init(tree));
		}
	}

	public boolean isFinished(BallMan ballMan) {
		return (getClosestWarehouse() == null) || tree.isDestroyed();
	}

	private Optional<Warehouse> getClosestWarehouse() {

		return ballMan.getOwner().getClosestWarehouse(tree.getLocation());
	}

}
