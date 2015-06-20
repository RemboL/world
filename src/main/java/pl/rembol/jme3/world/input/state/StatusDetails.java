package pl.rembol.jme3.world.input.state;

import java.util.ArrayList;
import java.util.List;

public class StatusDetails {

    private List<String> statusText;

    private List<String> inventoryIcons = new ArrayList<>();

    public StatusDetails(List<String> statusText) {
        this.statusText = statusText;
    }

    public StatusDetails(List<String> statusText, List<String> inventoryIcons) {
        this.statusText = statusText;
        this.inventoryIcons = inventoryIcons;
    }

    public List<String> statusText() {
        return statusText;
    }

    public List<String> inventoryIcons() {
        return inventoryIcons;
    }

}
