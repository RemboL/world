package pl.rembol.jme3.world.selection;

import pl.rembol.jme3.world.interfaces.WithNode;

public interface Selectable extends WithNode {

	void select();

	void deselect();
}
