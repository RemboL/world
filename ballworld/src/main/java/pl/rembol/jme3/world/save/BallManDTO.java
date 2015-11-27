package pl.rembol.jme3.world.save;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.world.ballman.BallMan;

@XStreamAlias("ballman")
public class BallManDTO extends UnitDTO {

    private String player;

    public BallManDTO(String key, BallMan ballMan) {
        super(key, ballMan.getNode().getWorldTranslation());
        this.player = ballMan.getOwner().getName();
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public WithNode produce(GameState gameState) {
        return new BallMan(gameState, getPosition(), player);
    }

}
