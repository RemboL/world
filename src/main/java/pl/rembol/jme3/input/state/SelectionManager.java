package pl.rembol.jme3.input.state;

import java.util.ArrayList;
import java.util.List;

import pl.rembol.jme3.world.GameRunningAppState;
import pl.rembol.jme3.world.selection.Selectable;

public class SelectionManager {

	private List<Selectable> selected = new ArrayList<>();
	private GameRunningAppState appState;

	public SelectionManager(GameRunningAppState appState) {
		this.appState = appState;
	}

	public void select(Selectable selectable) {
		if (appState.getModifierKeysManager().isControlPressed()) {
			switchSelection(selectable);
		} else {
			clearSelection();
			doSelect(selectable);
		}

		updateSelectionText();
	}

	private void updateSelectionText() {

		if (selected.size() == 0) {
			appState.getHudManager().setSelectionText("");
		} else if (selected.size() == 1) {
			appState.getHudManager().setSelectionText(
					selected.get(0).getNode().getName());
		} else {
			appState.getHudManager().setSelectionText(
					"selected " + selected.size() + " units");
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

}
