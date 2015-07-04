package pl.rembol.jme3.world.save;

import pl.rembol.jme3.world.player.Player;

import com.jme3.math.ColorRGBA;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("player")
public class PlayerDTO {

    private String name;

    private ColorRGBA color;

    private Boolean active;
    private int wood;
    private int stone;

    public PlayerDTO(Player player) {
        this.name = player.getName();
        this.color = player.getColor();
        this.active = player.isActive();
        this.wood = player.getResourcesWood();
        this.stone = player.getResourcesStone();
    }

    public String getName() {
        return name;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public Boolean getActive() {
        return active;
    }

    public int getWood() {
        return wood;
    }

    public int getStone() {
        return stone;
    }
}
