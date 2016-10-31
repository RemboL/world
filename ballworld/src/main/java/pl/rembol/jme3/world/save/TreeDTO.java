package pl.rembol.jme3.world.save;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.rts.RtsGameState;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.world.resources.deposits.Tree;

@XStreamAlias("tree")
public class TreeDTO extends UnitDTO {

    private int hp;

    public TreeDTO(String key, Tree tree) {
        super(key, tree.getNode().getWorldTranslation());
        this.hp = tree.getHp();
    }

    public int getHp() {
        return hp;
    }

    @Override
    public WithNode produce(RtsGameState gameState) {
        return new Tree(gameState);
    }

}
