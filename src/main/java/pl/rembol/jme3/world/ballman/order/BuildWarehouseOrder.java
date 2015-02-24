package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.BuildAction;
import pl.rembol.jme3.world.ballman.action.BuildWarehouseAction;

public class BuildWarehouseOrder extends BuildOrder {

	@Override
	public Class<? extends BuildAction> getActionClass() {
		return BuildWarehouseAction.class;
	}

	@Override
	protected boolean hasResources(BallMan ballMan) {
		return ballMan.getOwner().hasResources(150, 0);
	}

}
