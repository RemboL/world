package pl.rembol.jme3.world.building.warehouse;

import com.jme3.math.Vector2f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.ballman.BallMan.Hand;
import pl.rembol.jme3.world.building.Building;
import pl.rembol.jme3.world.resources.ResourceType;
import pl.rembol.jme3.world.resources.units.ResourceUnit;
import pl.rembol.jme3.world.save.UnitDTO;

public class Warehouse extends Building {

    public Warehouse(GameState gameState) {
        super(gameState);
    }

    @Override
    public String getNodeFileName() {
        return "warehouse/warehouse.scene";
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
        return "Warehouse";
    }

    @Override
    public String[] getGeometriesWithChangeableColor() {
        return new String[]{"Flag"};
    }

    @Override
    public String getIconName() {
        return "warehouse";
    }

    private void increaseResources(int resources, ResourceType resourceType) {
        if (owner != null) {
            owner.addResource(resourceType, resources);
        }
    }

    public void returnResource(BallMan ballMan) {
        if (ballMan.getWieldedObject(Hand.LEFT) instanceof ResourceUnit) {
            ResourceUnit resourceUnit = (ResourceUnit) ballMan
                    .getWieldedObject(Hand.LEFT);

            increaseResources(resourceUnit.getResourceCount(),
                    resourceUnit.getResourceType());
            ballMan.dropAndDestroy(Hand.LEFT);
        }

    }

    @Override
    public int getMaxHp() {
        return 100;
    }

    @Override
    public UnitDTO save(String key) {
        return new WarehouseDTO(key, this);
    }

    @Override
    public void load(UnitDTO unit) {
        if (WarehouseDTO.class.isInstance(unit)) {
            init(new Vector2f(unit.getPosition().x, unit.getPosition().z));
            this.setOwner(gameState.playerService.getPlayer(WarehouseDTO.class.cast(unit)
                    .getPlayer()));
        }
    }

}
