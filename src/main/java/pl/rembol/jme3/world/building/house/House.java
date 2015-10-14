package pl.rembol.jme3.world.building.house;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.save.UnitDTO;

import java.util.Arrays;
import java.util.List;

public class House extends Building {

    private HouseStatus status;

    public House(GameState gameState) {
        super(gameState);
    }

    @Override
    public String getNodeFileName() {
        return "house2/house2.scene";
    }

    @Override
    public float getHeight() {
        return 15f;
    }

    @Override
    public float getWidth() {
        return 5f;
    }

    @Override
    public String getName() {
        return "House";
    }

    @Override
    public String[] getGeometriesWithChangeableColor() {
        return new String[]{"hay"};
    }

    @Override
    public String getIconName() {
        return "house";
    }

    @Override
    public int getMaxHp() {
        return 150;
    }

    @Override
    public UnitDTO save(String key) {
        return new HouseDTO(key, this);
    }

    @Override
    public void load(UnitDTO unit) {
        if (HouseDTO.class.isInstance(unit)) {
            init(new Vector2f(unit.getPosition().x, unit.getPosition().z));
            this.setOwner(gameState.playerService.getPlayer(HouseDTO.class.cast(unit)
                    .getPlayer()));
        }
    }

    @Override
    protected List<Control> createControls() {
        return Arrays.asList(new HouseControl(gameState, this));
    }

    public HouseControl control() {
        return getNode().getControl(HouseControl.class);
    }

    @Override
    protected String[] statusLines() {
        if (control() != null && control().isRecruiting()) {
            return new String[]{
                    getName(),
                    "hp: " + getHp() + " / " + getMaxHp()
            };
        }

        return super.statusLines();
    }

    @Override
    public Node getStatusDetails() {
        if (status == null) {
            status = new HouseStatus(this, gameState);
        }

        status.update();
        return status;
    }


}
