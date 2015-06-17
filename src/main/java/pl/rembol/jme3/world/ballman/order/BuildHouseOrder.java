package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.BuildAction;
import pl.rembol.jme3.world.ballman.action.BuildHouseAction;

public class BuildHouseOrder extends BuildOrder {

	@Override
	public Class<? extends BuildAction> getActionClass() {
		return BuildHouseAction.class;
	}

	@Override
	protected boolean hasResources(BallMan ballMan) {
		return ballMan.getOwner().hasResources(100, 0, 0);
	}

}
