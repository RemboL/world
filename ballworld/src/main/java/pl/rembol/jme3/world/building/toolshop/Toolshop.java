package pl.rembol.jme3.world.building.toolshop;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.rts.GameState;
import pl.rembol.jme3.rts.save.UnitDTO;
import pl.rembol.jme3.world.building.Building;

public class Toolshop extends Building {

    public Toolshop(GameState gameState) {
        super(gameState);
    }

    @Override
    public String getNodeFileName() {
        return "toolshop/toolshop.scene";
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
        return "Tool Shop";
    }

    @Override
    public String[] getGeometriesWithChangeableColor() {
        return new String[]{"Flag"};
    }

    @Override
    public int getMaxHp() {
        return 75;
    }

    @Override
    public UnitDTO save(String key) {
        return new ToolshopDTO(key, this);
    }

    @Override
    public void load(UnitDTO unit) {
        if (ToolshopDTO.class.isInstance(unit)) {
            init(new Vector2f(unit.getPosition().x, unit.getPosition().z));
            this.setOwner(gameState.playerService.getPlayer(ToolshopDTO.class.cast(unit)
                    .getPlayer()));
        }
    }
}
