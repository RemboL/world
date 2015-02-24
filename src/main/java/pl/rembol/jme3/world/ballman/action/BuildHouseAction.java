package pl.rembol.jme3.world.ballman.action;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.house.House;

public class BuildHouseAction extends BuildAction {

	@Override
	public House createBuilding() {
		return applicationContext.getAutowireCapableBeanFactory().createBean(
				House.class);
	}

	@Override
	protected boolean retrieveResources(BallMan ballMan) {
		return ballMan.getOwner().retrieveResources(100, 0);
	}

}
