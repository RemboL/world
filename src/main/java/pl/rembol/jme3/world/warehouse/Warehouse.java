package pl.rembol.jme3.world.warehouse;

import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.smallobject.Log;

import com.jme3.scene.Node;

public class Warehouse extends Building {

	@Override
	public Node initNode() {
		return (Node) assetManager.loadModel("warehouse/warehouse.scene");
	}

	@Override
	public float getHeight() {
		return 15f;
	}

	@Override
	public float getWidth() {
		return 5f;
	}

	@Override
	public String getName() {
		return "Warehouse";
	}

	@Override
	public String[] getGeometriesWithChangeableColor() {
		return new String[] { "Flag" };
	}

	@Override
	public String getIconName() {
		return "warehouse";
	}

	private void increaseResources(int resources) {
		if (owner != null) {
			owner.addWood(resources);
		}
	}

	public void returnResource(BallMan ballMan) {
		if (ballMan.getWieldedObject() instanceof Log) {
			Log log = (Log) ballMan.getWieldedObject();

			increaseResources(log.getResourceCount());
			ballMan.dropAndDestroy();
		}

	}

	@Override
	public int getMaxHp() {
		return 100;
	}

}
