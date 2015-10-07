package pl.rembol.jme3.world.input.state;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.ConstructionSite;
import pl.rembol.jme3.world.building.house.House;
import pl.rembol.jme3.world.hud.ActionBox;
import pl.rembol.jme3.world.hud.status.StatusBar;
import pl.rembol.jme3.world.input.ModifierKeysManager;
import pl.rembol.jme3.world.player.PlayerService;
import pl.rembol.jme3.world.player.WithOwner;
import pl.rembol.jme3.world.selection.Selectable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SelectionManager {

    public static enum SelectionType {
        UNIT, HOUSE;
    }

    private List<Selectable> selected = new ArrayList<>();

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ActionBox actionBox;

    @Autowired
    private StatusBar statusBar;

    @Autowired
    private ModifierKeysManager modifierKeysManager;

    @Autowired
    private UnitRegistry gameState;

    public void select(Selectable selectable) {
        if (modifierKeysManager.isShiftPressed()) {
            switchSelection(selectable);
        } else {
            clearSelection();
            doSelect(selectable);
        }

        updateSelection();
        actionBox.updateActionButtons();
    }

    public void updateSelection() {

        if (selected.size() == 0) {
            statusBar.clear();
        } else if (selected.size() == 1) {
            Node node = selected.get(0).getStatusDetails();
            if (node != null) {
                statusBar.setStatusDetails(node);
            }
        } else {
            statusBar.setIcons(selected);
        }

    }

    private void switchSelection(Selectable selectable) {
        if (selected.contains(selectable)) {
            doDeselect(selectable);
        } else {
            doSelect(selectable);
        }
    }

    public void deselect(Selectable selectable) {
        doDeselect(selectable);

        updateSelection();
        actionBox.updateActionButtons();
    }

    private void clearSelection() {
        for (Selectable previouslySelected : selected) {
            previouslySelected.deselect();
        }

        selected.clear();
    }

    private void doDeselect(Selectable selectable) {
        selected.remove(selectable);
        selectable.deselect();
    }

    private void doSelect(Selectable selectable) {
        selected.add(selectable);
        selectable.select();
    }

    public List<Selectable> getSelected() {
        return selected;
    }

    public SelectionType getSelectionType() {
        if (!selected.isEmpty()) {
            if (selected.stream().allMatch(
                    selectedUnit -> BallMan.class.isInstance(selectedUnit)
                            && isOwnedByActivePlayer(selectedUnit))) {
                return SelectionType.UNIT;
            }

            if (selected.stream().allMatch(
                    selectedUnit -> House.class.isInstance(selectedUnit)
                            && isOwnedByActivePlayer(selectedUnit)
                            && isNotUnderConstruction(selectedUnit))) {
                return SelectionType.HOUSE;
            }
        }

        return null;
    }

    private boolean isNotUnderConstruction(Selectable selectedUnit) {
        return selectedUnit.getNode().getControl(ConstructionSite.class) == null;
    }

    private boolean isOwnedByActivePlayer(Selectable selectedUnit) {
        return WithOwner.class.cast(selectedUnit).getOwner()
                .equals(playerService.getActivePlayer());
    }

    public void dragSelect(Vector3f dragStart, Vector3f dragStop) {

        List<Selectable> dragSelected = gameState.getSelectableByPosition(
                dragStart, dragStop);

        if (modifierKeysManager.isShiftPressed()) {
            for (Selectable selectable : dragSelected) {
                if (!selected.contains(selectable)) {
                    doSelect(selectable);
                }
            }
        } else {
            clearSelection();

            List<WithOwner> activePlayerOwned = dragSelected
                    .stream()
                    .filter(selectable -> WithOwner.class
                            .isInstance(selectable))
                    .map(selectable -> WithOwner.class.cast(selectable))
                    .filter(withOwner -> withOwner.getOwner().equals(
                            playerService.getActivePlayer()))
                    .collect(Collectors.toList());

            if (!activePlayerOwned.isEmpty()) {

                if (activePlayerOwned.stream().anyMatch(
                        withOwner -> BallMan.class.isInstance(withOwner))) {
                    activePlayerOwned
                            .stream()
                            .filter(withOwner -> BallMan.class
                                    .isInstance(withOwner))
                            .map(withOwner -> BallMan.class.cast(withOwner))
                            .forEach(ballMan -> doSelect(ballMan));
                } else {
                    activePlayerOwned.stream()
                            .map(withOwner -> Selectable.class.cast(withOwner))
                            .forEach(selectable -> doSelect(selectable));
                }
            }

        }

        updateSelection();
        actionBox.updateActionButtons();
    }

    public void updateStatusIfSingleSelected(Selectable selectable) {
        if (selected.size() == 1 && selected.contains(selectable)) {
            updateSelection();
        }

    }
}
