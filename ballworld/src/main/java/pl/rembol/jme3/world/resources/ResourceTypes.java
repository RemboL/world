package pl.rembol.jme3.world.resources;

import pl.rembol.jme3.rts.resources.ResourceType;

import java.util.Arrays;
import java.util.List;

public class ResourceTypes {

    public static final ResourceType FOOD = new ResourceType("food", false);
    public static final ResourceType WOOD = new ResourceType("wood", false);
    public static final ResourceType STONE = new ResourceType("stone", false);
    public static final ResourceType HOUSING = new ResourceType("housing", true);

    public static List<ResourceType> values() {
        return Arrays.asList(FOOD, WOOD, STONE, HOUSING);
    }
}
