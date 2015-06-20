package pl.rembol.jme3.world.building.house;

import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.save.UnitDTO;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

public class House extends Building {

    @Override
    public Node initNode() {
        return (Node) assetManager.loadModel("house2/house2.scene");
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
        return new String[] { "hay" };
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
            this.setOwner(playerService.getPlayer(HouseDTO.class.cast(unit)
                    .getPlayer()));
        }
    }
}
