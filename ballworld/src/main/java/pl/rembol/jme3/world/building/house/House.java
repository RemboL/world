package pl.rembol.jme3.world.building.house;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.Building;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class House extends Building {

    public static final String INSIDE_DATA_KEY = "inside_data_key";

    private HouseStatus status;

    private List<BallMan> unitsInside = new ArrayList<>();

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

    public boolean enter(BallMan ballMan) {
        if (unitsInside.size() < 4) {
            unitsInside.add(ballMan);
            gameState.rootNode.detachChild(ballMan.getNode());
            gameState.unitRegistry.registerUserData(ballMan, INSIDE_DATA_KEY, this);
            gameState.selectionManager.deselect(ballMan);
            gameState.selectionManager.updateStatusIfSingleSelected(this);

            return true;
        } else {
            gameState.consoleLog.addLineExternal("House is full");
            return false;
        }
    }

    public void exit(BallMan ballMan) {
        unitsInside.remove(ballMan);
        gameState.unitRegistry.clearUserData(ballMan, INSIDE_DATA_KEY);
        gameState.rootNode.attachChild(ballMan.getNode());
        gameState.selectionManager.updateStatusIfSingleSelected(this);
    }

    public Vector2f getEnteringLocation() {
        Vector3f location = getLocation().add(getNode().getWorldRotation().toRotationMatrix().mult(new Vector3f(0f, 0f, -6f)));
        return new Vector2f(location.x, location.z);
    }

    public List<BallMan> unitsInside() {
        return unitsInside;
    }

    @Override
    protected void destroy() {
        super.destroy();

        unitsInside.forEach(this::exit);
    }

    @Override
    public List<String> getAvailableOrders() {
        if (isConstructed()) {
            return Arrays.asList("ballman");
        }
        return Collections.emptyList();
    }
}
