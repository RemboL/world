package pl.rembol.jme3.world.house;

import pl.rembol.jme3.world.building.Building;

import com.jme3.scene.Node;

public class House extends Building {

	@Override
	public Node initNode() {
		return (Node) assetManager.loadModel("house2/house2.scene");
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
		return "House";
	}

	@Override
	public String[] getGeometriesWithChangeableColor() {
		return new String[] { "hay" };
	}

	@Override
	public String getIconName() {
		return "house";
	}

	@Override
	public int getMaxHp() {
		return 150;
	}
}
