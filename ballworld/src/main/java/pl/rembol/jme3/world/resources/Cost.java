package pl.rembol.jme3.world.resources;

import java.util.HashMap;
import java.util.Map;

public class Cost {

    int food = 0;
    int wood = 0;
    int stone = 0;
    int housing = 0;

    private Map<ResourceType, Integer> resources = new HashMap<>();

    public Cost of(ResourceType type, int count) {
        resources.put(type, count);
        return this;
    }

    public Cost and(ResourceType type, int count) {
        return of(type, count);
    }

    public Map<ResourceType, Integer> map() {
        return resources;
    }
}
