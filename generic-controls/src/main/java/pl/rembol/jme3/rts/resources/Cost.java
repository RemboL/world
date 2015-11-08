package pl.rembol.jme3.rts.resources;

import java.util.HashMap;
import java.util.Map;

public class Cost {

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
