package pl.rembol.jme3.world.save;

import pl.rembol.jme3.world.interfaces.WithNode;
import pl.rembol.jme3.world.resources.deposits.StoneDeposit;

import com.thoughtworks.xstream.annotations.XStreamAlias;

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
	public Class<? extends WithNode> getUnitClass() {
		return StoneDeposit.class;
	}

}
