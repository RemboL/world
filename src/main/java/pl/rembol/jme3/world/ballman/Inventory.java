package pl.rembol.jme3.world.ballman;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import pl.rembol.jme3.world.smallobject.tools.Tool;

public class Inventory {

    private List<Tool> tools = new ArrayList<>();

    public void add(Tool tool) {
        tools.add(tool);
    }

    public Optional<Tool> get(Class<? extends Tool> toolClass) {
        for (Tool tool : tools) {
            if (toolClass.isInstance(tool)) {
                return Optional.of(tool);
            }
        }
        return Optional.empty();
    }

    public List<String> icons() {
        return tools.stream().map(tool -> tool.iconName())
                .collect(Collectors.toList());
    }

}
