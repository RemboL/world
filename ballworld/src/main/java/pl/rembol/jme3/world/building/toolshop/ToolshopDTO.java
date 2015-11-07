package pl.rembol.jme3.world.building.toolshop;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.save.UnitDTO;

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
