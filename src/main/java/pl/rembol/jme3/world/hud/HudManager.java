package pl.rembol.jme3.world.hud;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

public class HudManager {

	private ActionBox actionBox;
	private StatusBar statusBar;
	private SelectionBox selectionBox;

	public HudManager(Node guiNode, AppSettings settings,
			AssetManager assetManager) {
		actionBox = new ActionBox(guiNode, settings, assetManager);
		statusBar = new StatusBar(guiNode, settings, assetManager);
		selectionBox = new SelectionBox(guiNode, settings, assetManager);
	}

	public void setSelectionText(String text) {
		selectionBox.setText(text);
	}
}
