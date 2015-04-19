package pl.rembol.jme3.world.save;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("units")
public class UnitsDTO {
	private int idSequence;
	private List<UnitDTO> units;

	public UnitsDTO(int idSequence, List<UnitDTO> units) {
		this.idSequence = idSequence;
		this.units = units;
	}

	public List<UnitDTO> getUnits() {
		return units;
	}

	public int getIdSequence() {
		return idSequence;
	}
}
