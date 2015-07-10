package pl.rembol.jme3.world.hud;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.world.smallobject.tools.Tool;

import com.jme3.asset.AssetManager;

@Component
public class InventoryIconCache {

    private static final long TIME_TO_CLEAR_CACHE = 5000;

    private static final long SIZE_TO_CLEAR_CACHE = 100;

    private static final long TIME_TO_LEAVE_CACHE = 30000;

    @Autowired
    private AssetManager assetManager;

    private Map<Tool, InventoryIcon> map = new HashMap<>();

    private Map<Tool, Long> lastUsed = new HashMap<>();

    private Long cacheLastCleared = System.currentTimeMillis();

    public InventoryIcon get(Tool tool) {
        clearCache();

        if (!map.containsKey(tool)) {
            map.put(tool, new InventoryIcon(tool.iconName(), assetManager));
        }

        lastUsed.put(tool, System.currentTimeMillis());
        return map.get(tool);
    }

    public void clearCache() {
        if (System.currentTimeMillis() - cacheLastCleared <= TIME_TO_CLEAR_CACHE) {
            return;
        }

        if (map.size() < SIZE_TO_CLEAR_CACHE) {
            return;
        }

        lastUsed.entrySet()
                .stream()
                .filter(entry -> System.currentTimeMillis() - entry.getValue() > TIME_TO_LEAVE_CACHE)
                .forEach(entry -> {
                    map.remove(entry.getKey());
                    lastUsed.remove(entry.getKey());
                });

        cacheLastCleared = System.currentTimeMillis();
    }

}
