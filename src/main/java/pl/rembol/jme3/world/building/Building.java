package pl.rembol.jme3.world.building;

import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.selection.Selectable;

public interface Building extends Selectable, WithNode {

	float getHeight();

	float getWidth();

	default boolean isConstructed() {
		return getNode().getControl(ConstructionSite.class) == null;
	}

	default void finish() {
	}

}
