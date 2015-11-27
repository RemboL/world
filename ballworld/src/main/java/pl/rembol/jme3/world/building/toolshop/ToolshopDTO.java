package pl.rembol.jme3.world.building.toolshop;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.save.UnitDTO;

@XStreamAlias("toolshop")
public class ToolshopDTO extends UnitDTO {

    private String player;

    public ToolshopDTO(String key, Toolshop toolshop) {
        super(key, toolshop.getNode().getWorldTranslation());
        this.player = toolshop.getOwner().getName();
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public WithNode produce(GameState gameState) {
        return new Toolshop(gameState);
    }

}
