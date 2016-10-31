package pl.rembol.jme3.rts.gameobjects.selection;

import com.jme3.scene.Node;
import pl.rembol.jme3.rts.gameobjects.interfaces.WithNode;

import java.util.ArrayList;
import java.util.List;

public interface Selectable extends WithNode {

    void select();

    void deselect();

    default Node getStatusDetails() {
        return null;
    }

    SelectionIcon getIcon();

    default List<String> getAvailableOrders() {
        return new ArrayList<>();
    }
}
