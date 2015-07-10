package pl.rembol.jme3.world.input.state;

import java.util.ArrayList;
import java.util.List;

import pl.rembol.jme3.world.smallobject.tools.Tool;

public class StatusDetails {

    private List<String> statusText;

    private List<Tool> inventory = new ArrayList<>();

    public StatusDetails(List<String> statusText) {
        this.statusText = statusText;
    }

    public StatusDetails(List<String> statusText, List<Tool> inventoryIcons) {
        this.statusText = statusText;
        this.inventory = inventoryIcons;
    }

    public List<String> statusText() {
        return statusText;
    }

    public List<Tool> inventory() {
        return inventory;
    }

}
