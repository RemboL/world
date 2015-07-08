package pl.rembol.jme3.world.player;

import static pl.rembol.jme3.world.resources.ResourceType.HOUSING;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.building.toolshop.Toolshop;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.hud.ConsoleLog;
import pl.rembol.jme3.world.hud.ResourcesBar;
import pl.rembol.jme3.world.resources.Cost;
import pl.rembol.jme3.world.resources.ResourceType;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

public class Player {

    private static final int HOUSING_PER_HOUSE = 10;

    private String name;

    private static Integer playerCounter = 0;
    private Integer id;

    // private int resourcesFood = 0;
    // private int resourcesWood = 0;
    // private int resourcesStone = 0;
    // private int resourcesHousing = 0;
    private Map<ResourceType, Integer> resources = new HashMap<>();
    private int resourcesHousingLimit = 0;

    private ColorRGBA color = null;

    private boolean active = false;

    @Autowired
    private ResourcesBar resourcesBar;

    @Autowired
    private ConsoleLog consoleLog;

    @Autowired
    private UnitRegistry gameState;

    public Player() {
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
    }

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

    public void addResource(ResourceType type, int count) {
        resources.put(type, resources.get(type) + count);

        updateResources();
    }

    public void updateHousingLimit() {
        resourcesHousingLimit = gameState.getHousesByOwner(this).size()
                * HOUSING_PER_HOUSE;

        updateResources();
    }

    public void updateHousing() {
        resources.put(HOUSING, gameState.getBallMenByOwner(this).size());

        updateResources();
    }

    public boolean hasResources(Cost cost) {
        for (Map.Entry<ResourceType, Integer> entry : cost.map().entrySet()) {
            if (entry.getValue() > 0
                    && resources.get(entry.getKey()) < entry.getValue()) {
                if (active) {
                    consoleLog.addLine("Not enough "
                            + entry.getKey().resourceName() + " ("
                            + entry.getValue() + " required)");
                    resourcesBar.blinkResource(entry.getKey());
                }
                return false;
            }
        }
        return true;
    }

    public boolean retrieveResources(Cost cost) {
        if (hasResources(cost)) {
            for (Map.Entry<ResourceType, Integer> entry : cost.map().entrySet()) {
                resources.put(entry.getKey(), resources.get(entry.getKey())
                        - entry.getValue());
            }

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
            resourcesBar.updateResources(resources, resourcesHousingLimit);
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

    public Optional<Toolshop> getClosestToolshop(final Vector3f location) {

        return gameState
                .getToolshopsByOwner(this)
                .stream()
                .sorted((first, second) -> Float.valueOf(
                        first.getNode().getWorldTranslation()
                                .distance(location)).compareTo(
                        second.getNode().getWorldTranslation()
                                .distance(location))).findFirst();
    }

    public int getResource(ResourceType type) {
        return resources.get(type);
    }

}
