package pl.rembol.jme3.world.building.warehouse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.unit.interfaces.WithNode;
import pl.rembol.jme3.rts.save.UnitDTO;

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
    public WithNode produce(GameState gameState) {
        // TODO FIXME
        return new Warehouse(pl.rembol.jme3.world.GameState.class.cast(gameState));
    }

}
