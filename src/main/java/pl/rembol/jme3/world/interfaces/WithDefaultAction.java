package pl.rembol.jme3.world.interfaces;

import com.jme3.math.Vector2f;

public interface WithDefaultAction {

	void performDefaultAction(WithNode target);

	void performDefaultAction(Vector2f target);

}
