package pl.rembol.jme3.world.save;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.interfaces.WithNode;

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
	public Class<? extends WithNode> getUnitClass() {
		return BallMan.class;
	}
}
