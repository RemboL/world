package pl.rembol.jme3.world.save;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;
import pl.rembol.jme3.rts.save.PlayerDTO;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.rts.save.UnitsDTO;
import pl.rembol.jme3.rts.terrain.save.AlphaMapDTO;
import pl.rembol.jme3.rts.terrain.save.TerrainDTO;
import pl.rembol.jme3.rts.terrain.save.TerrainQuadDTO;
import pl.rembol.jme3.world.building.house.HouseDTO;
import pl.rembol.jme3.world.building.warehouse.WarehouseDTO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@XStreamAlias("save")
public class SaveState {

    private TerrainDTO terrain;

    private List<PlayerDTO> players;

    private UnitsDTO units;

    public SaveState(TerrainDTO terrain, List<PlayerDTO> players, UnitsDTO units) {
        this.terrain = terrain;
        this.players = players;
        this.units = units;
    }

    private static XStream getXStream() {
        XStream xStream = new XStream(new DomDriver());
        xStream.processAnnotations(new Class[]{SaveState.class,
                TerrainDTO.class, TerrainQuadDTO.class, AlphaMapDTO.class,
                PlayerDTO.class, UnitsDTO.class, UnitDTO.class, HouseDTO.class,
                BallManDTO.class, WarehouseDTO.class, TreeDTO.class, StoneDepositDTO.class});

        return xStream;
    }

    public void save(String fileName) {
        XStream xStream = getXStream();

        try {
            File file = new File(fileName);

            xStream.toXML(this, new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SaveState load(String fileName) {
        XStream xStream = getXStream();
        xStream.processAnnotations(TerrainQuadDTO.class);

        return SaveState.class.cast(xStream.fromXML(new File(fileName)));
    }

    public TerrainDTO getTerrain() {
        return terrain;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public UnitsDTO getUnits() {
        return units;
    }

}
