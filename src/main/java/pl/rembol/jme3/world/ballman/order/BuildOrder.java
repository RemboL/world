package pl.rembol.jme3.world.ballman.order;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.action.BuildAction;
import pl.rembol.jme3.world.ballman.action.MoveTowardsLocationAction;
import pl.rembol.jme3.world.ballman.action.SmoothenTerrainAction;
import pl.rembol.jme3.world.hud.ConsoleLog;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.terrain.Terrain;

import com.jme3.math.Vector2f;

public abstract class BuildOrder extends Order<BallMan> {

	private float width;

	@Autowired
	protected Terrain terrain;

	@Autowired
	protected ConsoleLog consoleLog;

	@Autowired
	protected UnitRegistry gameState;

	@PostConstruct
	private void initWidth() {
		width = applicationContext.getAutowireCapableBeanFactory()
				.createBean(getActionClass()).createBuilding().getWidth();
	}

	@Override
	protected void doPerform(BallMan ballMan, Vector2f location) {
		if (!gameState.isSpaceFreeWithBuffer(
				terrain.getGroundPosition(location), width)) {
			consoleLog.addLine("Can't build here, something's in the way");
			return;
		}
		if (hasResources(ballMan)) {

			ballMan.setAction(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(MoveTowardsLocationAction.class)
					.init(location, 10f));
			ballMan.addAction(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(SmoothenTerrainAction.class)
					.init(location.add(new Vector2f(-width, -width)),
							location.add(new Vector2f(width, width)), 3));
			ballMan.addAction(applicationContext
					.getAutowireCapableBeanFactory()
					.createBean(getActionClass()).init(location));
		}
	}

	protected abstract boolean hasResources(BallMan ballMan);

	public abstract Class<? extends BuildAction> getActionClass();

	@Override
	protected void doPerform(BallMan ballMan, WithNode target) {
		System.out.println("I cannot smoothen the " + target);
	}

}