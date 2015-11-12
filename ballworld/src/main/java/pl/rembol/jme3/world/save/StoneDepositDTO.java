package pl.rembol.jme3.world.save;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.world.resources.deposits.StoneDeposit;

@XStreamAlias("stone_deposit")
public class StoneDepositDTO extends UnitDTO {

    private int hp;

    public StoneDepositDTO(String key, StoneDeposit stoneDeposit) {
        super(key, stoneDeposit.getNode().getWorldTranslation());
        this.hp = stoneDeposit.getHp();
    }

    public int getHp() {
        return hp;
    }

    @Override
    public WithNode produce(GameState gameState) {
        // TODO FIXME
        return new StoneDeposit(pl.rembol.jme3.world.GameState.class.cast(gameState));
    }

}
