package pl.rembol.jme3.world.save;

import pl.rembol.jme3.world.interfaces.WithNode;

import com.jme3.math.Vector3f;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("unit")
public class UnitDTO {

	private String key;
	private Vector3f position;

	public UnitDTO(String key, Vector3f position) {
		this.key = key;
		this.position = position;
	}

	public String getKey() {
		return key;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Class<? extends WithNode> getUnitClass() {
		return WithNode.class;
	}

}
