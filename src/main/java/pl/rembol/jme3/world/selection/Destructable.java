package pl.rembol.jme3.world.selection;

public interface Destructable extends Selectable {

	void strike(int strength);

	boolean isDestroyed();

}
