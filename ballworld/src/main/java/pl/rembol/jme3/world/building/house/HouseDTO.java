package pl.rembol.jme3.world.building.house;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.save.UnitDTO;

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
    public WithNode produce(RtsGameState gameState) {
        return new House(gameState);
    }

}
