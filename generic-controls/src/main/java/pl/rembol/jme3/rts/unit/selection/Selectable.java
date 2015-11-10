package pl.rembol.jme3.rts.unit.selection;

import java.util.ArrayList;
import java.util.List;

import com.jme3.scene.Node;
import pl.rembol.jme3.rts.unit.interfaces.WithNode;

public interface Selectable extends WithNode {

    void select();

    void deselect();

    default Node getStatusDetails() {
        return null;
    }

    SelectionIcon getIcon();

    default List<String> getAvailableOrders() { return new ArrayList<>(); }
}
