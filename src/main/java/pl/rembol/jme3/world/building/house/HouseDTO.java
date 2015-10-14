package pl.rembol.jme3.world.building.house;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.save.UnitDTO;

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
        return new House(gameState);
    }

}
