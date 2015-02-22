package pl.rembol.jme3.world.ballman.order;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.house.House;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

public class RecruitOrder extends Order<House> {

	@Override
	protected void doPerform(House house, Vector2f location) {
		doPerform(house);
	}

	@Override
	protected void doPerform(House house, Selectable target) {
		doPerform(house);
	}

	private void doPerform(House house) {
		if (house.getOwner().retrieveResources(50, 1)) {

			Vector2f location = new Vector2f(house.getLocation().x,
					house.getLocation().z);

			BallMan ballMan = new BallMan(applicationContext, location.add(
					new Vector2f(-10, 0)).add(
					new Vector2f(FastMath.rand.nextFloat() * 2 - 1,
							FastMath.rand.nextFloat() * 2 - 1)));
			ballMan.setOwner(house.getOwner());
		}
	}
}
