package pl.rembol.jme3.world.player;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.hud.ConsoleLog;
import pl.rembol.jme3.world.hud.ResourcesBar;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

public class Player {

    private static final int HOUSING_PER_HOUSE = 10;

    private String name;

    private static Integer playerCounter = 0;
    private Integer id;

    private int resourcesWood = 0;
    private int resourcesStone = 0;
    private int resourcesHousing = 0;
    private int resourcesHousingLimit = 0;

    private ColorRGBA color = null;

    private boolean active = false;

    @Autowired
    private ResourcesBar resourcesBar;

    @Autowired
    private ConsoleLog consoleLog;

    @Autowired
    private UnitRegistry gameState;

    @PostConstruct
    public void init() {
        id = playerCounter++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public ColorRGBA getColor() {
        return color;
    }

    @Override
    public boolean equals(Object that) {
        if (!Player.class.isInstance(that)) {
            return false;
        }

        return Player.class.cast(that).getId().equals(this.getId());
    }

    public int getResourcesWood() {
        return resourcesWood;
    }

    public int getResourcesStone() {
        return resourcesStone;
    }

    public int getResourcesHousing() {
        return resourcesHousing;
    }

    public int getResourcesHousingLimit() {
        return resourcesHousingLimit;
    }

    public void addWood(int wood) {
        resourcesWood += wood;

        updateResources();
    }

    public void addStone(int stone) {
        resourcesStone += stone;

        updateResources();
    }

    public void updateHousingLimit() {
        resourcesHousingLimit = gameState.getHousesByOwner(this).size()
                * HOUSING_PER_HOUSE;

        updateResources();
    }

    public void updateHousing() {
        resourcesHousing = gameState.getBallMenByOwner(this).size();

        updateResources();
    }

    public boolean hasResources(int wood, int stone, int housing) {
        if (wood > 0 && resourcesWood < wood) {
            if (active) {
                consoleLog.addLine("Not enough wood");
                resourcesBar.blinkWood();
            }
            return false;
        }
        
        if (stone > 0 && resourcesStone < stone) {
            if (active) {
                consoleLog.addLine("Not enough stone");
                resourcesBar.blinkStone();
            }
            return false;
        }

        if (housing > 0 && resourcesHousingLimit - resourcesHousing < housing) {
            if (active) {
                consoleLog.addLine("Not enough housing");
                resourcesBar.blinkHousing();
            }
            return false;
        }

        return true;
    }

    public boolean retrieveResources(int wood, int stone, int housing) {
        if (hasResources(wood, stone, housing)) {
            resourcesWood -= wood;
            resourcesStone -= stone;
            resourcesHousing += housing;

            updateResources();

            return true;
        } else {
            return false;
        }
    }

    public void setActive(boolean active) {
        this.active = active;

        updateResources();
    }

    public boolean isActive() {
        return active;
    }

    private void updateResources() {
        if (active) {
            resourcesBar.updateResources(resourcesWood, resourcesStone, resourcesHousing,
                    resourcesHousingLimit);
        }
    }

    public Optional<Warehouse> getClosestWarehouse(final Vector3f location) {

        return gameState
                .getWarehousesByOwner(this)
                .stream()
                .sorted((first, second) -> Float.valueOf(
                        first.getNode().getWorldTranslation()
                                .distance(location)).compareTo(
                        second.getNode().getWorldTranslation()
                                .distance(location))).findFirst();

    }
}
