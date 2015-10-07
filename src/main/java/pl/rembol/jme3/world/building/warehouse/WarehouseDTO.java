package pl.rembol.jme3.world.building.warehouse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.save.UnitDTO;

@XStreamAlias("warehouse")
public class WarehouseDTO extends UnitDTO {

	private String player;

	public WarehouseDTO(String key, Warehouse warehouse) {
		super(key, warehouse.getNode().getWorldTranslation());
		this.player = warehouse.getOwner().getName();
	}

	public String getPlayer() {
		return player;
	}

	@Override
	public Class<? extends WithNode> getUnitClass() {
		return Warehouse.class;
	}

}
