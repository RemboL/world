package pl.rembol.jme3.world.building.toolshop;

import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.save.UnitDTO;

import com.jme3.math.Vector2f;

public class Toolshop extends Building {

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
        return new String[] { "Flag" };
    }

    @Override
    public String getIconName() {
        return "toolshop";
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
            this.setOwner(playerService.getPlayer(ToolshopDTO.class.cast(unit)
                    .getPlayer()));
        }
    }
}
