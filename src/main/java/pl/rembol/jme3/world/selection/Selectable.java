package pl.rembol.jme3.world.selection;

import pl.rembol.jme3.world.input.state.StatusDetails;
import pl.rembol.jme3.world.interfaces.WithNode;

public interface Selectable extends WithNode {

    void select();

    void deselect();

    StatusDetails getStatusDetails();

    SelectionIcon getIcon();
}
