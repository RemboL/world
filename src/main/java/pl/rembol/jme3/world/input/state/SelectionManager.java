package pl.rembol.jme3.world.input.state;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.UnitRegistry;
import pl.rembol.jme3.world.ballman.BallMan;
import pl.rembol.jme3.world.building.ConstructionSite;
import pl.rembol.jme3.world.house.House;
import pl.rembol.jme3.world.hud.ActionBox;
import pl.rembol.jme3.world.hud.StatusBar;
import pl.rembol.jme3.world.input.ModifierKeysManager;
import pl.rembol.jme3.world.player.WithOwner;
import pl.rembol.jme3.world.selection.Selectable;

import com.jme3.math.Vector3f;

@Component
public class SelectionManager {

	public static enum SelectionType {
		UNIT, HOUSE;
	}

	private List<Selectable> selected = new ArrayList<>();

	@Autowired
	private GameRunningAppState appState;

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

		updateSelectionText();
		actionBox.updateActionButtons();
	}

	public void updateSelectionText() {

		if (selected.size() == 0) {
			statusBar.setText("");
		} else if (selected.size() == 1) {
			statusBar.setText(selected.get(0).getStatusText());
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

		updateSelectionText();
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
				.equals(appState.getActivePlayer());
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
							appState.getActivePlayer()))
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

		updateSelectionText();
		actionBox.updateActionButtons();
	}
}
