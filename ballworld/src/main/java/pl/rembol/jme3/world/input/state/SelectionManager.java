package pl.rembol.jme3.world.input.state;

import com.jme3.math.Vector3f;
import pl.rembol.jme3.rts.events.selectionchanged.SelectionChangedEvent;
import pl.rembol.jme3.rts.player.WithOwner;
import pl.rembol.jme3.rts.unit.selection.Selectable;
import pl.rembol.jme3.world.GameState;
import pl.rembol.jme3.world.ballman.BallMan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectionManager {

    private List<Selectable> selected = new ArrayList<>();

    private GameState gameState;

    public SelectionManager(GameState gameState) {
        this.gameState = gameState;

        gameState.eventManager.onUnitDestroyed(event -> deselect(event.getUnit()));
    }

    public void select(Selectable selectable) {
        if (gameState.modifierKeysManager.isShiftPressed()) {
            switchSelection(selectable);
        } else {
            clearSelection();
            doSelect(selectable);
        }

        gameState.eventManager.sendEvent(new SelectionChangedEvent(selected));
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

        gameState.eventManager.sendEvent(new SelectionChangedEvent(selected));
    }

    private void clearSelection() {
        selected.forEach(Selectable::deselect);

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

    public void dragSelect(Vector3f dragStart, Vector3f dragStop) {

        List<Selectable> dragSelected = gameState.unitRegistry.getSelectableByPosition(
                dragStart, dragStop);

        if (gameState.modifierKeysManager.isShiftPressed()) {
            dragSelected.stream().filter(selectable -> !selected.contains(selectable)).forEach(this::doSelect);
        } else {
            clearSelection();

            List<WithOwner> activePlayerOwned = dragSelected
                    .stream()
                    .filter(WithOwner.class::isInstance)
                    .map(WithOwner.class::cast)
                    .filter(withOwner -> withOwner.getOwner().equals(
                            gameState.playerService.getActivePlayer()))
                    .collect(Collectors.toList());

            if (!activePlayerOwned.isEmpty()) {

                if (activePlayerOwned.stream().anyMatch(
                        BallMan.class::isInstance)) {
                    activePlayerOwned
                            .stream()
                            .filter(BallMan.class::isInstance)
                            .map(BallMan.class::cast)
                            .forEach(this::doSelect);
                } else {
                    activePlayerOwned.stream()
                            .map(Selectable.class::cast)
                            .forEach(this::doSelect);
                }
            }

        }

        gameState.eventManager.sendEvent(new SelectionChangedEvent(selected));
    }

    public void updateStatusIfSingleSelected(Selectable selectable) {
        if (selected.size() == 1 && selected.contains(selectable)) {
            gameState.eventManager.sendEvent(new SelectionChangedEvent(selected));
        }

    }
}
