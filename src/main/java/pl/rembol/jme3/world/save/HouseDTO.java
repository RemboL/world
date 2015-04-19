package pl.rembol.jme3.world.save;

import pl.rembol.jme3.world.house.House;
import pl.rembol.jme3.world.interfaces.WithNode;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("house")
public class HouseDTO extends UnitDTO {

	private String player;

	public HouseDTO(String key, House house) {
		super(key, house.getNode().getWorldTranslation());
		this.player = house.getOwner().getName();
	}

	public String getPlayer() {
		return player;
	}

	@Override
	public Class<? extends WithNode> getUnitClass() {
		return House.class;
	}

}
