package pl.rembol.jme3.world.interfaces;

import pl.rembol.jme3.world.save.UnitDTO;

import com.jme3.scene.Node;

public interface WithNode {

	Node getNode();

	Node initNodeWithScale();

	float getWidth();

	default UnitDTO save(String key) {
		return new UnitDTO(key, getNode().getWorldTranslation());
	}

	default void load(UnitDTO unitDTO) {
	}

}
