package pl.rembol.jme3.rts.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.jme3.math.ColorRGBA;
import pl.rembol.jme3.rts.gui.ResourcesBar;
import pl.rembol.jme3.rts.gui.console.ConsoleLog;
import pl.rembol.jme3.rts.resources.Cost;
import pl.rembol.jme3.rts.resources.ResourceType;

public class Player {

    private String name;

    private static Integer playerCounter = 0;
    private Integer id;

    protected Map<ResourceType, Integer> resources = new HashMap<>();
    protected Map<ResourceType, Integer> resourcesLimits = new HashMap<>();

    private ColorRGBA color = null;

    private boolean active = false;

    private final ResourcesBar resourcesBar;
    private final ConsoleLog consoleLog;

    public Player(ResourcesBar resourcesBar, ConsoleLog consoleLog, List<ResourceType> resourceTypeList) {
        this.resourcesBar = resourcesBar;
        this.consoleLog = consoleLog;

        id = playerCounter++;

        initResources(resourceTypeList);
    }

    private void initResources(List<ResourceType> resourceTypeList) {
        if (resourceTypeList != null) {
            for (ResourceType type : resourceTypeList) {
                resources.put(type, 0);
                if (type.isLimited()) {
                    resourcesLimits.put(type, 0);
                }
            }
        }
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
        return Optional.ofNullable(that)
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .map(Player::getId)
                .filter(this.getId()::equals)
                .isPresent();
    }

    public void addResource(ResourceType type, int count) {
        resources.put(type, resources.get(type) + count);

        updateResources();
    }

    public void setResource(ResourceType type, int count) {
        resources.put(type, count);

        updateResources();
    }

    public void setResourceLimit(ResourceType type, int count) {
        resourcesLimits.put(type, count);

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

    public void updateResources() {
        if (active) {
            resourcesBar.updateResources(resources, resourcesLimits);
        }
    }


    public int getResource(ResourceType type) {
        return resources.get(type);
    }

    public boolean availableResource(ResourceType resourceType, int count) {
        if (resourceType.isLimited()) {
            return resourcesLimits.get(resourceType) - resources.get(resourceType) >= count;
        }

        return false;
    }
}
