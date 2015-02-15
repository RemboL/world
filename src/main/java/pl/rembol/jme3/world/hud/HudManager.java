package pl.rembol.jme3.world.hud;

import java.util.List;

import pl.rembol.jme3.world.GameRunningAppState;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

public class HudManager {

	private ActionBox actionBox;
	private StatusBar statusBar;
	private SelectionBox selectionBox;

	public HudManager(Node guiNode, AppSettings settings,
			AssetManager assetManager, GameRunningAppState appState) {
		actionBox = new ActionBox(guiNode, settings, assetManager, appState);
		statusBar = new StatusBar(guiNode, settings, assetManager);
		selectionBox = new SelectionBox(guiNode, settings, assetManager);
	}

	public void setSelectionText(String text) {
		statusBar.setText(text);
	}

	public void setSelectionText(List<String> text) {
		statusBar.setText(text);

	}

	public void updateActionButtons() {
		actionBox.updateActionButtons();
	}

	public Node getActionButtonNode() {
		return actionBox.getActionButtonNode();
	}
}
