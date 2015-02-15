package pl.rembol.jme3.player;

import pl.rembol.jme3.world.ModelHelper;
import pl.rembol.jme3.world.interfaces.WithNode;

public interface WithOwner extends WithNode {

	public Player getOwner();

	public void setOwner(Player player);

	default public void updateColor() {
		if (getOwner() != null && getOwner().getColor() != null
				&& getNode() != null) {
			for (String geometry : getGeometriesWithChangeableColor()) {
				ModelHelper.setColorToGeometry(getNode(),
						getOwner().getColor(), geometry);
			}
		}
	}

	public String[] getGeometriesWithChangeableColor();

}
