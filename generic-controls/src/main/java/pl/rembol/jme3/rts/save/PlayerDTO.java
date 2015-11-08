package pl.rembol.jme3.rts.save;

import com.jme3.math.ColorRGBA;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.rts.player.Player;

@XStreamAlias("player")
public class PlayerDTO {

    private String name;

    private ColorRGBA color;

    private Boolean active;

    public PlayerDTO(Player player) {
        this.name = player.getName();
        this.color = player.getColor();
        this.active = player.isActive();
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

}
