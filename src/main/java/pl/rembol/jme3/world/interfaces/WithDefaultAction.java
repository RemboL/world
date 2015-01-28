package pl.rembol.jme3.world.interfaces;

import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector2f;

public interface WithDefaultAction {

	void performDefaultAction(Selectable target);

	void performDefaultAction(Vector2f target);

}
