package pl.rembol.jme3.world.building.house;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;

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
    public WithNode produce(GameState gameState) {
        // TODO FIXME
        return new House(pl.rembol.jme3.world.GameState.class.cast(gameState));
    }

}
