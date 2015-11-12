package pl.rembol.jme3.world.save;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.world.resources.deposits.FruitBush;

@XStreamAlias("fruitbush")
public class FruitBushDTO extends UnitDTO {

    private int hp;

    public FruitBushDTO(String key, FruitBush fruitBush) {
        super(key, fruitBush.getNode().getWorldTranslation());
        this.hp = fruitBush.getHp();
    }

    public int getHp() {
        return hp;
    }

    @Override
    public WithNode produce(GameState gameState) {

        // TODO FIXME
        return new FruitBush(pl.rembol.jme3.world.GameState.class.cast(gameState));
    }

}
