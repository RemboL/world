package pl.rembol.jme3.world.save;

import pl.rembol.jme3.world.Tree;
import pl.rembol.jme3.world.interfaces.WithNode;

import com.thoughtworks.xstream.annotations.XStreamAlias;

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
	public Class<? extends WithNode> getUnitClass() {
		return Tree.class;
	}

}
