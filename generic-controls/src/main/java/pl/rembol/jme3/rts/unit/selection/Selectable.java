package pl.rembol.jme3.rts.unit.selection;

import com.jme3.scene.Node;
import pl.rembol.jme3.rts.unit.interfaces.WithNode;

public interface Selectable extends WithNode {

    void select();

    void deselect();

    default Node getStatusDetails() {
        return null;
    }

    SelectionIcon getIcon();
}
