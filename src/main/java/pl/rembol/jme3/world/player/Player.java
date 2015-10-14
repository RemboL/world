package pl.rembol.jme3.world.player;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.building.toolshop.Toolshop;
import pl.rembol.jme3.world.building.warehouse.Warehouse;
import pl.rembol.jme3.world.resources.Cost;
import pl.rembol.jme3.world.resources.ResourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static pl.rembol.jme3.world.resources.ResourceType.HOUSING;

public class Player {

    private static final int HOUSING_PER_HOUSE = 10;

    private String name;

    private static Integer playerCounter = 0;
    private Integer id;

    private Map<ResourceType, Integer> resources = new HashMap<>();
    private int resourcesHousingLimit = 0;

    private ColorRGBA color = null;

    private boolean active = false;

    private GameState gameState;

    public Player(GameState gameState) {
        this.gameState = gameState;
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }

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
        resourcesHousingLimit = gameState.unitRegistry.getHousesByOwner(this).size()
                * HOUSING_PER_HOUSE;

        updateResources();
    }

    public void updateHousing() {
        resources.put(HOUSING, gameState.unitRegistry.countHousing(this));

        updateResources();
    }

    public boolean hasResources(Cost cost) {
        for (Map.Entry<ResourceType, Integer> entry : cost.map().entrySet()) {
            if (entry.getValue() > 0
                    && resources.get(entry.getKey()) < entry.getValue()) {
                if (active) {
                    gameState.consoleLog.addLine("Not enough "
                            + entry.getKey().resourceName() + " ("
                            + entry.getValue() + " required)");
                    gameState.resourcesBar.blinkResource(entry.getKey());
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
            gameState.resourcesBar.updateResources(resources, resourcesHousingLimit);
        }
    }

    public Optional<Warehouse> getClosestWarehouse(final Vector3f location) {

        return gameState.unitRegistry
                .getWarehousesByOwner(this)
                .stream()
                .sorted((first, second) -> Float.valueOf(
                        first.getNode().getWorldTranslation()
                                .distance(location)).compareTo(
                        second.getNode().getWorldTranslation()
                                .distance(location))).findFirst();
    }

    public Optional<Toolshop> getClosestToolshop(final Vector3f location) {

        return gameState.unitRegistry
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

    public boolean availableHousing(int i) {
        return resourcesHousingLimit - resources.get(HOUSING) >= i;
    }

}
